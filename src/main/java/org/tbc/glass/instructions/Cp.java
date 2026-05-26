package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class Cp extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Cp_R.ARGUMENTS.check(arguments))
			return new Cp_R(context, arguments);
		if (Cp_N.ARGUMENTS.check(arguments))
			return new Cp_N(context, arguments);
		throw new ArgumentException();
	}

	public static class Cp_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R_INDIRECT_HL_IX_IY);

		private final Expression argument;

		public Cp_R(Scope context, Expression arguments) {
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
			return indexifyIndirect(register, (byte)(0xB8 | register.get8BitCode()));
		}

	}

	public static class Cp_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Cp_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xFE, (byte)argument.getInteger() };
		}

	}

}