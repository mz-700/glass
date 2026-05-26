package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Neg extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Neg_.ARGUMENTS.check(arguments))
			return new Neg_(context);
		throw new ArgumentException();
	}

	public static class Neg_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Neg_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0x44 };
		}

	}

}