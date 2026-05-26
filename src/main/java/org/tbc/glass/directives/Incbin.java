package org.tbc.glass.directives;

import java.nio.file.Path;
import java.util.List;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;

public class Incbin extends Directive {

	private final List<Path> basePaths;

	public Incbin(List<Path> basePaths) {
		this.basePaths = basePaths;
	}

	@Override
	public void register(Scope scope, Line line) {
		line.setInstruction(new org.tbc.glass.instructions.Incbin( basePaths) );
		super.register(scope, line);
	}

}