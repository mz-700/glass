package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Add;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;
import org.tbc.glass.expressions.Schema;
import org.tbc.glass.expressions.Subtract;

public class Djnz extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Djnz_N.ARGUMENTS.check(arguments))
			return new Djnz_N(context, arguments);
		throw new ArgumentException();
	}

	public static class Djnz_N extends InstructionObject {

		public static final Schema ARGUMENTS = new Schema( Schema.DIRECT_N);

		private final Expression argument;

		public Djnz_N(Scope context, Expression arguments) {
			super(context);
			this.argument = arguments;
		}

		@Override
		public Expression getSize() {
			return IntegerLiteral.TWO;
		}

		@Override
		public byte[] getBytes() {
			int offset = new Subtract(argument, new Add(context.getAddress(), getSize())).getInteger();
			if (offset < -128 || offset > 127)
				throw new ArgumentException("Jump offset out of range: " + offset);
			return new byte[] { (byte)0x10, (byte)offset };
		}

	}

}