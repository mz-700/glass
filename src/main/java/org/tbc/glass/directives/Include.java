package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Include extends Directive {

	private final Source source;

	public Include(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Include(source.copy(scope.getParent()));
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.Include( source) );
		super.register(scope, line);
		source.register();
	}

}