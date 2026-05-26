package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Outd extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Outd_.ARGUMENTS.check(arguments))
			return new Outd_(context);
		throw new ArgumentException();
	}

	public static class Outd_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Outd_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0xAB };
		}

	}

}