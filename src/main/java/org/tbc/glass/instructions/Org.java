package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Schema;

public class Org extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (Org_N.ARGUMENTS.check(arguments))
			return new Org_N(context, arguments.getElement(0));
		throw new ArgumentException();
	}

	public static class Org_N extends Empty.EmptyObject {

		public static final Schema ARGUMENTS = new Schema( Schema.INTEGER);

		private final Expression argument;

		public Org_N(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public Expression resolve(Expression address) {
			super.resolve(address);
			return argument;
		}

	}

}