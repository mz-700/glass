package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.Line;
import org.tbc.glass.ParameterScope;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Equals;
import org.tbc.glass.expressions.Expression;

public class MacroInstruction extends InstructionFactory {

	private final Expression parameters;
	private final Source source;

	public MacroInstruction(Expression parameters, Source source) {
		this.parameters = parameters;
		this.source = source;

		Expression parameter = parameters != null ? parameters.getElement(0) : null;
		for (int i = 0; parameter != null; i++) {
			if (!ParameterScope.isParameter(parameter) &&
					!(parameter instanceof Equals && ParameterScope.isParameter(((Equals)parameter).getTerm1())))
				throw new ArgumentException("Parameter must be an identifier.");
			parameter = parameters.getElement(i + 1);
		}
	}

	@Override
	public void expand(Line line, List<Line> lines) {
		super.expand(line, lines);
		Scope parameterScope = new ParameterScope(source.getScope().getParent(), parameters, line.getArguments());
		Source sourceCopy = source.copy(parameterScope);
		sourceCopy.register();
		sourceCopy.register(line.getScope());
		sourceCopy.expand(lines);
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		return new Empty.EmptyObject(context);
	}

}