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

    public static final int MAX_PATTERNS = 100;
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFileContentMatcher.class);
    private static final ThreadLocal<Map<String, BoyerMoore>> patternCache = withInitial(() -> sizeLimitMap());

    private final BiFunction<String, String, Boolean> matchFunction;
    private final ConcurrentMap<Path, String> fileContent = new ConcurrentHashMap<>();

    public InMemoryFileContentMatcher(BiFunction<String, String, Boolean> matchFunction) {
        this.matchFunction = matchFunction;
    }

    public static ContentMatcher create(MatchType value) {
        return value.create();
    }

    @Override
    public boolean match(Path p, String pattern) {
        if (!FileTypes.isTextFile(p))
            return false;
        try {
            readIfRequired(p);
            String data = fileContent.get(p);
            return matchFunction.apply(pattern, data);
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
            return false;
        }

    }

    public static boolean bruteMatch(String pattern, String wholeText) {
        return wholeText.contains(pattern);
    }

    public static boolean boyerMatch(String pattern, String wholeText) {
        BoyerMoore bm = readPatternCache(pattern);
        return bm.search(wholeText) >= 0;
    }

    private static BoyerMoore readPatternCache(String patter) {
        return patternCache.get().computeIfAbsent(patter, p -> new BoyerMoore(p));
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

    private static Map<String, BoyerMoore> sizeLimitMap() {
        return new LinkedHashMap<String, BoyerMoore>() {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > MAX_PATTERNS;
            }
        };
    }
}
