package nl.grauw.glass;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import nl.grauw.glass.SourceFile.SourceFileSpan;

public class Assembler {

    private final Source source;
	private static final int MZF_HEADER_SIZE = 128;

	/**
     */
	static void main( String[] args ) {
		try {
			run(args);
		} catch (AssemblyException e) {
			System.err.println(formatError(e));
			System.exit(1);
		}
	}

	private static void run(String[] args) {
		if (args.length == 0) {
			System.out.printf( "%s %s by %s%n",
                               Assembler.class.getPackage().getImplementationTitle(),
                               Assembler.class.getPackage().getImplementationVersion(),
                               Assembler.class.getPackage().getImplementationVendor()
                             );
			System.out.println();
			System.out.println("Usage: java -jar glass.jar [OPTION] SOURCE [OBJECT] [SYMBOL]");
			System.exit(1);
		}

		Path sourcePath = null;
		Path objectPath = null;
		Path symbolPath = null;
		Path listPath = null;
		Path debugPath = null;
		OutputType outputType = OutputType.BIN;
		List<Path> includePaths = new ArrayList<>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-I")) {
				if (++i >= args.length)
					throw new AssemblyException("Missing argument value.");
				includePaths.add(Paths.get(args[i]));
			} else if (args[i].equals("-L")) {
				if (++i >= args.length)
					throw new AssemblyException("Missing argument value.");
				listPath = Paths.get(args[i]);
			} else if (args[i].equals("-D")) {
				if (++i >= args.length)
					throw new AssemblyException("Missing argument value.");
				debugPath = Paths.get(args[i]);
			} else if (args[i].equals("-O")) {
				if (++i >= args.length)
					throw new AssemblyException("Missing argument value.");
				outputType = OutputType.parse(args[i]);
			} else if (sourcePath == null) {
				sourcePath = Paths.get(args[i]);
			} else if (objectPath == null) {
				objectPath = Paths.get(args[i]);
			} else if (symbolPath == null) {
				symbolPath = Paths.get(args[i]);
			} else {
				throw new AssemblyException("Too many arguments.");
			}
		}

		Assembler instance = new Assembler( sourcePath, includePaths );
		if (outputType == OutputType.MZF)
			objectPath = withMzfExtension(objectPath);
		instance.writeObject( objectPath, outputType );
		if (debugPath != null)
			instance.writeDebug( debugPath );
		if (symbolPath != null)
			instance.writeSymbols( symbolPath );
		if (listPath != null)
			instance.writeList( listPath );
	}

	private static String formatError(AssemblyException e) {
		SourceFileSpan context = !e.getContexts().isEmpty() ? e.getContexts().get(0) : null;
		String path = context != null && context.getSourceFile().getPath() != null ?
			context.getSourceFile().getPath().toAbsolutePath().normalize().toString() : "<unknown>";
		int line = context != null ? context.lineStart + 1 : 1;
		int column = context != null && context.column != -1 ? context.column + 1 : 1;
		return path + ":" + line + ":" + column + ": error: " + e.getPlainMessage();
	}

	public Assembler(Path sourcePath, List<Path> includePaths) {
		source = new SourceBuilder(includePaths).parse(sourcePath);
	}

	public void writeObject(Path objectPath) {
		writeObject(objectPath, OutputType.BIN);
	}

	public void writeObject(Path objectPath, OutputType outputType) {
		try (OutputStream output = objectPath != null ? createBufferedOutputStream(objectPath) : new NullOutputStream()) {
			byte[] object = assemble();
			if (outputType == OutputType.MZF)
				object = createMzfObject(object);
			output.write(object, 0, object.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] assemble() throws IOException {
		try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
			source.assemble(output);
			return output.toByteArray();
		}
	}

	private byte[] createMzfObject(byte[] object) {
		Source.MzfConfig mzfConfig = source.getMzfConfig();
		if (!mzfConfig.hasLoadAddress())
			throw new AssemblyException("MZF_LOAD is required for MZF output.");

		int size = MZF_HEADER_SIZE + object.length;
		checkWord("MZF size", size);
		checkWord("MZF_LOAD", mzfConfig.getLoadAddress());
		checkWord("MZF_START", mzfConfig.getStartAddress());

		byte[] mzfObject = new byte[size];
		mzfObject[0] = 1;
		writeTextField(mzfObject, 1, 17, mzfConfig.getTitle(), true);
		writeWord(mzfObject, 18, size);
		writeWord(mzfObject, 20, mzfConfig.getLoadAddress());
		writeWord(mzfObject, 22, mzfConfig.getStartAddress());
		writeTextField(mzfObject, 24, 104, mzfConfig.getComments(), false);
		System.arraycopy(object, 0, mzfObject, MZF_HEADER_SIZE, object.length);
		return mzfObject;
	}

	private void writeTextField(byte[] output, int offset, int length, String text, boolean terminate) {
		int maxTextLength = terminate ? length - 1 : length;
		int copyLength = Math.min(text.length(), maxTextLength);
		for (int i = 0; i < copyLength; i++)
			output[offset + i] = Mz700Charset.encode(text.charAt(i));
		if (terminate)
			output[offset + copyLength] = 0x0D;
	}

	private void writeWord(byte[] output, int offset, int value) {
		output[offset] = (byte)value;
		output[offset + 1] = (byte)(value >> 8);
	}

	private void checkWord(String field, int value) {
		if (value < 0 || value > 0xFFFF)
			throw new AssemblyException(field + " out of range: " + value);
	}

	public void writeSymbols(Path symbolPath) {
		try (PrintStream symbolOutput = new PrintStream(createBufferedOutputStream(symbolPath))) {
			symbolOutput.print(source.getScope().serializeSymbols());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeList(Path listPath) {
		try (PrintStream output = new PrintStream(createBufferedOutputStream(listPath))) {
			new ListingWriter(output).write(source);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void writeDebug(Path debugPath) {
		try (PrintStream output = new PrintStream(createBufferedOutputStream(debugPath))) {
			new DebugWriter(output).write(source);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static OutputStream createBufferedOutputStream(Path path) throws IOException {
		return new BufferedOutputStream(Files.newOutputStream(path), 0x10000);
	}

	private static Path withMzfExtension(Path path) {
		if (path == null)
			return null;
		String fileName = path.getFileName().toString();
		int extensionIndex = fileName.lastIndexOf('.');
		String mzfFileName = (extensionIndex != -1 ? fileName.substring(0, extensionIndex) : fileName) + ".mzf";
		Path parent = path.getParent();
		return parent != null ? parent.resolve(mzfFileName) : Paths.get(mzfFileName);
	}

	public static class NullOutputStream extends OutputStream {
		public void write(int b) throws IOException {}
		public void write(byte[] b) throws IOException {}
		public void write(byte[] b, int off, int len) throws IOException {}
	}

	public enum OutputType {
		BIN,
		MZF;

		public static OutputType parse(String value) {
			if ("BIN".equals(value))
				return BIN;
			if ("MZF".equals(value))
				return MZF;
			throw new AssemblyException("Unsupported output type: " + value);
		}
	}

}
