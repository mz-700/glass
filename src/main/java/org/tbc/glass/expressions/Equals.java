package org.tbc.glass.expressions;

public class Equals extends BinaryOperator {

	public Equals(Expression term1, Expression term2) {
		super(term1, term2);
	}

	@Override
	public Equals copy(Context context) {
		return new Equals(term1.copy(context), term2.copy(context));
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
				return IntegerLiteral.of(registersEqual(term1.getRegister(), term2.getRegister()));
			return IntegerLiteral.of(term1.getInteger() == term2.getInteger());
		}
		return super.get(type);
	}

	static boolean registersEqual(Register register1, Register register2) {
		if (!register1.toString().equals(register2.toString()))
			return false;
		if (!register1.isIndex() || !register1.isPair())
			return true;
		return register1.getIndexOffset().getInteger() == register2.getIndexOffset().getInteger();
	}

	@Override
	public String getLexeme() {
		return "=";
	}

}