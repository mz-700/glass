package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Ret extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Ret_.ARGUMENTS.check(arguments))
			return new Ret_(context);
		if (Ret_F.ARGUMENTS.check(arguments))
			return new Ret_F(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Ret_ extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema();

		public Ret_(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xC9 };
		}

	}

	public static class Ret_F extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( new Schema.IsFlag());

		private final Expression argument;

		public Ret_F(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)(0xC0 | argument.getFlag().getCode() << 3) };
		}

	}

}