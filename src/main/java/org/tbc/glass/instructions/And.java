package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class And extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (And_R.ARGUMENTS.check(arguments))
			return new And_R(context, arguments);
		if (And_N.ARGUMENTS.check(arguments))
			return new And_N(context, arguments);
		throw new ArgumentException();
	}

	public static class And_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R_INDIRECT_HL_IX_IY);

		private final Expression argument;

		public And_R(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return indexifyIndirect(argument.getRegister(), IntegerLiteral.ONE);
		}

		@Override
		public byte[] getBytes() {
			Register register = argument.getRegister();
			return indexifyIndirect(register, (byte)(0xA0 | register.get8BitCode()));
		}

	}

	public static class And_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public And_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xE6, (byte)argument.getInteger() };
		}

	}

}