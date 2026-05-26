package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Rst extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Rst_N.ARGUMENTS.check(arguments))
			return new Rst_N(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Rst_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Rst_N(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			int value = argument.getInteger();
			if (value < 0 || value > 0x38 || (value & 7) != 0)
				throw new ArgumentException();
			return new byte[] { (byte)(0xC7 + value) };
		}

	}

}