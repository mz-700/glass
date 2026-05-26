package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;

public class Empty extends InstructionFactory {

	public static final Empty INSTANCE = new Empty();

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		return new EmptyObject(context);
	}

	public static class EmptyObject extends InstructionObject {

		private static final byte[] NO_BYTES = new byte[] {};

		public EmptyObject(Scope context) {
			super(context);
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.ZERO;
		}

		@Override
		public byte[] getBytes() {
			return NO_BYTES;
		}

	}

}