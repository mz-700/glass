package nl.grauw.glass;

import nl.grauw.glass.expressions.Equals;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Group;
import nl.grauw.glass.expressions.Identifier;
import nl.grauw.glass.instructions.ArgumentException;

public class ParameterScope extends Scope {

	public ParameterScope(Scope parent, Expression parameters, Expression arguments) {
		super(parent);

		while (parameters != null) {
			Expression parameter = parameters.getHead();
			Expression argument;

			if (parameter instanceof Equals) {
				argument = arguments != null ? arguments.getHead() : ((Equals)parameter).getTerm2();
				parameter = ((Equals)parameter).getTerm1();
			} else {
				if (arguments == null)
					throw new ArgumentException("Not enough arguments.");
				argument = arguments.getHead();
			}

			Identifier identifier = getParameterIdentifier(parameter);
			if (identifier == null)
				throw new ArgumentException("Parameter must be an identifier.");

			if (parameter instanceof Group) {
				if (!(argument instanceof Group))
					throw new ArgumentException("Argument must be parenthesized.");
				argument = ((Group)argument).getTerm();
			}

			addSymbol(identifier.getName(), argument);

			parameters = parameters.getTail();
			if (arguments != null)
				arguments = arguments.getTail();
		}
		if (arguments != null)
			throw new ArgumentException("Too many arguments.");
	}

	public static boolean isParameter(Expression parameter) {
		return getParameterIdentifier(parameter) != null;
	}

	public static Identifier getParameterIdentifier(Expression parameter) {
		if (parameter instanceof Identifier)
			return (Identifier)parameter;
		if (parameter instanceof Group && ((Group)parameter).getTerm() instanceof Identifier)
			return (Identifier)((Group)parameter).getTerm();
		return null;
	}

}
