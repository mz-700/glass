package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Reti extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Reti_.ARGUMENTS.check(arguments))
			return new Reti_(context);
		throw new ArgumentException();
	}

	public static class Reti_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Reti_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0x4D };
		}

	}

}