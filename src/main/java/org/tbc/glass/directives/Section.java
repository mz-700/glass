package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Section extends Directive {

	private final Source source;

	public Section(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Section(source.copy(scope.getParent()));
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.Section( source) );
		super.register(scope, line);
		source.register();
	}

}