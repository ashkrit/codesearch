package org.search.codesearch;

import org.search.codesearch.args.ArgsParser;
import org.search.codesearch.index.Search;
import org.search.codesearch.index.cache.CacheFileTreeCodeSearch;
import org.search.codesearch.index.matcher.InMemoryFileContentMatcher;
import org.search.codesearch.index.naive.BruteForceCodeSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static Map<String, Function<List<String>, Search>> searchAlgo = new HashMap<String, Function<List<String>, Search>>() {{
        put("naive", x -> new BruteForceCodeSearch(x));
        put("cache", x -> new CacheFileTreeCodeSearch(x, InMemoryFileContentMatcher.create()));
    }};

    public static void main(String[] args) {
        Map<String, String> params = ArgsParser.cmdParams(args);

        logger.info("Search params {}", params);
        String rootPath = params.get("source");
        List<String> locations = Arrays.asList(rootPath.split(";"));

        String key = algoToUse(params);
        Function<List<String>, Search> searchBuilder = searchAlgo.get(key);

        Objects.requireNonNull(searchBuilder, String.format("Unable to find %s, supported algo are %s", key, searchAlgo.keySet()));

        Search search = searchBuilder.apply(locations);

        logger.info("Start search now ....");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String p = scanner.nextLine();
            if (p.trim().isEmpty()) continue;
            search.match(p.toLowerCase(), file -> logger.info("Found {}", file), 1000);

        }

    }

    private static String algoToUse(Map<String, String> params) {
        String algo = params.get("algo");
        return (algo == null) ? "cache" : algo;
    }

}
