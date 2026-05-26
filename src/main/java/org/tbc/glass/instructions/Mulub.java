package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Mulub extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Mulub_R_R.ARGUMENTS.check(arguments))
			return new Mulub_R_R(context, arguments.getElement(1));
		throw new ArgumentException();
	}

	public static class Mulub_R_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_A, Schema.DIRECT_R);

		private final Expression argument;

		public Mulub_R_R(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)(0xC1 | argument.getRegister().get8BitCode() << 3) };
		}

	}

}