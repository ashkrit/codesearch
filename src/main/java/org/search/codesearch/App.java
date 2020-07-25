package org.search.codesearch;

import org.search.codesearch.args.ArgsParser;
import org.search.codesearch.index.BruteForceCodeSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Map<String, String> params = ArgsParser.cmdParams(args);

        String rootPath = params.get("source");
        logger.info("Searching {}", rootPath);

        Search search = new BruteForceCodeSearch(rootPath);
        search.match("benchmark", file -> logger.info("Found {}", file));
    }

}
