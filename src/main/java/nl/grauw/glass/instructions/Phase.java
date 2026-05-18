package nl.grauw.glass.instructions;

import nl.grauw.glass.Scope;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Schema;

public class Phase extends InstructionFactory {

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (PhaseObject.ARGUMENTS.check(arguments))
			return new PhaseObject(context, arguments.getElement(0), null);
		if (PhaseObject.ARGUMENTS_SIZE.check(arguments))
			return new PhaseObject(context, arguments.getElement(0), arguments.getElement(1));
		throw new ArgumentException();
	}

	public static class PhaseObject extends Empty.EmptyObject {

		public static final Schema ARGUMENTS = new Schema(Schema.INTEGER);
		public static final Schema ARGUMENTS_SIZE = new Schema(Schema.INTEGER, Schema.INTEGER);

		private final Expression argument;
		private final Expression size;

		public PhaseObject(Scope context, Expression argument, Expression size) {
			super(context);
			this.argument = argument;
			this.size = size;
		}

		@Override
		public Expression resolve(Expression address) {
			super.resolve(address);
			return argument;
		}

		public boolean hasSize() {
			return size != null;
		}

		public int getSizeLimit() {
			return size.getInteger();
		}

	}

}
