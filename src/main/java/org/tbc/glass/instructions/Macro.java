package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.AssemblyException;
import org.tbc.glass.Line;
import org.tbc.glass.ParameterScope;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Equals;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Identifier;
import org.tbc.glass.expressions.IntegerLiteral;

public class Macro extends InstructionFactory {

	private final Source source;
	private final Scope parameterScope;

	public Macro(Source source) {
		this.source = new Source(source.getScope());
		this.parameterScope = new Scope(source.getScope());
		this.source.addLines(source.copy(parameterScope).getLines());
		this.source.register();
	}

	@Override
	public void expand(Line line, List<Line> lines) {
		Expression parameters = line.getArguments();
		while (parameters != null) {
			Expression parameter = parameters.getHead();
			if (!ParameterScope.isParameter(parameter) &&
					!(parameter instanceof Equals && ParameterScope.isParameter(((Equals)parameter).getTerm1())))
				throw new ArgumentException("Parameter must be an identifier.");

			if ( parameter instanceof Equals equals ) {
				Identifier identifier = ParameterScope.getParameterIdentifier(equals.getTerm1());
				parameterScope.addSymbol(identifier.getName(), equals.getTerm2());
			} else {
				Identifier identifier = ParameterScope.getParameterIdentifier(parameter);
				parameterScope.addSymbol(identifier.getName(), IntegerLiteral.ZERO);
			}
			parameters = parameters.getTail();
		}

		try {
			source.expand();
		} catch (AssemblyException e) {
			// ignore
		}
		super.expand(line, lines);
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		return new MacroObject(context);
	}

	public class MacroObject extends Empty.EmptyObject {

		public MacroObject(Scope context) {
			super(context);
		}

		@Override
		public Expression resolve(Expression address) {
			try {
				source.resolve(IntegerLiteral.ZERO);
			} catch (AssemblyException e) {
				// ignore
			}
			return super.resolve(address);
		}

	}

}