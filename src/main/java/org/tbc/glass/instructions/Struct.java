package org.tbc.glass.instructions;

import java.util.List;

import org.tbc.glass.AssemblyException;
import org.tbc.glass.Line;
import org.tbc.glass.Scope;
import org.tbc.glass.Source;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.IntegerLiteral;

public class Struct extends InstructionFactory {

	private final Source source;

	public Struct(Source source) {
		this.source = source;
		this.source.register();
		try {
			this.source.expand();
		} catch (AssemblyException e) {
			// Labels in incomplete conditional branches can still be used lazily.
		}
	}

	@Override
	public void expand(Line line, List<Line> lines) {
		super.expand(line, lines);
	}

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		return new StructObject(context);
	}

	public class StructObject extends Empty.EmptyObject {

		public StructObject(Scope context) {
			super(context);
		}

		@Override
		public Expression resolve(Expression address) {
			try {
				source.resolve(IntegerLiteral.ZERO);
			} catch (AssemblyException e) {
				// Invalid virtual layout members are reported when referenced.
			}
			return super.resolve(address);
		}

	}

}