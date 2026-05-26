package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Schema;
import org.tbc.glass.expressions.Type;

public class Section extends InstructionFactory {

	public static final Schema ARGUMENTS = new Schema( Schema.IDENTIFIER);

	private final Source source;

	public Section(Source source) {
		this.source = source;
	}

	public Source getSource() {
		return source;
	}

	@Override
	public void expand(Line line, List<Line> lines) {
		if (!ARGUMENTS.check(line.getArguments()))
			throw new ArgumentException();

		if (!line.getArguments().is(Type.SECTIONCONTEXT))
			throw new ArgumentException("Argument does not reference a section context.");

		line.getArguments().getSectionContext().addSection(this);

		source.expand();
		super.expand(line, lines);
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (ARGUMENTS.check(arguments))
			return new Empty.EmptyObject(context);
		throw new ArgumentException();
	}

}