package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Im extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Im_N.ARGUMENTS.check(arguments))
			return new Im_N(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Im_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Im_N(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			int value = argument.getInteger();
			if (value == 0) {
				return new byte[] { (byte)0xED, (byte)0x46 };
			} else if (value == 1) {
				return new byte[] { (byte)0xED, (byte)0x56 };
			} else if (value == 2) {
				return new byte[] { (byte)0xED, (byte)0x5E };
			}
			throw new ArgumentException();
		}

	}

}