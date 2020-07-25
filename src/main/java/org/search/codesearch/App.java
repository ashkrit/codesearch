package org.search.codesearch;

import org.search.codesearch.args.ArgsParser;
import org.search.codesearch.index.naive.BruteForceCodeSearch;
import org.search.codesearch.index.cache.CacheFileTreeCodeSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Map<String, String> params = ArgsParser.cmdParams(args);

        String rootPath = params.get("source");
        String term = params.get("term");
        logger.info("Searching {} for term {}", rootPath, term);

        List<String> locations = Arrays.asList(rootPath.split(";"));
        List<Search> searchAlgos = Arrays.asList(new BruteForceCodeSearch(locations), new CacheFileTreeCodeSearch(locations));

        logger.info("Start search now ....");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String p = scanner.nextLine();
            for (Search search : searchAlgos) {
                search.match(p.toLowerCase(), file -> logger.info("Found {}", file), 1000);
            }
        }
    }

}
