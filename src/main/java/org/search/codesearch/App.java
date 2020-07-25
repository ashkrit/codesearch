package org.search.codesearch;

import org.search.codesearch.args.ArgsParser;
import org.search.codesearch.index.naive.BruteForceCodeSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Map<String, String> params = ArgsParser.cmdParams(args);

        String rootPath = params.get("source");
        String term = params.get("term");
        logger.info("Searching {} for term {}", rootPath, term);

        Search search = new BruteForceCodeSearch(rootPath);
        search.match(term, file -> logger.info("Found {}", file));
    }

}
