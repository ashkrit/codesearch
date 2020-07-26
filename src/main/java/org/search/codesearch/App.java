package org.search.codesearch;

import org.search.codesearch.args.ArgsParser;
import org.search.codesearch.index.Search;
import org.search.codesearch.index.cache.DiskCacheFileTreeCodeSearch;
import org.search.codesearch.index.matcher.InMemoryFileContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
        List<Search> searchAlgo = Arrays.asList(
                new DiskCacheFileTreeCodeSearch(locations, InMemoryFileContentMatcher.create(), new File("\\tmp\\db\\file.db"))
                //new BruteForceCodeSearch(locations)
        );

        logger.info("Start search now ....");


        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String p = scanner.nextLine();
            if (p.trim().isEmpty()) continue;

            for (Search search : searchAlgo) {
                search.match(p.toLowerCase(), file -> logger.info("Found {}", file), 1000);
            }
        }

    }

}
