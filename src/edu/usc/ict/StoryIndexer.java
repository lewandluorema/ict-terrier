package edu.usc.ict;

import org.apache.commons.cli.*;

public class StoryIndexer {
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Options options = new Options();
	options.addOption("h", false, "Show this help screen");

	CommandLineParser parser = new GnuParser();

	try {
	    CommandLine cmd = parser.parse(options, args);
	}
	catch (ParserException e) {
	    System.err.println("common-cli: " + e.getMessage());
	}
    }
}
