package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Lddr extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Lddr_.ARGUMENTS.check(arguments))
			return new Lddr_(context);
		throw new ArgumentException();
	}

	public static class Lddr_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Lddr_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0xB8 };
		}

	}

}