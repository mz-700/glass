package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Schema;

public class Proc extends InstructionFactory {

	public static final Schema ARGUMENTS = new Schema();

	private final Source source;

	public Proc(Source source) {
		this.source = source;
	}

	public void expand(Line line, List<Line> lines) {
		Expression arguments = line.getArguments();
		if (!ARGUMENTS.check(arguments))
			throw new ArgumentException();

		super.expand(line, lines);
		Source sourceCopy = source.copy(line.getScope());
		sourceCopy.register();
		sourceCopy.expand(lines);
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		return new Empty.EmptyObject(context);
	}

}