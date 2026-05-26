package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Call extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Call_F_N.ARGUMENTS.check(arguments))
			return new Call_F_N(context, arguments.getElement(0), arguments.getElement(1));
		if (Call_N.ARGUMENTS.check(arguments))
			return new Call_N(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Call_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Call_N(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.THREE;
		}

		@Override
		public byte[] getBytes() {
			int address = argument.getInteger();
			return new byte[] { (byte)0xCD, (byte)address, (byte)(address >> 8) };
		}

	}

	public static class Call_F_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( new Schema.IsFlag(), Schema.DIRECT_N);

		private final Expression argument1;
		private final Expression argument2;

		public Call_F_N(Scope context, Expression argument1, Expression argument2) {
			super(context);
			this.argument1 = argument1;
			this.argument2 = argument2;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.THREE;
		}

		@Override
		public byte[] getBytes() {
			int address = argument2.getInteger();
			return new byte[] { (byte)(0xC4 | argument1.getFlag().getCode() << 3), (byte)address, (byte)(address >> 8) };
		}

	}

}