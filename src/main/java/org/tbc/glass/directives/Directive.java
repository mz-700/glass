package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;

public abstract class Directive {

	public Directive copy(Scope scope) {
		return this;
	}

    public void register(Scope scope, Line line) {
		for (String label : line.getLabels())
			scope.addSymbol(label, line.getScope());
	}

}