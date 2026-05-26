package org.tbc.glass.directives;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.expressions.SectionContextLiteral;

public class Ds extends Directive {

	@Override
	public void register(Scope scope, Line line) {
		org.tbc.glass.instructions.Ds ds = new org.tbc.glass.instructions.Ds();
		line.setInstruction(ds);
		SectionContextLiteral sectionContextLiteral = new SectionContextLiteral(line.getScope(), ds);
		for (String label : line.getLabels())
			scope.addSymbol(label, sectionContextLiteral);
	}

}


// TODO: here