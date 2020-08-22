package org.search.codesearch.index.matcher;

import org.search.codesearch.index.BoyerMoore;
import org.search.codesearch.index.FileTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

import static java.lang.ThreadLocal.withInitial;

public class InMemoryFileContentMatcher implements ContentMatcher {

    private final BiFunction<String, String, Boolean> matchFunction;
    public static final int MAX_PATTERNS = 100;

    private static final ThreadLocal<Map<String, BoyerMoore>> patternCache = withInitial(() -> new LinkedHashMap<String, BoyerMoore>() {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_PATTERNS;
        }
    });

    public InMemoryFileContentMatcher(BiFunction<String, String, Boolean> matchFunction) {
        this.matchFunction = matchFunction;
    }

    private static final Logger logger = LoggerFactory.getLogger(InMemoryFileContentMatcher.class);

    private final ConcurrentMap<Path, String> fileContent = new ConcurrentHashMap<>();

    public static ContentMatcher create(MatchType value) {
        return value.create();
    }

    @Override
    public boolean match(Path p, String pattern) {
        if (!FileTypes.isTextFile(p))
            return false;
        try {
            readIfRequired(p);
            //return false;
            String data = fileContent.get(p);
            return matchFunction.apply(pattern, data);
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
            return false;
        }

    }

    public static boolean bruteMatch(String patter, String wholeText) {
        return wholeText.contains(patter);
    }

    public static boolean boyerMatch(String patter, String wholeText) {
        BoyerMoore bm = patternCache.get().computeIfAbsent(patter, p -> new BoyerMoore(p));
        return bm.search(wholeText) >= 0;
    }

    private void readIfRequired(Path p) {
        fileContent.computeIfAbsent(p, f -> loadFile(f));
    }

    private String loadFile(Path p) {
        try {
            logger.info("Loading {}", p);
            return new String(Files.readAllBytes(p)).toLowerCase();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
