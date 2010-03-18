package edu.usc.ict;

import org.apache.log4j.Logger;
import org.apache.commons.cli.*;
import org.terrier.indexing.*;
import java.io.*;

public class StoryIndexer {
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Option[] optionArray = {
	    OptionBuilder.withLongOpt("help").withDescription("Show this help screen").create(),
	    OptionBuilder.withLongOpt("verbose").withDescription("Show verbose output").create(),
	    OptionBuilder.withLongOpt("index").withArgName("path").hasArgs(1).withDescription("Path to the index").create(),
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

	    if (!cmd.hasOption("index")) throw new MissingOptionException("Missing option --index");

	    // NOTE: Prefix defaults "ict"
	    StoryIndexer indexer = new StoryIndexer(cmd.getOptionValue("index", "index.unnamed"), "ict");
	    indexer.process(filenames);
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    public static final String defaultCollectionClassName = "edu.usc.ict.terrier.Spinn3rCollection";
    public static final String defaultIndexerClassName = "org.terrier.indexing.BasicSinglePassIndexer";

    private String indexPath;
    private String indexPrefix;
    private Class<? extends Collection> collectionClass; 
    private Class<? extends Indexer> indexerClass;

    public StoryIndexer(String path, String prefix) throws ClassNotFoundException { 
	this(path, prefix, defaultIndexerClassName, defaultCollectionClassName); 
    }

    public StoryIndexer(String path, String prefix, String indexerClassName, String collectionClassName) 
	throws ClassNotFoundException 
    { 
	indexPath = path;  
	indexPrefix = prefix;
	indexerClass = Class.forName(indexerClassName).asSubclass(Indexer.class);
	collectionClass = Class.forName(collectionClassName).asSubclass(Collection.class);
    }

    public void process(String[] filenames) throws Exception
    {
	System.setProperty("terrier.home", ".");
	System.setProperty("terrier.var", ".");
	Indexer indexer = indexerClass.getConstructor(new Class[]{ String.class, String.class })
	    .newInstance(new Object[]{ indexPath, indexPrefix });

	Collection collection = collectionClass.getConstructor(new Class[]{ String[].class })
	    .newInstance(new Object[]{ filenames });

	indexer.index(new Collection[]{ collection });
    }
}
