package nl.grauw.glass;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class AssemblerTest extends TestBase {

	@Test
	public void testMzfOutput() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("testMzf.asm");
		Path objectPath = temporaryDirectory.resolve("testMzf.mzf");
		Files.write(sourcePath, Arrays.asList(
			" MZF_TITLE the game!",
			" MZF_LOAD 1200H",
			" MZF_START 1234H",
			" MZF_COMMENTS comments",
			" nop",
			" rst 38H"
		));

		new Assembler(sourcePath, Collections.singletonList(temporaryDirectory))
			.writeObject(objectPath, Assembler.OutputType.MZF);

		byte[] output = Files.readAllBytes(objectPath);
		assertEquals(130, output.length);
		assertEquals(1, output[0] & 0xFF);
		assertFieldEquals(new int[] { 0x96, 0x98, 0x92, ' ', 0x97, 0xA1, 0xB3, 0x92, '!', 0x0D }, output, 1, 17);
		assertWordEquals(130, output, 18);
		assertWordEquals(0x1200, output, 20);
		assertWordEquals(0x1234, output, 22);
		assertFieldEquals(new int[] { 0x9F, 0xB7, 0xB3, 0xB3, 0x92, 0xB0, 0x96, 0xA4 }, output, 24, 104);
		assertEquals(0x00, output[128] & 0xFF);
		assertEquals(0xFF, output[129] & 0xFF);
	}

	@Test
	public void testMzfDefaults() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("testMzfDefaults.asm");
		Path objectPath = temporaryDirectory.resolve("testMzfDefaults.mzf");
		Files.write(sourcePath, Arrays.asList(
			" MZF_LOAD 1200H",
			" nop"
		));

		new Assembler(sourcePath, Collections.singletonList(temporaryDirectory))
			.writeObject(objectPath, Assembler.OutputType.MZF);

		byte[] output = Files.readAllBytes(objectPath);
		assertFieldEquals("TITLE\r", output, 1, 17);
		assertWordEquals(0x1200, output, 20);
		assertWordEquals(0x1200, output, 22);
	}

	@Test
	public void testMzfLoadRequired() throws IOException {
		Path sourcePath = temporaryDirectory.resolve("testMzfLoadRequired.asm");
		Path objectPath = temporaryDirectory.resolve("testMzfLoadRequired.mzf");
		Files.write(sourcePath, Collections.singletonList(
			" nop"
		));

		AssemblyException exception = assertThrows(AssemblyException.class, () ->
			new Assembler(sourcePath, Collections.singletonList(temporaryDirectory))
				.writeObject(objectPath, Assembler.OutputType.MZF)
		);
		assertEquals("MZF_LOAD is required for MZF output.", exception.getPlainMessage());
	}

	@TempDir
	static Path temporaryDirectory;

	private void assertWordEquals(int expected, byte[] output, int offset) {
		assertEquals(expected & 0xFF, output[offset] & 0xFF);
		assertEquals(expected >> 8 & 0xFF, output[offset + 1] & 0xFF);
	}

	private void assertFieldEquals(String expected, byte[] output, int offset, int length) {
		for (int i = 0; i < length; i++) {
			int expectedValue = i < expected.length() ? expected.charAt(i) : 0;
			assertEquals(expectedValue, output[offset + i] & 0xFF, "offset " + (offset + i));
		}
	}

	private void assertFieldEquals(int[] expected, byte[] output, int offset, int length) {
		for (int i = 0; i < length; i++) {
			int expectedValue = i < expected.length ? expected[i] : 0;
			assertEquals(expectedValue, output[offset + i] & 0xFF, "offset " + (offset + i));
		}
	}

}
