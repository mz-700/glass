package nl.grauw.glass.instructions;

import nl.grauw.glass.Scope;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Identifier;
import nl.grauw.glass.expressions.IntegerLiteral;
import nl.grauw.glass.expressions.Schema;

public class BankPhase extends InstructionFactory {

	private final int address;
	private final int defaultSize;

	public BankPhase(int address, int defaultSize) {
		this.address = address;
		this.defaultSize = defaultSize;
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (BankPhaseObject.ARGUMENTS.check(arguments))
			return new BankPhaseObject(context, address, defaultSize);
		if (BankPhaseObject.ARGUMENTS_SIZE_OFF.check(arguments) && isSizeOff(arguments.getElement(0)))
			return new BankPhaseObject(context, address, null);
		if (BankPhaseObject.ARGUMENTS_SIZE.check(arguments))
			return new BankPhaseObject(context, address, arguments.getElement(0).getInteger());
		throw new ArgumentException();
	}

	private boolean isSizeOff(Expression argument) {
		String name = ((Identifier)argument).getName();
		return "size_off".equals(name) || "SIZE_OFF".equals(name);
	}

	public static class BankPhaseObject extends Phase.PhaseObject {

		public static final Schema ARGUMENTS = new Schema();
		public static final Schema ARGUMENTS_SIZE = new Schema(Schema.INTEGER);
		public static final Schema ARGUMENTS_SIZE_OFF = new Schema(Schema.IDENTIFIER);

		public BankPhaseObject(Scope context, int address, Integer size) {
			super(context, IntegerLiteral.of(address), size != null ? IntegerLiteral.of(size) : null);
		}

	}

}
