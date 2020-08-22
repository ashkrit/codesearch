package org.search.codesearch.index.cache;

import org.search.codesearch.index.SearchMetrics;
import org.search.codesearch.index.matcher.ContentMatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CachedFileTreeProcessor {
    private final Consumer<Path> consumer;
    private final List<ContentMatcher> matchers;
    private final String pattern;
    private final int limit;
    private final SearchMetrics metrics = new SearchMetrics();

    public CachedFileTreeProcessor(List<ContentMatcher> matchers, Consumer<Path> consumer, String pattern, int limit) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.pattern = pattern;
        this.limit = limit;
    }

    public SearchMetrics getMetrics() {
        return metrics;
    }

    public void search(String f) {
        metrics.recordFileVisited();
        if (limitReached()) {
            return;
        }
        match(f);
    }

    private boolean limitReached() {
        return metrics.filesProcessed() >= this.limit;
    }

    private void match(String filePath) {
        Path fileToCheck = Paths.get(filePath);
        updateReadBytes(fileToCheck);

        Optional<ContentMatcher> match = matchers.stream()
                .filter(x -> x.match(fileToCheck, pattern))
                .findFirst();

        match.ifPresent($ -> {
            consumer.accept(fileToCheck);
            metrics.recordFileProcessed();
        });
    }

    private void updateReadBytes(Path fileToCheck) {
        // This is expensive metrics as it involve IO
        //metrics.recordBytesRead(fileToCheck.toFile().length());
    }
}
