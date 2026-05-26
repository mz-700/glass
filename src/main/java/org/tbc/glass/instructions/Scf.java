package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Scf extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Scf_.ARGUMENTS.check(arguments))
			return new Scf_(context);
		throw new ArgumentException();
	}

	public static class Scf_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Scf_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0x37 };
		}

	}

}