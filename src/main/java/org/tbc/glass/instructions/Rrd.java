package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Rrd extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Rrd_.ARGUMENTS.check(arguments))
			return new Rrd_(context);
		throw new ArgumentException();
	}

	public static class Rrd_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Rrd_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)0x67 };
		}

	}

}