package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Rel extends Directive {

	private final Source source;

	public Rel(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Rel(source.copy(scope));
	}

	@Override
	public void register(Scope scope, Line line) {
		super.register(scope, line);
		line.setInstruction(new org.tbc.glass.instructions.Rel(source));
	}

}
