package nl.grauw.glass;

import java.io.Serial;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import nl.grauw.glass.expressions.Context;
import nl.grauw.glass.expressions.ContextLiteral;
import nl.grauw.glass.expressions.ErrorLiteral;
import nl.grauw.glass.expressions.EvaluationException;
import nl.grauw.glass.expressions.Expression;
import nl.grauw.glass.expressions.Type;

public class Scope implements Context {

	private final Scope parent;
	private final Map<String, Expression> symbols = new HashMap<>();
	private Expression address;

	public Scope() {
		this(null);
	}

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public Scope getParent() {
		return parent;
	}

	public Set<String> getSymbols() {
		return symbols.keySet();
	}

	public String getLocalSymbolNameForContext(Scope context) {
		for (Entry<String, Expression> entry : symbols.entrySet()) {
			Expression value = entry.getValue();
			try {
				if (value.is(Type.CONTEXT) && value.getContext() == context)
					return entry.getKey();
			} catch (EvaluationException e) {
				// Ignore unresolved symbols while searching for a scope owner.
			}
		}
		return null;
	}

	@Override
	public Expression getAddress() {
		if (this.address == null)
			throw new EvaluationException("Address not initialized.");
		return address;
	}

	public void setAddress(Expression address) {
		if (this.address != null)
			throw new AssemblyException("Address was already set.");
		this.address = address;
	}

	public void addSymbol(String name, Expression value) {
		if (name == null || value == null)
			throw new AssemblyException("Symbol name and value must not be null.");
		if (symbols.containsKey(name))
			throw new AssemblyException("Can not redefine symbol: " + name);
		symbols.put(name, value);
	}

	public void addSymbol(String name, Scope context) {
		addSymbol(name, new ContextLiteral(context));
	}

	public void addSymbols(Scope other) {
		for (Entry<String, Expression> entry : other.symbols.entrySet()) {
			addSymbol(entry.getKey(), entry.getValue());
		}
	}

	public boolean hasSymbol(String name) {
		return hasLocalSymbol(name) || parent != null && parent.hasSymbol(name);
	}

	public Expression getSymbol(String name) {
		Expression value = getLocalSymbolOrNull(name);
		if (value != null)
			return value;
		if (parent != null)
			return parent.getSymbol(name);
		return new ErrorLiteral(new SymbolNotFoundException(name));
	}

	public boolean hasLocalSymbol(String name) {
		return getLocalSymbolOrNull(name) != null;
	}

	public Expression getLocalSymbol(String name) {
		Expression value = getLocalSymbolOrNull(name);
		if (value != null)
			return value;
		return new ErrorLiteral(new SymbolNotFoundException(name));
	}

	private Expression getLocalSymbolOrNull(String name) {
		Expression value = symbols.get(name);
		if (value != null)
			return value;

		int index = name.length();
		while ((index = name.lastIndexOf('.', index - 1)) != -1) {
			Expression result = symbols.get(name.substring(0, index));
			if (result != null && result.is(Type.CONTEXT))
				return ((Scope)result.getContext()).getLocalSymbolOrNull(name.substring(index + 1));
		}
		return null;
	}

	public class SymbolNotFoundException extends EvaluationException {
		@Serial private static final long serialVersionUID = 1L;

		private final String name;

		public SymbolNotFoundException(String name) {
			super("Symbol not found: " + name);
			this.name = name;
		}

		public Scope getScope() {
			return Scope.this;
		}

		public String getName() {
			return name;
		}
	}

	public String serializeSymbols() {
		return serializeSymbols("", new SingleLinkedList<>( this, null ) );
	}

	public String serializeSymbols(String namePrefix, SingleLinkedList<Scope> breadcrumbs) {
		StringBuilder builder = new StringBuilder();
		TreeMap<String, Expression> sortedMap = new TreeMap<>(symbols);
		for (Map.Entry<String, Expression> entry : sortedMap.entrySet()) {
			String name = namePrefix + entry.getKey();
			Expression value = entry.getValue();
			if (value.is(Type.INTEGER)) {
				try {
					builder.append( name )
                           .append( ": equ " )
                           .append( value.getHexValue() )
                           .append( "\n" );
				} catch (EvaluationException e) {
					// ignore
				}
			}
			if (value.is(Type.CONTEXT)) {
				try {
					Scope context = (Scope)value.getContext();
					if (!breadcrumbs.contains(context)) {
						builder.append(context.serializeSymbols(name + ".", new SingleLinkedList<>( context, breadcrumbs ) ) );
					}
				} catch (EvaluationException e) {
					// ignore
				}
			}
		}
		return builder.toString();
	}

	public String toString() {
		return serializeSymbols();
	}

	private static class SingleLinkedList<T> {
		private final T head;
		private final SingleLinkedList<T> tail;
		public SingleLinkedList(T head, SingleLinkedList<T> tail) {
			this.head = head;
			this.tail = tail;
		}
		public boolean contains(T value) {
			return value == head || (tail != null && tail.contains(value));
		}
	}

}
