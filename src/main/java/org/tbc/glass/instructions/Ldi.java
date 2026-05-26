package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Ldi extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Ldi_.ARGUMENTS.check(arguments))
			return new Ldi_(context);
		throw new ArgumentException();
	}

	public static class Ldi_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Ldi_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0xA0 };
		}

	}

}