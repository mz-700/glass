package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class Rr extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Rr_R.ARGUMENTS.check(arguments))
			return new Rr_R(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Rr_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R_INDIRECT_HL_IX_IY);

		private final Expression argument;

		public Rr_R(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return indexifyIndirect(argument.getRegister(), IntegerLiteral.TWO);
		}

		@Override
		public byte[] getBytes() {
			Register register = argument.getRegister();
			return indexifyOnlyIndirect(register, (byte)0xCB, (byte)(0x18 + register.get8BitCode()));
		}

	}

}