package org.tbc.glass.instructions;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Annotation;
import org.tbc.glass.expressions.BinaryOperator;
import org.tbc.glass.expressions.EvaluationException;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Group;
import org.tbc.glass.expressions.Identifier;
import org.tbc.glass.expressions.IfElse;
import org.tbc.glass.expressions.Index;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Member;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.UnaryOperator;

public class Rel extends InstructionFactory {

	private final Source source;

	public Rel(Source source) {
		this.source = source;
		if (this.source != null) {
			this.source.register();
			this.source.expand();
		}
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (source == null)
			throw new ArgumentException();
		return new RelObject(context);
	}

	public class RelObject extends InstructionObject {

		private byte[] bytes;
		private int size;

		public RelObject(Scope context) {
			super(context);
		}

		@Override
		public Expression resolve(Expression address) {
			context.setAddress(address);
			source.resolve(IntegerLiteral.ZERO);

			byte[] code = source.getBytes();
			List<Integer> relocations = collectRelocations();
			int codeStart = 3 + relocations.size() * 2;

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			output.write(0xC3);
			writeWord(output, codeStart);
			for (int relocation : relocations)
				writeWord(output, relocation);
			output.write(code, 0, code.length);

			bytes = output.toByteArray();
			size = bytes.length;
			return IntegerLiteral.of(address.getInteger() + size);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.of(size);
		}

		@Override
		public byte[] getBytes() {
			return bytes;
		}

		private List<Integer> collectRelocations() {
			Set<String> labelNames = source.getScope().getSymbols();
			LinkedHashSet<Integer> relocations = new LinkedHashSet<>();

			for (Line line : source.getLines()) {
				LinkedHashSet<Integer> values = new LinkedHashSet<>();
				collectRelocatableValues(line.getArguments(), labelNames, values);
				if (values.isEmpty())
					continue;

				byte[] lineBytes = line.getBytes();
				int lineAddress = line.getScope().getAddress().getInteger();
				for (int value : values)
					collectWordMatches(lineBytes, lineAddress, value, relocations);
			}

			return List.copyOf(relocations);
		}

		private void collectWordMatches(byte[] lineBytes, int lineAddress, int value, Set<Integer> relocations) {
			int low = value & 0xFF;
			int high = (value >> 8) & 0xFF;
			for (int i = 0; i < lineBytes.length - 1; i++) {
				if ((lineBytes[i] & 0xFF) == low && (lineBytes[i + 1] & 0xFF) == high)
					relocations.add(lineAddress + i);
			}
		}

		private void collectRelocatableValues(Expression expression, Set<String> labelNames, Set<Integer> values) {
			if (expression == null)
				return;

			if (containsRelocatableLabel(expression, labelNames)) {
				try {
					values.add(expression.getInteger());
				} catch (EvaluationException e) {
					// The containing expression is not necessarily the integer operand.
				}
			}

			if (expression instanceof BinaryOperator) {
				BinaryOperator binary = (BinaryOperator)expression;
				collectRelocatableValues(binary.getTerm1(), labelNames, values);
				collectRelocatableValues(binary.getTerm2(), labelNames, values);
			} else if (expression instanceof UnaryOperator) {
				collectRelocatableValues(((UnaryOperator)expression).getTerm(), labelNames, values);
			} else if (expression instanceof Group) {
				collectRelocatableValues(((Group)expression).getTerm(), labelNames, values);
			} else if (expression instanceof Annotation) {
				collectRelocatableValues(((Annotation)expression).getAnnotee(), labelNames, values);
			} else if (expression instanceof Index) {
				Index index = (Index)expression;
				collectRelocatableValues(index.getSequence(), labelNames, values);
				collectRelocatableValues(index.getIndex(), labelNames, values);
			} else if (expression instanceof IfElse) {
				IfElse ifElse = (IfElse)expression;
				collectRelocatableValues(ifElse.getTrueTerm(), labelNames, values);
				collectRelocatableValues(ifElse.getFalseTerm(), labelNames, values);
			} else if (expression instanceof Member) {
				collectRelocatableValues(((Member)expression).getObject(), labelNames, values);
			}
		}

		private boolean containsRelocatableLabel(Expression expression, Set<String> labelNames) {
			if (expression instanceof Identifier)
				return labelNames.contains(((Identifier)expression).getName());
			if (expression instanceof Register)
				return false;
			if (expression instanceof BinaryOperator) {
				BinaryOperator binary = (BinaryOperator)expression;
				return containsRelocatableLabel(binary.getTerm1(), labelNames) ||
					containsRelocatableLabel(binary.getTerm2(), labelNames);
			}
			if (expression instanceof UnaryOperator)
				return containsRelocatableLabel(((UnaryOperator)expression).getTerm(), labelNames);
			if (expression instanceof Group)
				return containsRelocatableLabel(((Group)expression).getTerm(), labelNames);
			if (expression instanceof Annotation)
				return containsRelocatableLabel(((Annotation)expression).getAnnotee(), labelNames);
			if (expression instanceof Index) {
				Index index = (Index)expression;
				return containsRelocatableLabel(index.getSequence(), labelNames) ||
					containsRelocatableLabel(index.getIndex(), labelNames);
			}
			if (expression instanceof IfElse) {
				IfElse ifElse = (IfElse)expression;
				return containsRelocatableLabel(ifElse.getTrueTerm(), labelNames) ||
					containsRelocatableLabel(ifElse.getFalseTerm(), labelNames);
			}
			if (expression instanceof Member)
				return containsRelocatableLabel(((Member)expression).getObject(), labelNames);
			return false;
		}

		private void writeWord(ByteArrayOutputStream output, int value) {
			output.write(value & 0xFF);
			output.write((value >> 8) & 0xFF);
		}

	}

}
