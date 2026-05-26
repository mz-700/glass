package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class Push extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Push_RR.ARGUMENTS.check(arguments))
			return new Push_RR(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Push_RR extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_RR_AF_INDEX);

		final Expression argument;

		public Push_RR(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return indexifyDirect(argument.getRegister(), IntegerLiteral.ONE);
		}

		@Override
		public byte[] getBytes() {
			Register register = argument.getRegister();
			return indexifyDirect(register, (byte)(0xC5 | register.get16BitCode() << 4));
		}

	}

}