package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Muluw extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Muluw_RR_RR.ARGUMENTS.check(arguments))
			return new Muluw_RR_RR(context, arguments.getElement(1));
		throw new ArgumentException();
	}

	public static class Muluw_RR_RR extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_HL, Schema.DIRECT_RR);

		private final Expression argument;

		public Muluw_RR_RR(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)(0xC3 | argument.getRegister().get16BitCode() << 4) };
		}

	}

}