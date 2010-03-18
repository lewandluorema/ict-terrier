package edu.usc.ict;

import org.apache.commons.cli.*;
import org.terrier.indexing.*;
import org.terrier.matching.ResultSet;
import org.terrier.querying.Manager;
import org.terrier.querying.SearchRequest;
import org.terrier.structures.Index;
import java.io.*;

public class StorySearcher {
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
		formatter.printHelp("StorySearcher [options..] file..", options);
		return;
	    }

	    if (!cmd.hasOption("index")) throw new MissingOptionException("Missing option --index");

	    System.setProperty("terrier.home", ".");
	    System.setProperty("terrier.var", ".");

	    Index index = Index.createIndex(cmd.getOptionValue("index"), "ict");
	    Manager queryingManager = new Manager(index);
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String query;

	    while ((query = br.readLine()) != null) {
		SearchRequest srq = queryingManager.newSearchRequest("queryID0", query);
		srq.addMatchingModel("Matching", "PL2");
		srq.setControl("decorate", "on");
		//--------------------------------------------------
		// srq.setControl("summaries", "text");
		//-------------------------------------------------- 
		queryingManager.runPreProcessing(srq);
		queryingManager.runMatching(srq);
		queryingManager.runPostProcessing(srq);
		queryingManager.runPostFilters(srq);
		ResultSet rs = srq.getResultSet();

		int[] docids = rs.getDocids();
		double[] scores = rs.getScores();
		String[] urls = rs.getMetaItems("url");
		String[] texts = rs.getMetaItems("title");

		int size = Math.min(docids.length, 10);
		for (int i = 0; i < size; ++i) {
		    System.out.println(Integer.toString(docids[i]) + " " + Double.toString(scores[i]));
		    System.out.println(urls[i]);
		    System.out.println(texts[i]);
		}

		//--------------------------------------------------
		// String[] metaKeys = rs.getMetaKeys();
		// for (String key: metaKeys) {
		//     System.out.println(key + ": " + );
		//-------------------------------------------------- 
	    }
	//--------------------------------------------------
	//     // NOTE: Prefix defaults "ict"
	//     StorySearcher searcher = new StorySearcher(cmd.getOptionValue("index", "index.unnamed"), "ict");
	//     indexer.process(filenames);
	//-------------------------------------------------- 
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }
}
