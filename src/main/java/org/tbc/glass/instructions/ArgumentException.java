package org.tbc.glass.instructions;

import org.tbc.glass.AssemblyException;

import java.io.Serial;

public class ArgumentException extends AssemblyException {
	@Serial private static final long serialVersionUID = 1L;

	public ArgumentException() {
		this("Invalid arguments.");
	}

	public ArgumentException(String message) {
		super(message);
	}

}