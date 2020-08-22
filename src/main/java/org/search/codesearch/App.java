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

import static java.util.Objects.requireNonNull;
import static org.search.codesearch.index.matcher.MatchType.*;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static Map<String, Function<List<String>, Search>> searchAlgo = new HashMap<String, Function<List<String>, Search>>() {{
        put("naive", paths -> new BruteForceCodeSearch(paths));
        put("cache", x -> new CacheFileTreeCodeSearch(x, InMemoryFileContentMatcher.create(StringContains)));
        put("bcache", x -> new CacheFileTreeCodeSearch(x, InMemoryFileContentMatcher.create(BoyerMoor)));
    }};

    public static void main(String[] args) {
        Map<String, String> params = ArgsParser.cmdParams(args);

        if (params.isEmpty()) {
            throw new IllegalArgumentException("Params missing or eg -source /github/codesearch");
        }

        Search search = createSearch(params);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String p = scanner.nextLine();
            if (p.trim().isEmpty()) continue;
            search.match(p.toLowerCase(), file -> logger.info("Found {}", file), 1000_000);
        }

    }

    private static Search createSearch(Map<String, String> params) {
        logger.info("Search params {}", params);
        String rootPath = params.get("source");
        List<String> locations = Arrays.asList(rootPath.split(";"));
        String key = algoToUse(params);
        Function<List<String>, Search> searchBuilder = searchAlgo.get(key);

        requireNonNull(searchBuilder, String.format("Unable to find %s, supported algo are %s", key, searchAlgo.keySet()));

        Search search = searchBuilder.apply(locations);

        logger.info("Start search now ....");
        return search;
    }

    private static String algoToUse(Map<String, String> params) {
        return params.getOrDefault("algo", "bcache");
    }

}
