package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Schema;

public class Warning extends InstructionFactory {

	public static final Schema ARGUMENTS = new Schema();
	public static final Schema ARGUMENTS_S = new Schema( Schema.STRING);

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (ARGUMENTS.check(arguments) || ARGUMENTS_S.check(arguments))
			return new Warning_(context, arguments);
		throw new ArgumentException();
	}

	public static class Warning_ extends Empty.EmptyObject {

		private final Expression argument;

		public Warning_(Scope context, Expression argument) {
			super(context);
			this.argument = argument;
		}

		@Override
		public byte[] getBytes() {
			if (argument == null)
				System.out.println("Warning: A warning directive was encountered.");
			else
				System.out.println("Warning: " + argument.getString());
			return super.getBytes();
		}

	}

}