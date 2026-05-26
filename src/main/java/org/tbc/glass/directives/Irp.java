package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;

public class Irp extends Directive {

	private final Source source;

	public Irp(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Irp(source.copy(scope));
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.Irp( source) );
		super.register(scope, line);
	}

}