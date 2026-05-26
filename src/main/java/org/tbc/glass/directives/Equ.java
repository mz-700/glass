package org.tbc.glass.directives;

import org.tbc.glass.AssemblyException;
import org.tbc.glass.Line;
import org.tbc.glass.Scope;

public class Equ extends Directive {

	@Override
	public void register(Scope scope, Line line) {
		if ( line.getLabels()
                 .isEmpty() )
			throw new AssemblyException("Equ without label.");
		for (String label : line.getLabels())
			scope.addSymbol(label, line.getArguments());
	}

}