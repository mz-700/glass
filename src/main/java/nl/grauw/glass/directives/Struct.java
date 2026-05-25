package nl.grauw.glass.directives;

import nl.grauw.glass.AssemblyException;
import nl.grauw.glass.Line;
import nl.grauw.glass.Scope;
import nl.grauw.glass.Source;

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
		line.setInstruction(new nl.grauw.glass.instructions.Struct(source));
	}

}
