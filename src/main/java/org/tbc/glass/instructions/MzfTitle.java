package org.tbc.glass.instructions;

import org.tbc.glass.Scope;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.Schema;

public class MzfTitle extends InstructionFactory {

	public static final Schema ARGUMENTS = new Schema(Schema.STRING);

	@Override
	public InstructionObject createObject(Scope context, Expression arguments) {
		if (ARGUMENTS.check(arguments))
			return new Empty.EmptyObject(context);
		throw new ArgumentException();
	}

}