package org.tbc.glass.directives;

import org.tbc.glass.AssemblyException;
import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Instruction;
import org.tbc.glass.instructions.Empty;
import org.tbc.glass.instructions.MacroInstruction;

public class Macro extends Directive {

	private final Source source;

	public Macro(Source source) {
		this.source = source;
	}

	@Override
	public Directive copy(Scope scope) {
		return new Macro(source.copy(scope));
	}

	@Override
	public void register(Scope scope, Line line) {
		if ( line.getLabels()
                 .isEmpty() )
			throw new AssemblyException("Macro without label.");
		Instruction instruction = new Instruction(
			new MacroInstruction(line.getArguments(), source),
			source.getScope()
		);
		for (String label : line.getLabels())
			scope.addSymbol(label, instruction);
		line.setInstruction(Empty.INSTANCE);
	}

}