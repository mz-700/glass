package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Cpl extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Cpl_.ARGUMENTS.check(arguments))
			return new Cpl_(context);
		throw new ArgumentException();
	}

	public static class Cpl_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Cpl_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0x2F };
		}

	}

}