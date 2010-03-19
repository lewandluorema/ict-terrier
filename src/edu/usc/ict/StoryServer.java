package edu.usc.ict;

import org.apache.log4j.Logger;
import org.apache.commons.cli.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class StoryServer {
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
		formatter.printHelp("StoryServer [options..]", options);
		return;
	    }

	    if (!cmd.hasOption("index")) throw new MissingOptionException("Missing option --index");

	    // NOTE: Prefix defaults "ict"
	    StoryServer server = new StoryServer(cmd.getOptionValue("index", "index.unnamed"), "ict");
	    server.start();
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    private HttpServer httpServer;

    public StoryServer(String path, String prefix) {
	StorySearcher searcher = new StorySearcher(path, prefix);

	try {
	    httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
	    httpServer.createContext("/search/", new SearchHandler(searcher));
	    httpServer.createContext("/stats/", new StatsHandler());
	}
	catch (IOException e) { e.printStackTrace(); }
    }

    public void start() { httpServer.start(); }
    public void stop(int delay) { httpServer.stop(delay); }
}

class SearchHandler implements HttpHandler {
    private StorySearcher searcher;
    public SearchHandler(StorySearcher searcher) { this.searcher = searcher; }

    public void handle(HttpExchange exchange) {
	int rCode = 200;

	Headers headers = exchange.getResponseHeaders();
	headers.add("Content-Type", "text/xml; charset=UTF-8");

	try {
	    String path = exchange.getRequestURI().getPath();
	    String basePath = exchange.getHttpContext().getPath();
	    String query = URLDecoder.decode(path.substring(basePath.length()), "UTF-8");
	    String message = searcher.process(query);

	    exchange.sendResponseHeaders(rCode, 0); 
	    PrintWriter writer = new PrintWriter(exchange.getResponseBody());
	    writer.print(message);
	    writer.close();
	    System.err.println(path + " " + message.length());
	}
	catch (UnsupportedEncodingException e) { }
	catch (IOException e) { e.printStackTrace(); }
	finally { exchange.close(); }
    }
}

class StatsHandler implements HttpHandler {
    public void handle(HttpExchange exchange) {
	try { exchange.sendResponseHeaders(200, 0l); }
	catch (IOException e) {}
	finally { exchange.close(); }
    }
}

