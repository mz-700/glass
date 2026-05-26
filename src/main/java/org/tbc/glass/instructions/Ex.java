package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;

public class Ex extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Ex_AF.ARGUMENTS.check(arguments))
			return new Ex_AF(context);
		if (Ex_DE_HL.ARGUMENTS.check(arguments))
			return new Ex_DE_HL(context);
		if (Ex_SP.ARGUMENTS.check(arguments))
			return new Ex_SP(context, arguments.getElement(1));
		throw new ArgumentException();
	}

	public static class Ex_AF extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_AF, Schema.DIRECT_AF_);

		public Ex_AF(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0x08 };
		}

	}

	public static class Ex_DE_HL extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_DE, Schema.DIRECT_HL);

		public Ex_DE_HL(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ONE;
		}

		@Override
		public byte[] getBytes() {
			return new byte[] { (byte)0xEB };
		}

	}

	public static class Ex_SP extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.INDIRECT_SP, Schema.DIRECT_HL_IX_IY);

		private final Expression argument;

		public Ex_SP(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression getSize() {
			return indexifyDirect(argument.getRegister(), IntegerLiteral.ONE);
		}

		@Override
		public byte[] getBytes() {
			return indexifyDirect(argument.getRegister(), (byte)0xE3);
		}

	}

}