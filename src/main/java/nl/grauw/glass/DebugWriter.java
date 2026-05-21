package nl.grauw.glass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.PrintStream;
import java.nio.file.Path;

import nl.grauw.glass.SourceFile.SourceFileSpan;
import nl.grauw.glass.expressions.Annotation;
import nl.grauw.glass.expressions.BinaryOperator;
import nl.grauw.glass.expressions.EvaluationException;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Group;
import nl.grauw.glass.expressions.Identifier;
import nl.grauw.glass.expressions.IfElse;
import nl.grauw.glass.expressions.Index;
import nl.grauw.glass.expressions.Member;
import nl.grauw.glass.expressions.Register;
import nl.grauw.glass.expressions.UnaryOperator;
import nl.grauw.glass.instructions.InstructionObject;

public class DebugWriter {

	private final PrintStream output;

	public DebugWriter(PrintStream output) {
		this.output = output;
	}

	public void write(Source source) {
        output.println("; LABELS\n");
		writeLabels(source);
        output.println("\n; MAP\n");
		writeMap(source);
        output.println("\n; USAGES\n");
		writeUsages(source);
	}

	private void writeLabels(Source source) {
		for (Line line : source.getLines()) {
			writeLineLabels(source, line);
			writeNestedLabels(line);
		}
	}

	private void writeLineLabels(Source source, Line line) {
		for (String label : line.getLabels()) {
			output.print(getLabelName(source, line, label));
			output.print(": ");
			output.print(getPath(line.getSourceSpan()));
			output.print(",");
			output.print(line.getSourceSpan().lineStart + 1);
			output.print(",");
			output.print(getType(line));
			writeExtraFields(line, label);
			output.println();
		}
	}

	private String getLabelName(Source source, Line line, String label) {
		return line.getScope().getParent() != source.getScope() && !label.startsWith(".") ? "." + label : label;
	}

	private void writeExtraFields(Line line, String label) {
		if (isEqu(line)) {
			output.print("," + getEquValue(line, label));
		} else {
			output.print("," + getAddress(line));
		}
	}

	private String getEquValue(Line line, String label) {
		try {
			return Integer.toString(line.getScope().getParent().getSymbol(label).getInteger());
		} catch (EvaluationException e) {
			return line.getArguments().toDebugString();
		}
	}

	private String getAddress(Line line) {
		return Integer.toString(line.getScope().getAddress().getInteger());
	}

	private void writeNestedLabels(Line line) {
		InstructionObject instructionObject = line.getInstructionObject();
		if (instructionObject instanceof nl.grauw.glass.instructions.If.IfObject) {
			writeLabels(((nl.grauw.glass.instructions.If.IfObject)instructionObject).getSelectedSource());
		}

		if (line.getInstruction() instanceof nl.grauw.glass.instructions.Section) {
			writeLabels(((nl.grauw.glass.instructions.Section)line.getInstruction()).getSource());
		}
	}

	private String getPath(SourceFileSpan sourceSpan) {
		Path path = sourceSpan.getSourceFile().getPath();
		return path != null ? path.toAbsolutePath().normalize().toString() : "null";
	}

	private String getType(Line line) {
		if (isEqu(line))
			return "EQU";
		String mnemonic = line.getMnemonic();
		if ("ds".equals(mnemonic) || "DS".equals(mnemonic))
			return "DS";
		if ("db".equals(mnemonic) || "DB".equals(mnemonic) ||
				"dw".equals(mnemonic) || "DW".equals(mnemonic) ||
				"dd".equals(mnemonic) || "DD".equals(mnemonic) ||
				"mz_asc".equals(mnemonic) || "MZ_ASC".equals(mnemonic) ||
				"mz_disp".equals(mnemonic) || "MZ_DISP".equals(mnemonic))
			return "DC";
		return "LABEL";
	}

	private boolean isEqu(Line line) {
		String mnemonic = line.getMnemonic();
		return "equ".equals(mnemonic) || "EQU".equals(mnemonic);
	}

	private void writeMap(Source source) {
		List<Range> ranges = new ArrayList<>();
		collectRanges(source, ranges);
		for (Range range : mergeRanges(splitRanges(ranges))) {
			output.print(range.start);
			output.print(":");
			output.print(range.end);
			output.print(":");
			output.println(range.type);
		}
	}

	private void collectRanges(Source source, List<Range> ranges) {
		for (Line line : source.getLines()) {
			Range range = getRange(line);
			if (range != null)
				ranges.add(range);
			collectNestedRanges(line, ranges);
		}
	}

	private void collectNestedRanges(Line line, List<Range> ranges) {
		InstructionObject instructionObject = line.getInstructionObject();
		if (instructionObject instanceof nl.grauw.glass.instructions.If.IfObject) {
			collectRanges(((nl.grauw.glass.instructions.If.IfObject)instructionObject).getSelectedSource(), ranges);
		}

		if (line.getInstruction() instanceof nl.grauw.glass.instructions.Section) {
			collectRanges(((nl.grauw.glass.instructions.Section)line.getInstruction()).getSource(), ranges);
		}
	}

	private Range getRange(Line line) {
		int start = line.getScope().getAddress().getInteger();
		int size;
		try {
			size = line.getSize().getInteger();
		} catch (AssemblyException e) {
			return null;
		}
		if (size <= 0)
			return null;

		String type = getMapType(line);
		if (type == null)
			return null;

		return new Range(start, start + size, type);
	}

	private String getMapType(Line line) {
		String mnemonic = line.getMnemonic();
		if ("db".equals(mnemonic) || "DB".equals(mnemonic) ||
				"dw".equals(mnemonic) || "DW".equals(mnemonic) ||
				"dd".equals(mnemonic) || "DD".equals(mnemonic) ||
				"mz_asc".equals(mnemonic) || "MZ_ASC".equals(mnemonic) ||
				"mz_disp".equals(mnemonic) || "MZ_DISP".equals(mnemonic) ||
				"ds".equals(mnemonic) || "DS".equals(mnemonic))
			return "data";
		if (line.getInstructionObject() != null)
			return "code";
		return null;
	}

	private List<Range> splitRanges(List<Range> ranges) {
		List<Integer> boundaries = new ArrayList<>();
		for (Range range : ranges) {
			boundaries.add(range.start);
			boundaries.add(range.end);
		}
		Collections.sort(boundaries);

		List<Range> splitRanges = new ArrayList<>();
		for (int i = 1; i < boundaries.size(); i++) {
			int start = boundaries.get(i - 1);
			int end = boundaries.get(i);
			if (start == end)
				continue;

			String type = getCoveringType(ranges, start, end);
			if (type != null)
				splitRanges.add(new Range(start, end, type));
		}
		return splitRanges;
	}

	private String getCoveringType(List<Range> ranges, int start, int end) {
		String type = null;
		for (Range range : ranges) {
			if (range.start <= start && range.end >= end) {
				if ("code".equals(range.type))
					return "code";
				type = range.type;
			}
		}
		return type;
	}

	private List<Range> mergeRanges(List<Range> ranges) {
		Collections.sort(ranges, Comparator.comparingInt(range -> range.start));

		List<Range> mergedRanges = new ArrayList<>();
		for (Range range : ranges) {
			Range previous = !mergedRanges.isEmpty() ? mergedRanges.get(mergedRanges.size() - 1) : null;
			if (previous != null && previous.end == range.start && previous.type.equals(range.type)) {
				previous.end = range.end;
			} else {
				mergedRanges.add(range);
			}
		}
		return mergedRanges;
	}

	private static class Range {
		final int start;
		int end;
		final String type;

		Range(int start, int end, String type) {
			this.start = start;
			this.end = end;
			this.type = type;
		}
	}

	private void writeUsages(Source source) {
		Set<String> labelNames = new LinkedHashSet<>();
		collectLabelNames(source, labelNames);

		Map<String, LinkedHashSet<Integer>> usages = new LinkedHashMap<>();
		collectUsages(source, labelNames, usages);

		for (Map.Entry<String, LinkedHashSet<Integer>> entry : usages.entrySet()) {
			output.print(entry.getKey());
			output.print(":");
			boolean first = true;
			for (Integer address : entry.getValue()) {
				if (!first)
					output.print(",");
				output.print(address);
				first = false;
			}
			output.println();
		}
	}

	private void collectLabelNames(Source source, Set<String> labelNames) {
		for (Line line : source.getLines()) {
			for (String label : line.getLabels())
				labelNames.add(getLabelName(source, line, label));
			collectNestedLabelNames(line, labelNames);
		}
	}

	private void collectNestedLabelNames(Line line, Set<String> labelNames) {
		InstructionObject instructionObject = line.getInstructionObject();
		if (instructionObject instanceof nl.grauw.glass.instructions.If.IfObject)
			collectLabelNames(((nl.grauw.glass.instructions.If.IfObject)instructionObject).getSelectedSource(), labelNames);

		if (line.getInstruction() instanceof nl.grauw.glass.instructions.Section)
			collectLabelNames(((nl.grauw.glass.instructions.Section)line.getInstruction()).getSource(), labelNames);
	}

	private void collectUsages(Source source, Set<String> labelNames, Map<String, LinkedHashSet<Integer>> usages) {
		for (Line line : source.getLines()) {
			collectLineUsages(source, line, labelNames, usages);
			collectNestedUsages(line, labelNames, usages);
		}
	}

	private void collectLineUsages(Source source, Line line, Set<String> labelNames, Map<String, LinkedHashSet<Integer>> usages) {
		if (line.getArguments() == null || isEqu(line))
			return;

		LinkedHashSet<String> lineUsages = new LinkedHashSet<>();
		collectExpressionUsages(line.getArguments(), source, line, labelNames, lineUsages);
		for (String labelName : lineUsages)
			usages.computeIfAbsent(labelName, key -> new LinkedHashSet<>()).add(line.getScope().getAddress().getInteger());
	}

	private void collectNestedUsages(Line line, Set<String> labelNames, Map<String, LinkedHashSet<Integer>> usages) {
		InstructionObject instructionObject = line.getInstructionObject();
		if (instructionObject instanceof nl.grauw.glass.instructions.If.IfObject)
			collectUsages(((nl.grauw.glass.instructions.If.IfObject)instructionObject).getSelectedSource(), labelNames, usages);

		if (line.getInstruction() instanceof nl.grauw.glass.instructions.Section)
			collectUsages(((nl.grauw.glass.instructions.Section)line.getInstruction()).getSource(), labelNames, usages);
	}

	private void collectExpressionUsages(Expression expression, Source source, Line line, Set<String> labelNames, Set<String> lineUsages) {
		if (expression instanceof Identifier) {
			addUsage((Identifier)expression, source, line, labelNames, lineUsages);
		} else if (expression instanceof BinaryOperator) {
			BinaryOperator binaryOperator = (BinaryOperator)expression;
			collectExpressionUsages(binaryOperator.getTerm1(), source, line, labelNames, lineUsages);
			collectExpressionUsages(binaryOperator.getTerm2(), source, line, labelNames, lineUsages);
		} else if (expression instanceof UnaryOperator) {
			collectExpressionUsages(((UnaryOperator)expression).getTerm(), source, line, labelNames, lineUsages);
		} else if (expression instanceof Group) {
			collectExpressionUsages(((Group)expression).getTerm(), source, line, labelNames, lineUsages);
		} else if (expression instanceof Annotation) {
			collectExpressionUsages(((Annotation)expression).getAnnotee(), source, line, labelNames, lineUsages);
		} else if (expression instanceof IfElse) {
			IfElse ifElse = (IfElse)expression;
			collectExpressionUsages(ifElse.getCondition(), source, line, labelNames, lineUsages);
			collectExpressionUsages(ifElse.getTrueTerm(), source, line, labelNames, lineUsages);
			collectExpressionUsages(ifElse.getFalseTerm(), source, line, labelNames, lineUsages);
		} else if (expression instanceof Index) {
			Index index = (Index)expression;
			collectExpressionUsages(index.getSequence(), source, line, labelNames, lineUsages);
			collectExpressionUsages(index.getIndex(), source, line, labelNames, lineUsages);
		} else if (expression instanceof Member) {
			Member member = (Member)expression;
			collectExpressionUsages(member.getObject(), source, line, labelNames, lineUsages);
			collectExpressionUsages(member.getSubject(), source, line, labelNames, lineUsages);
		} else if (expression instanceof Register) {
			collectRegisterUsages((Register)expression, source, line, labelNames, lineUsages);
		}
	}

	private void collectRegisterUsages(Register register, Source source, Line line, Set<String> labelNames, Set<String> lineUsages) {
		if (!register.isIndex())
			return;
		try {
			collectExpressionUsages(register.getIndexOffset(), source, line, labelNames, lineUsages);
		} catch (EvaluationException e) {
			// Not an index register pair with an offset.
		}
	}

	private void addUsage(Identifier identifier, Source source, Line line, Set<String> labelNames, Set<String> lineUsages) {
		String labelName = getUsageName(source, line, identifier.getName());
		if (labelNames.contains(labelName))
			lineUsages.add(labelName);
	}

	private String getUsageName(Source source, Line line, String name) {
		if (line.getScope().getParent() != source.getScope() &&
				line.getScope().getParent().hasLocalSymbol(name) &&
				!name.startsWith("."))
			return "." + name;
		return name;
	}

}