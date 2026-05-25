package nl.grauw.glass;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ListFileTest extends TestBase {

	@Test
	public void testSimple() {
		assertIterableEquals(
			s(
				"# source: null",
				"   1	0000	00				\tnop",
				"   2	0001	FF				\trst 38H",
				"   3	0002	C3	02	00		\tjp $",
				"   4	0005					"
			),
			list(
				"\tnop",
				"\trst 38H",
				"\tjp $"
			)
		);
	}

	@Test
	public void testSection() {
		assertIterableEquals(
			s(
				"# source: null",
				"   1	0000	00				\tnop",
				"   2	0001					\tSECTION s",
				"   6	0001	00				\tnop",
				"   7	0002	FF	C3	03	00	s: ds 4",
				"   3	0002	FF				\trst 38H",
				"   4	0003	C3	03	00		\tjp $",
				"   5	0006					\tENDS",
				"   8	0006	00				\tnop",
				"   9	0007					"
			),
			list(
				"\tnop",
				"\tSECTION s",
				"\trst 38H",
				"\tjp $",
				"\tENDS",
				"\tnop",
				"s: ds 4",
				"\tnop"
			)
		);
	}

	@Test
	public void testInclude() throws IOException {
		Path testInclude = temporaryDirectory.resolve("testInclude.asm");
		Files.write(testInclude, Arrays.asList(
			"\trst 38H",
			"\tjp $"
		));

		assertIterableEquals(
			s(
				"# source: null",
				"   1	0000	00				\tnop",
				"   2	0001					\tINCLUDE \"testInclude.asm\"",
				"# source: " + testInclude,
				"   1	0001	FF				\trst 38H",
				"   2	0002	C3	02	00		\tjp $",
				"   3	0005					",
				"# source: null",
				"   3	0005	00				\tnop",
				"   4	0006					"
			),
			list(
				"\tnop",
				"\tINCLUDE \"testInclude.asm\"",
				"\tnop"
			)
		);
	}

	@Test
	public void testMacroParameters() {
		assertIterableEquals(
			s(
				"# source: null",
				"   1	0000					LDW: MACRO dest,src",
				"   5	0000	MACRO				 LDW BC,DE",
				"   2	0000	D5					PUSH DE",
				"   3	0001	C1					POP BC",
				"   4	0002					\tENDM",
				"   6	0002					"
			),
			list(
				"LDW: MACRO dest,src",
				" PUSH src",
				" POP dest",
				"\tENDM",
				" LDW BC,DE"
			)
		);
	}

	@Test
	public void testNestedMacroParameters() {
		assertIterableEquals(
			s(
				"# source: null",
				"   1	0000					LDW: MACRO dest,src",
					"   5	0000					LDB: MACRO dest,(src)",
					"  11	0000	MACRO				 LDB C,(DE)",
					"   6	0000	E5					PUSH HL",
					"   7	0001	MACRO				\tLDW HL, DE",
					"   2	0001	D5					PUSH DE",
				"   3	0002	E1					POP HL",
				"   4	0003					\tENDM",
				"   8	0003	4E					LD C, (HL)",
				"   9	0004	E1					POP HL",
				"  10	0005					\tENDM",
				"  12	0005					"
			),
			list(
				"LDW: MACRO dest,src",
				" PUSH src",
				" POP dest",
				"\tENDM",
				"LDB: MACRO dest,(src)",
				" PUSH HL",
				"\tLDW HL,src",
				" LD dest,(HL)",
				" POP HL",
				"\tENDM",
				" LDB C,(DE)"
			)
		);
	}

	@Test
	public void testMacroIfParameters() {
		assertIterableEquals(
			s(
					"# source: null",
					"   1	0000					ADDB: MACRO dest,src",
					"  10	0000	MACRO				 ADDB C,E",
					"   5	0000	79					LD A, C",
					"   6	0001	83					ADD A, E",
					"   7	0002	4F					LD C, A",
					"   8	0003						ENDIF",
					"   9	0003						ENDM",
					"  11	0003					"
				),
			list(
				"ADDB: MACRO dest,src",
				" IF dest = A",
				" ERROR \"Invalid destination. A is not allowed.\"",
				" ELSE",
				"\tLD A,dest",
				"\tADD A,src",
				"\tLD dest,A",
				" ENDIF",
				" ENDM",
				" ADDB C,E"
			)
		);
	}

	@TempDir
	static Path temporaryDirectory;

	public List<String> list(String... sourceLines) {
		SourceBuilder sourceBuilder = new SourceBuilder( Collections.singletonList( temporaryDirectory ) );
		Source source = sourceBuilder.parse(new SourceFile(String.join("\n", sourceLines)));
		String list;
		try {
			source.assemble(new Assembler.NullOutputStream());
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				try (PrintStream printStream = new PrintStream( outputStream, false, StandardCharsets.UTF_8 )) {
					new ListingWriter(new PrintStream(outputStream)).write(source);
				}
				list = outputStream.toString( StandardCharsets.UTF_8 );
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return list.isEmpty() ? Collections.emptyList() : Arrays.asList(list.split("\\R"));
	}

}
