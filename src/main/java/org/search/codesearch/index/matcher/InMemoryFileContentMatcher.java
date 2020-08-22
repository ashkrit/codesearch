package org.search.codesearch.index.matcher;

import org.search.codesearch.index.BoyerMoore;
import org.search.codesearch.index.FileTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

public class InMemoryFileContentMatcher implements ContentMatcher {

    private final BiFunction<String, String, Boolean> matchFunction;
    public static final int MAX_PATTERNS = 100;

    private static final ThreadLocal<Map<String, BoyerMoore>> patternCache = ThreadLocal.withInitial(() -> new LinkedHashMap<String, BoyerMoore>() {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_PATTERNS;
        }
    });

    public InMemoryFileContentMatcher(BiFunction<String, String, Boolean> matchFunction) {
        this.matchFunction = matchFunction;
    }

    public enum MatchType {
        StringContains {
            public InMemoryFileContentMatcher create() {
                return new InMemoryFileContentMatcher(InMemoryFileContentMatcher::bruteMatch);
            }
        },
        BoyerMoor {
            public InMemoryFileContentMatcher create() {
                return new InMemoryFileContentMatcher(InMemoryFileContentMatcher::boyerMatch);
            }
        };

        public InMemoryFileContentMatcher create() {
            throw new IllegalArgumentException("Not supported");
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(InMemoryFileContentMatcher.class);

    private final ConcurrentMap<String, String> fileContent = new ConcurrentHashMap<>();

    public static InMemoryFileContentMatcher create(MatchType value) {
        return value.create();
    }

    private static boolean bruteMatch(String patter, String wholeText) {
        return wholeText.contains(patter);
    }

    private static boolean boyerMatch(String patter, String wholeText) {
        BoyerMoore bm = patternCache.get().computeIfAbsent(patter, p -> new BoyerMoore(p));
        return bm.search(wholeText) >= 0;
    }

    @Override
    public boolean match(Path p, String pattern) {
        if (!FileTypes.isTextFile(p))
            return false;
        try {
            File file = p.toFile();
            readIfRequired(file);
            String data = fileContent.get(file.getAbsolutePath());
            return matchFunction.apply(pattern, data);
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
            return false;
        }

    }

    private void readIfRequired(File p) {
        fileContent.computeIfAbsent(p.getAbsolutePath(), f -> loadFile(p.toPath()));
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
