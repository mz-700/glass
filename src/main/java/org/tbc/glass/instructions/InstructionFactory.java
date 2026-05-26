package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;

public abstract class InstructionFactory {

	public void expand(Line line, List<Line> lines) {
		lines.add(line);
	}

	public abstract InstructionObject createObject(Scope context, Expression arguments);

}