package org.tbc.glass.expressions;

public class NotEquals extends BinaryOperator {

	public NotEquals(Expression term1, Expression term2) {
		super(term1, term2);
	}

	@Override
	public NotEquals copy(Context context) {
		return new NotEquals(term1.copy(context), term2.copy(context));
	}

	@Override
	public boolean is(Expression type) {
		return type.is(Type.INTEGER) && term1.is(Type.REGISTER) && term2.is(Type.REGISTER) ||
				super.is(type);
	}

	@Override
	public Expression get(Expression type) {
		if (type.is(Type.INTEGER)) {
			if (term1.is(Type.REGISTER) && term2.is(Type.REGISTER))
				return IntegerLiteral.of(!Equals.registersEqual(term1.getRegister(), term2.getRegister()));
			return IntegerLiteral.of(term1.getInteger() != term2.getInteger());
		}
		return super.get(type);
	}

	@Override
	public String getLexeme() {
		return "!=";
	}

}