package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Out extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Out_C_N.ARGUMENTS.check(arguments))
			return new Out_C_N(context, arguments.getElement(1));
		if (Out_N_N.ARGUMENTS.check(arguments))
			return new Out_N_N(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Out_C_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.INDIRECT_C, Schema.DIRECT_R);

		private final Expression argument;

		public Out_C_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)(0x41 | argument.getRegister().get8BitCode() << 3) };
		}

	}

	public static class Out_N_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.INDIRECT_N, Schema.DIRECT_A);

		private final Expression argument;

		public Out_N_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xD3, (byte)argument.getInteger() };
		}

	}

}