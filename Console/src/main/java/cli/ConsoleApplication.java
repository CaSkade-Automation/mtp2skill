package console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import mapping.Mapping;

public class ConsoleApplication {
	Mapping mapping = new Mapping();

	public void run(String[] args) {
		CommandLine line = parseArguments(args);

		if (line.hasOption("filename")) {

			String fileName = line.getOptionValue("filename");
			// String file = readData(fileName);
			mapping.executeMapping(fileName);
		}
	}

	private CommandLine parseArguments(String[] args) {
		Options options = getOptions();
		CommandLine line = null;

		CommandLineParser parser = new DefaultParser();

		try {
			line = parser.parse(options, args);

		} catch (ParseException ex) {

			System.err.println("Failed to parse command line arguments");
			System.err.println(ex.toString());

			System.exit(1);
		}
		return line;
	}

	private Options getOptions() {

		Options options = new Options();

		options.addOption("f", "filename", true, "file name to load data from");
		return options;
	}
}
