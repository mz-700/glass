package nl.grauw.glass;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.grauw.glass.SourceFile.SourceFileSpan;

public class AssemblyException extends RuntimeException {
	@Serial private static final long serialVersionUID = 1L;

	private final List<SourceFileSpan> contexts = new ArrayList<>();

	public AssemblyException() {
		this((Throwable)null);
	}

	public AssemblyException(String message) {
		this(message, null);
	}

	public AssemblyException(Throwable cause) {
		this("Error during assembly.", null);
	}

	public AssemblyException(String message, Throwable cause) {
		super(message, cause);
	}

	public void addContext(SourceFileSpan sourceSpan) {
		contexts.add(sourceSpan);
	}

	@Override
	public String getMessage() {
		StringBuilder message = new StringBuilder( super.getMessage() );

		for (SourceFileSpan context : contexts)
			message.append( "\n" )
                   .append( context );

		return message.toString();
	}

	public String getPlainMessage() {
		return super.getMessage();
	}

	public List<SourceFileSpan> getContexts() {
		return Collections.unmodifiableList(contexts);
	}

}