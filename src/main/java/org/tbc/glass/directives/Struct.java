package org.tbc.glass.directives;

import org.tbc.glass.AssemblyException;
import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Struct extends Directive {

	private final Source source;

	public Struct(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Struct(source.copy(scope));
	}

	@Override
	public void register(Scope scope, Line line) {
		if ( line.getLabels()
                 .isEmpty() )
			throw new AssemblyException("Struct without label.");
		for (String label : line.getLabels())
			scope.addSymbol(label, source.getScope());
		line.setInstruction(new org.tbc.glass.instructions.Struct( source) );
	}

}