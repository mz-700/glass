package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Rept extends Directive {

	private final Source source;

	public Rept(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Rept(source.copy(scope));
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.Rept( source) );
		super.register(scope, line);
	}

}