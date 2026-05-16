package nl.grauw.glass.expressions;

public interface Context {

	Expression getSymbol( String name );

	Expression getLocalSymbol( String name );

	boolean hasSymbol( String name );

	boolean hasLocalSymbol( String name );

	Expression getAddress();

}