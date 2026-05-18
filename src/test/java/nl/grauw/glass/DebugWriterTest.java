package nl.grauw.glass;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DebugWriterTest extends TestBase {

	@Test
	public void testLabelSection() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("debug.asm");
		Files.write(sourcePath, Arrays.asList(
			"start: nop",
			"space: ds 2",
			"data: db 1",
			"word: DW 2",
			"const: equ 3",
			"sum: equ const + 10"
		));

		assertIterableEquals(
			s(
				label("start", sourcePath, 1, "LABEL", "0"),
				label("space", sourcePath, 2, "DS", "1"),
				label("data", sourcePath, 3, "DC", "3"),
				label("word", sourcePath, 4, "DC", "4"),
				label("const", sourcePath, 5, "EQU", "3"),
				label("sum", sourcePath, 6, "EQU", "13"),
				map(0, 1, "code"),
				map(1, 6, "data")
			),
			debug(sourcePath)
		);
	}

	@Test
	public void testIncludedLabels() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("debugInclude.asm");
		Path includePath = temporaryDirectory.resolve("debugIncluded.asm");
		Files.write(sourcePath, Arrays.asList(
			"main: nop",
			" include \"debugIncluded.asm\""
		));
		Files.write(includePath, Collections.singletonList(
			"included: db 1"
		));

		assertIterableEquals(
			s(
				label("main", sourcePath, 1, "LABEL", "0"),
				label("included", includePath, 1, "DC", "1"),
				map(0, 1, "code"),
				map(1, 2, "data")
			),
			debug(sourcePath)
		);
	}

	@Test
	public void testSelectedIfLabels() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("debugIf.asm");
		Files.write(sourcePath, Arrays.asList(
			" IF 0",
			"thenLabel: db 1",
			" ELSE",
			"elseLabel: db 2",
			" ENDIF"
		));

		assertIterableEquals(
			s(
				label("elseLabel", sourcePath, 4, "DC", "0"),
				map(0, 1, "data")
			),
			debug(sourcePath)
		);
	}

	@Test
	public void testProcLocalLabels() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("debugProc.asm");
		Files.write(sourcePath, Arrays.asList(
			"outer: nop",
			"procLabel: PROC",
			"inner: nop",
			" ENDP"
		));

		assertIterableEquals(
			s(
				label("outer", sourcePath, 1, "LABEL", "0"),
				label("procLabel", sourcePath, 2, "LABEL", "1"),
				label(".inner", sourcePath, 3, "LABEL", "1"),
				map(0, 2, "code")
			),
			debug(sourcePath)
		);
	}

	@Test
	public void testLabelUsages() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("debugUsages.asm");
		Files.write(sourcePath, Arrays.asList(
			"target: nop",
			" ld hl,target",
			" jp target",
			"procLabel: PROC",
			"local: nop",
			" jr local",
			" ENDP"
		));

		assertIterableEquals(
			s(
				label("target", sourcePath, 1, "LABEL", "0"),
				label("procLabel", sourcePath, 4, "LABEL", "7"),
				label(".local", sourcePath, 5, "LABEL", "7"),
				map(0, 10, "code"),
				"target:1,4",
				".local:8"
			),
			debug(sourcePath)
		);
	}

	@TempDir
	static Path temporaryDirectory;

	private List<String> debug(Path sourcePath) throws IOException {
		SourceBuilder sourceBuilder = new SourceBuilder(Collections.singletonList(temporaryDirectory));
		Source source = sourceBuilder.parse(sourcePath);
		source.assemble(new Assembler.NullOutputStream());

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			new DebugWriter(new PrintStream(outputStream)).write(source);
			String debug = outputStream.toString();
			return debug.isEmpty() ? Collections.emptyList() : Arrays.asList(debug.split("\\R"));
		}
	}

	private String label(String name, Path sourcePath, int lineNumber, String type) {
		return name + ": " + sourcePath.toAbsolutePath().normalize() + "," + lineNumber + "," + type;
	}

	private String label(String name, Path sourcePath, int lineNumber, String type, String value) {
		return label(name, sourcePath, lineNumber, type) + "," + value;
	}

	private String map(int start, int end, String type) {
		return start + ":" + end + ":" + type;
	}

}
