package org.tbc.glass.directives;

import java.util.HashSet;
import java.util.Set;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.ContextLiteral;
import org.tbc.glass.expressions.EvaluationException;
import org.tbc.glass.expressions.Identifier;
import org.tbc.glass.expressions.IfElse;
import org.tbc.glass.expressions.Member;

public class If extends Directive {

	private final Source thenSource;
	private final Source elseSource;

	public If(Source thenSource, Source elseSource) {
		this.thenSource = thenSource;
		this.elseSource = elseSource;
	}

	@Override
	public Directive copy(Scope scope) {
		return new If(thenSource.copy(new Scope(scope.getParent())), elseSource.copy(new Scope(scope.getParent())));
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.If( thenSource, elseSource) );
		super.register(scope, line);
		thenSource.register();
		elseSource.register();

		try {
			Source selectedSource = line.getArguments().getInteger() != 0 ? thenSource : elseSource;
			for (String symbol : selectedSource.getScope().getSymbols())
				scope.addSymbol(symbol, selectedSource.getScope().getLocalSymbol(symbol));
			return;
		} catch (EvaluationException e) {
			// If the condition can not be resolved yet, keep the symbols lazy.
		}

		Set<String> symbols = new HashSet<>();
		symbols.addAll(thenSource.getScope().getSymbols());
		symbols.addAll(elseSource.getScope().getSymbols());
		for (String symbol : symbols)
		{
			scope.addSymbol(symbol, new IfElse(line.getArguments(),
				new Member(new ContextLiteral(thenSource.getScope()), new Identifier(symbol, null)),
				new Member(new ContextLiteral(elseSource.getScope()), new Identifier(symbol, null))
			));
		}
	}

}
