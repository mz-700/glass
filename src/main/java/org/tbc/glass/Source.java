package org.tbc.glass;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;

public class Source {

	private final Scope scope;
	private final MzfConfig mzfConfig = new MzfConfig();
	private List<Line> lines = new ArrayList<>();

	public Source(Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return scope;
	}

	public List<Line> getLines() {
		return lines;
	}

	public MzfConfig getMzfConfig() {
		return mzfConfig;
	}

	public Line getLastLine() {
		return !lines.isEmpty() ? lines.getLast() : null;
	}

	public Source copy(Scope scope) {
		Source newSource = new Source(scope);
		for (Line line : lines)
			newSource.addLine(line.copy(new Scope(scope)));
		return newSource;
	}

	public void addLine(Line line) {
		lines.add(line);
	}

	public void addLines(List<Line> lines) {
		this.lines.addAll(lines);
	}

	public void assemble(OutputStream output) throws IOException {
		register();
		expand();
		resolve();

		byte[] objectCode = getBytes();
		output.write(objectCode, 0, objectCode.length);
	}

	public void register() {
		register(scope);
	}

	public void register(Scope scope) {
		for (Line line : lines)
			line.register(scope);
	}

	public void expand() {
		List<Line> newLines = new ArrayList<>();
		expand(newLines);
		lines = newLines;
	}

	public void expand(List<Line> newLines) {
		for (Line line : lines)
			line.expand(newLines);
	}

	public Expression resolve() {
		return resolve(IntegerLiteral.ZERO);
	}

	public Expression resolve(Expression address) {
		Expression physicalAddress = address;
		Expression phaseAddress = null;
		Expression phaseStartAddress = null;
		Integer phaseSizeLimit = null;
		Line phaseLine = null;
		for (Line line : lines)
		{
			Expression activeAddress = phaseAddress != null ? phaseAddress : physicalAddress;
			Expression nextAddress = line.resolve(activeAddress);
			updateMzfConfig(line);

			if (line.getInstructionObject() instanceof org.tbc.glass.instructions.Phase.PhaseObject) {
				if (phaseAddress != null) {
					AssemblyException exception = new AssemblyException("Already in phase.");
					exception.addContext(line.getSourceSpan());
					throw exception;
				}
				phaseAddress = nextAddress;
				phaseStartAddress = nextAddress;
				org.tbc.glass.instructions.Phase.PhaseObject phaseObject =
					( org.tbc.glass.instructions.Phase.PhaseObject)line.getInstructionObject();
				phaseSizeLimit = phaseObject.hasSize() ? phaseObject.getSizeLimit() : null;
				checkPhaseRange(phaseStartAddress, phaseSizeLimit, line);
				phaseLine = line;
			} else if (line.getInstructionObject() instanceof org.tbc.glass.instructions.Dephase.DephaseObject) {
				if (phaseAddress == null) {
					AssemblyException exception = new AssemblyException("Dephase without phase.");
					exception.addContext(line.getSourceSpan());
					throw exception;
				}
				phaseAddress = null;
				phaseStartAddress = null;
				phaseSizeLimit = null;
				phaseLine = null;
			} else if (line.getInstructionObject() instanceof org.tbc.glass.instructions.Org.Org_N) {
				if (phaseAddress != null) {
					phaseAddress = nextAddress;
					checkPhaseSize(phaseStartAddress, phaseAddress, phaseSizeLimit, line);
				} else {
					physicalAddress = nextAddress;
				}
			} else if (phaseAddress != null) {
				phaseAddress = nextAddress;
				checkPhaseSize(phaseStartAddress, phaseAddress, phaseSizeLimit, line);
			} else {
				physicalAddress = nextAddress;
			}
		}
		if (phaseAddress != null) {
			AssemblyException exception = new AssemblyException("Phase without dephase.");
			exception.addContext(phaseLine.getSourceSpan());
			throw exception;
		}
		return physicalAddress;
	}

	private void checkPhaseSize(Expression phaseStartAddress, Expression phaseAddress, Integer phaseSizeLimit, Line line) {
		if (phaseSizeLimit != null && phaseAddress.getInteger() - phaseStartAddress.getInteger() > phaseSizeLimit) {
			AssemblyException exception = new AssemblyException("Phase size exceeded.");
			exception.addContext(line.getSourceSpan());
			throw exception;
		}
	}

	private void checkPhaseRange(Expression phaseStartAddress, Integer phaseSizeLimit, Line line) {
		if (phaseSizeLimit != null && phaseStartAddress.getInteger() + phaseSizeLimit > 0x10000) {
			AssemblyException exception = new AssemblyException("Phase range exceeds 64KB address space.");
			exception.addContext(line.getSourceSpan());
			throw exception;
		}
	}

	private void updateMzfConfig(Line line) {
		String mnemonic = line.getMnemonic();
		if ("mzf_title".equals(mnemonic) || "MZF_TITLE".equals(mnemonic)) {
			mzfConfig.title = line.getArguments().getString();
		} else if ("mzf_comments".equals(mnemonic) || "MZF_COMMENTS".equals(mnemonic)) {
			mzfConfig.comments = line.getArguments().getString();
		} else if ("mzf_load".equals(mnemonic) || "MZF_LOAD".equals(mnemonic)) {
			mzfConfig.loadAddress = line.getArguments().getInteger();
		} else if ("mzf_start".equals(mnemonic) || "MZF_START".equals(mnemonic)) {
			mzfConfig.startAddress = line.getArguments().getInteger();
		}
	}

	public byte[] getBytes() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		for (Line line : lines)
		{
			byte[] object = line.getBytes();
			bytes.write(object, 0, object.length);
		}
		return bytes.toByteArray();
	}

	public String toString() {
		StringBuilder string = new StringBuilder();
		for (Line line : lines) {
			string.append(line);
			string.append('\n');
		}
		return string.toString();
	}

	public static class MzfConfig {
		private String title = "TITLE";
		private String comments = "";
		private Integer loadAddress;
		private Integer startAddress;

		public String getTitle() {
			return title;
		}

		public String getComments() {
			return comments;
		}

		public boolean hasLoadAddress() {
			return loadAddress != null;
		}

		public int getLoadAddress() {
			return loadAddress;
		}

		public int getStartAddress() {
			return startAddress != null ? startAddress : loadAddress;
		}
	}

}