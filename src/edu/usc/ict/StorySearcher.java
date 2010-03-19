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

	    StorySearcher searcher = new StorySearcher(cmd.getOptionValue("index"), "ict");

	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    String query;
	    while ((query = br.readLine()) != null) 
		searcher.process(query, System.out);
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    private Manager queryingManager;

    public StorySearcher(String path, String prefix) {
	System.setProperty("terrier.home", ".");
	System.setProperty("terrier.var", ".");

	Index index = Index.createIndex(path, prefix);
	queryingManager = new Manager(index);
    }

    public void process(String query, OutputStream out) {
	PrintWriter writer = new PrintWriter(out);

	SearchRequest srq = queryingManager.newSearchRequest("queryID0", query);
	srq.addMatchingModel("Matching", "PL2");
	srq.setControl("decorate", "on");
	queryingManager.runPreProcessing(srq);
	queryingManager.runMatching(srq);
	queryingManager.runPostProcessing(srq);
	queryingManager.runPostFilters(srq);
	ResultSet rs = srq.getResultSet();

	int[] docids = rs.getDocids();
	double[] scores = rs.getScores();
	String[] urls = rs.getMetaItems("url");
	String[] titles = rs.getMetaItems("title");
	String[] texts = rs.getMetaItems("text");
	String[] xs = rs.getMetaItems("x");

	int size = Math.min(docids.length, 10);
	for (int i = 0; i < size; ++i) {
	    writer.println("---------------------------------------------------------");
	    writer.println(Integer.toString(docids[i]) + " " + Double.toString(scores[i]));
	    if (xs != null) writer.println(xs[i]);
	    if (urls != null) writer.println("url: " + urls[i]);
	    if (titles != null) writer.println("title: " + titles[i]);
	    if (texts != null) writer.println("text: " + texts[i]);
	}
    }
}
