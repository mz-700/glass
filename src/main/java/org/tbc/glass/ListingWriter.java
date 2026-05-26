package org.tbc.glass;

import java.io.PrintStream;

import org.tbc.glass.SourceFile.SourceFileSpan;
import org.tbc.glass.instructions.Ds;
import org.tbc.glass.instructions.MacroInstruction;
import org.tbc.glass.instructions.Section;

public class ListingWriter {

	private final PrintStream output;
	private SourceFile sourceFile;

	public ListingWriter(PrintStream output) {
		this.output = output;
		this.sourceFile = null;
	}

	public void write(Source source) {
		for (Line line : source.getLines()) {
			if (line.getInstructionObject() instanceof org.tbc.glass.instructions.If.IfObject) {
				write((( org.tbc.glass.instructions.If.IfObject)line.getInstructionObject()).getSelectedSource() );
				continue;
			}
			write(line);
			if (line.getInstruction() instanceof Ds) {
				for (Section section : ((Ds)line.getInstruction()).getSections()) {
					write(section.getSource());
				}
			}
		}
	}

	public void write(Line line) {
		SourceFileSpan span = line.getSourceSpan();
		if (sourceFile != span.getSourceFile()) {
			sourceFile = span.getSourceFile();
			output.println("# source: " + sourceFile.getPath());
		}

		int lineEnd = span.lineEnd + (span.lineStart == span.lineEnd ? 1 : 0);
		for (int lineNumber = span.lineStart; lineNumber < lineEnd; lineNumber++) {
			output.format("% 4d\t%04X\t", lineNumber + 1, line.getScope().getAddress().getInteger());
			if (lineNumber == lineEnd - 1) {
				writeObjectColumn(line);
			} else {
				output.print("\t\t\t\t");
			}
			if (lineNumber < span.lineEnd)
				output.println(isExpandedLine(line) ? line.toListingString() : sourceFile.getLine(lineNumber));
			else
				output.println();
		}
	}

	private void writeObjectColumn(Line line) {
		if (line.getInstruction() instanceof MacroInstruction) {
			output.print("MACRO\t\t\t\t");
			return;
		}

		byte[] bytes = line.getBytes();
		for (byte b : bytes)
			output.format("%02X\t", b);
		for (int i = bytes.length; i < 4; i++)
			output.print("\t");
	}

	private boolean isExpandedLine(Line line) {
		Scope scope = line.getScope();
		while (scope != null) {
			if (scope instanceof ParameterScope)
				return true;
			scope = scope.getParent();
		}
		return false;
	}

}