package org.tbc.glass;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.function.Executable;

import org.tbc.glass.Parser.SyntaxError;
import org.tbc.glass.Scope.SymbolNotFoundException;
import org.tbc.glass.SourceFile.SourceFileSpan;
import org.tbc.glass.expressions.EvaluationException;
import org.tbc.glass.expressions.Expression;
import org.tbc.glass.expressions.ExpressionBuilder.ExpressionError;
import org.tbc.glass.instructions.ArgumentException;
import org.tbc.glass.instructions.Error.ErrorDirectiveException;

public class TestBase {

	public static byte[] b(int... values) {
		byte[] bytes = new byte[values.length];
		for (int i = 0; i < values.length; i++)
			bytes[i] = (byte)values[i];
		return bytes;
	}

	public static List<String> s(String... values) {
		return Arrays.asList(values);
	}

	public Expression parseExpression(String text) {
		Parser parser = new Parser(new SourceFile(text));
		parser.skipToArgumentStartState("test");
		return parser.parse(new Scope()).getArguments();
	}

	public static AssemblyException assertAssemblyException(int line, Executable executable) {
		return assertAssemblyException(line, line + 1, executable);
	}

	public static AssemblyException assertAssemblyException(int lineStart, int lineEnd, Executable executable) {
		AssemblyException exception = assertThrows(AssemblyException.class, executable);
		assertAssemblyExceptionSpanEquals(lineStart, lineEnd, -1, exception);
		return exception;
	}

	public static SyntaxError assertSyntaxError(int lineStart, int lineEnd, int column, Executable executable) {
		SyntaxError exception = assertThrows(SyntaxError.class, executable);
		assertAssemblyExceptionSpanEquals(lineStart, lineEnd, column, exception);
		return exception;
	}

	public static ExpressionError assertExpressionError(int lineStart, int lineEnd, int column, Executable executable) {
		ExpressionError exception = assertThrows(ExpressionError.class, executable);
		assertAssemblyExceptionSpanEquals(lineStart, lineEnd, column, exception);
		return exception;
	}

	public static EvaluationException assertEvaluationException(int line, Executable executable) {
		EvaluationException exception = assertThrows(EvaluationException.class, executable);
		assertAssemblyExceptionSpanEquals(line, line + 1, -1, exception);
		return exception;
	}

	public static ArgumentException assertArgumentException(int line, Executable executable) {
		ArgumentException exception = assertThrows(ArgumentException.class, executable);
		assertAssemblyExceptionSpanEquals(line, line + 1, -1, exception);
		return exception;
	}

	public static SymbolNotFoundException assertSymbolNotFoundException(String name, int line, Executable executable) {
		SymbolNotFoundException exception = assertThrows(SymbolNotFoundException.class, executable);
		assertEquals(name, exception.getName(), "name");
		assertAssemblyExceptionSpanEquals(line, line + 1, -1, exception);
		return exception;
	}

	public static ErrorDirectiveException assertErrorDirectiveException(String message, int line, Executable executable) {
		ErrorDirectiveException exception = assertThrows(ErrorDirectiveException.class, executable);
		assertEquals(message, exception.getPlainMessage(), "message");
		assertAssemblyExceptionSpanEquals(line, line + 1, -1, exception);
		return exception;
	}

	public static void assertAssemblyExceptionSpanEquals(int lineStart, int lineEnd, int column, AssemblyException e) {
		assertEquals( true, !e.getContexts()
                              .isEmpty(), "has context" );
		SourceFileSpan span = e.getContexts().getFirst();
		assertEquals(lineStart, span.lineStart, "lineStart");
		assertEquals(lineEnd, span.lineEnd, "lineEnd");
		assertEquals(column, span.column, "column");
	}

}