package edu.usc.ict;

import org.apache.log4j.Logger;
import org.apache.commons.cli.*;
import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;

public class StoryServer {
    public static Logger logger = Logger.getLogger(StoryServer.class);
    public static void main(String[] args) {
	//--------------------------------------------------
	// CLI stuff
	//-------------------------------------------------- 
	Option[] optionArray = {
	    OptionBuilder.withLongOpt("help").withDescription("Show this help screen").create(),
	    OptionBuilder.withLongOpt("port").withArgName("number").hasArgs(1).
		withDescription("Port to listen.  Defaults '8080'").create(),
	};

	Options options = new Options();
	for (Option o: optionArray) options.addOption(o);

	CommandLineParser parser = new GnuParser();

	try {
	    CommandLine cmd = parser.parse(options, args);
	    String[] filenames = cmd.getArgs();

	    if (cmd.hasOption("help")) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("StoryServer [options..] index-path [index-path..]\n", 
		    "", options, 
		    "\nNOTE: <index-path> has to be absolute path");
		return;
	    }

	    // NOTE: Prefix defaults "ict"
	    StoryServer server = new StoryServer(filenames, "ict", 
		Integer.parseInt(cmd.getOptionValue("port", "8080")));
	    server.start();
	}
	catch (ParseException e) { System.err.println("common-cli: " + e.getMessage()); }
	catch (Exception e) { e.printStackTrace(); }
    }

    private HttpServer httpServer;

    public StoryServer(String[] pathes, String prefix, int port) {
	try {
	    logger.info("Launch the HTTP server at port " + port);
	    httpServer = HttpServer.create(new InetSocketAddress(port), 0);

	    for (String path: pathes) {
		if (path.endsWith("/")) path = path.substring(0, path.lastIndexOf("/"));
		if (path.lastIndexOf('/') == -1) throw new IOException();
		String name = path.substring(path.lastIndexOf('/') + 1);
		String context = "/search/" + name + "/";
		
		StorySearcher searcher = new StorySearcher(path, prefix);

		logger.info("Register context " + context + " for the index " + path);
		httpServer.createContext(context, new SearchHandler(searcher));
	    }

	    logger.info("Register context /stats");
	    httpServer.createContext("/stats", new StatsHandler());
	}
	catch (IOException e) { e.printStackTrace(); }
    }

    public void start() { httpServer.start(); }
    public void stop(int delay) { httpServer.stop(delay); }
}

class SearchHandler implements HttpHandler {
    public static Logger logger = Logger.getLogger(SearchHandler.class);
    private StorySearcher searcher;
    public SearchHandler(StorySearcher searcher) { this.searcher = searcher; }

    public void handle(HttpExchange exchange) {
	int rCode = 200;

	Headers headers = exchange.getResponseHeaders();
	headers.add("Content-Type", "text/xml; charset=UTF-8");

	try {
	    URI uri = exchange.getRequestURI();
	//--------------------------------------------------
	//     logger.info("GET " + uri);
	//-------------------------------------------------- 

	    String path = uri.getPath();
	    String basePath = exchange.getHttpContext().getPath();
	    String query = URLDecoder.decode(path.substring(basePath.length()), "UTF-8");
	    String message = searcher.process(query);

	    exchange.sendResponseHeaders(rCode, 0); 
	    PrintWriter writer = new PrintWriter(exchange.getResponseBody());
	    writer.print(message);
	    writer.close();
	}
	catch (UnsupportedEncodingException e) { }
	catch (IOException e) { e.printStackTrace(); }
	finally { exchange.close(); }
    }
}

class StatsHandler implements HttpHandler {
    public static Logger logger = Logger.getRootLogger();
    public void handle(HttpExchange exchange) {
	Headers headers = exchange.getResponseHeaders();
	headers.add("Content-Type", "text/xml; charset=UTF-8");

	try { 
	    exchange.sendResponseHeaders(200, 0l); 
	    PrintWriter writer = new PrintWriter(exchange.getResponseBody());
	    writer.print("<?xml version=\"1.0\"?><stats />");
	    writer.close();
	}
	catch (IOException e) {}
	finally { exchange.close(); }
    }
}

