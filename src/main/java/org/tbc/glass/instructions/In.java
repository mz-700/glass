package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class In extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (In_N_C.ARGUMENTS.check(arguments))
			return new In_N_C(context, arguments.getElement(0));
		if (In_N_C.ARGUMENTS_NO_R.check(arguments))
			return new In_N_C(context, Register.HL);
		if (In_N_N.ARGUMENTS.check(arguments))
			return new In_N_N(context, arguments.getElement(1));
		throw new ArgumentException();
	}

	public static class In_N_C extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R, Schema.INDIRECT_C);
		public static final Schema ARGUMENTS_NO_R = new Schema( Schema.INDIRECT_C);

		private final Expression argument;

		public In_N_C(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xED, (byte)(0x40 | argument.getRegister().get8BitCode() << 3) };
		}

	}

	public static class In_N_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_A, Schema.INDIRECT_N);

		private final Expression argument;

		public In_N_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xDB, (byte)argument.getInteger() };
		}

	}

}