package nl.grauw.glass.instructions;

import nl.grauw.glass.Scope;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Schema;

public class Dephase extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (DephaseObject.ARGUMENTS.check(arguments))
			return new DephaseObject(context);
		throw new ArgumentException();
	}

	public static class DephaseObject extends Empty.EmptyObject {

		public static final Schema ARGUMENTS = new Schema();

		public DephaseObject(Scope context) {
			super(context);
		}

	}

}
