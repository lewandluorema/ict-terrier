package edu.usc.ict;

import org.apache.commons.cli.*;

public class StoryIndexer {
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Option[] optionArray = {
	    OptionBuilder.withLongOpt("help").withDescription("Show this help screen").create(),
	    OptionBuilder.withLongOpt("verbose").withDescription("Show verbose output").create()
	};

	Options options = new Options();
	for (Option o: optionArray) options.addOption(o);

	CommandLineParser parser = new GnuParser();

	try {
	    CommandLine cmd = parser.parse(options, args);

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("StoryIndexer [options..] file..", options);
		return;
	    }
	}
	catch (ParseException e) {
	    System.err.println("common-cli: " + e.getMessage());
	}
    }
}
