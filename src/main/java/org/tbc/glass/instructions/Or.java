package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class Or extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Or_R.ARGUMENTS.check(arguments))
			return new Or_R(context, arguments);
		if (Or_N.ARGUMENTS.check(arguments))
			return new Or_N(context, arguments);
		throw new ArgumentException();
	}

	public static class Or_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R_INDIRECT_HL_IX_IY);

		private final Expression argument;

		public Or_R(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return indexifyIndirect(argument.getRegister(), IntegerLiteral.ONE);
		}

		@Override
		public byte[] getBytes() {
			Register register = argument.getRegister();
			return indexifyIndirect(register, (byte)(0xB0 | register.get8BitCode()));
		}

	}

	public static class Or_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Or_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xF6, (byte)argument.getInteger() };
		}

	}

}