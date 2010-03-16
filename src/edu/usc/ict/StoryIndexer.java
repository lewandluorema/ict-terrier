package edu.usc.ict;

import org.apache.commons.cli.*;
import org.terrier.indexing.CollectionFactory;
import java.io.*;
import java.util.zip.GZIPInputStream;

public class StoryIndexer {
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Option[] optionArray = {
	    OptionBuilder.withLongOpt("help").withDescription("Show this help screen").create(),
	    OptionBuilder.withLongOpt("verbose").withDescription("Show verbose output").create(),
	    OptionBuilder.withLongOpt("index").withArgName("path").hasArgs(1).withDescription("Path to the index").create("i"),
	};

	Options options = new Options();
	for (Option o: optionArray) options.addOption(o);

	CommandLineParser parser = new GnuParser();

	try {
	    CommandLine cmd = parser.parse(options, args);
	    String[] filenames = cmd.getArgs();

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("StoryIndexer [options..] file..", options);
		return;
	    }

	    StoryIndexer indexer = new StoryIndexer(cmd.getOptionValue("index"));
	    indexer.process(filenames);
	}
	catch (ParseException e) {
	    System.err.println("common-cli: " + e.getMessage());
	}
    }

    private String indexPath;

    public StoryIndexer(String indexPath) {
	this.indexPath = indexPath;
    }

    public void process(String[] filenames) {
	for (String filename: filenames) {
	    try {
		InputStream in = new GZIPInputStream(new FileInputStream(filename));
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line = reader.readLine();
		while (line != null) {
		    System.out.println(line);
		    line = reader.readLine();
		}
	    }
	    catch (FileNotFoundException e) {}
	    catch (UnsupportedEncodingException e) {}
	    catch (IOException e) {}
	}
    }
}
