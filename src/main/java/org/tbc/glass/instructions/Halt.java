package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Halt extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Halt_.ARGUMENTS.check(arguments))
			return new Halt_(context);
		throw new ArgumentException();
	}

	public static class Halt_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Halt_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0x76 };
		}

	}

}