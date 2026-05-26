package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Register;
import org.tbc.glass.expressions.Schema;

public class Dec extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Dec_R.ARGUMENTS.check(arguments))
			return new Dec_R(context, arguments.getElement(0));
		if (Dec_RR.ARGUMENTS.check(arguments))
			return new Dec_RR(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Dec_R extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_R_INDIRECT_HL_IX_IY);

		private final Expression argument;

		public Dec_R(Scope context, Expression arguments) {
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
			return indexifyIndirect(register, (byte)(0x05 | register.get8BitCode() << 3));
		}

	}

	public static class Dec_RR extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_RR_INDEX);

		private final Expression argument;

		public Dec_RR(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return indexifyDirect(argument.getRegister(), IntegerLiteral.ONE);
		}

		@Override
		public byte[] getBytes() {
			Register register = argument.getRegister();
			return indexifyDirect(register, (byte)(0x0B | register.get16BitCode() << 4));
		}

	}

}