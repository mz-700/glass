package nl.grauw.glass.expressions;

import nl.grauw.glass.AssemblyException;

import java.io.Serial;

public class EvaluationException extends AssemblyException {
	@Serial private static final long serialVersionUID = 1L;

	public EvaluationException() {
		super();
	}

	public EvaluationException(String message) {
		super(message);
	}

	public EvaluationException(Throwable cause) {
		super(cause);
	}

	public EvaluationException(String message, Throwable cause) {
		super(message, cause);
	}

}