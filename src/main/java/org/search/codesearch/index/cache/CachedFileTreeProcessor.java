package org.search.codesearch.index.cache;

import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.matcher.ContentMatcher;
import org.search.codesearch.metrics.SearchMetrics;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CachedFileTreeProcessor {
    private final Consumer<Path> consumer;
    private final List<ContentMatcher> matchers;
    private final int limit;
    private final SearchMetrics metrics = new SearchMetrics();
    private final SearchQuery query;

    public CachedFileTreeProcessor(List<ContentMatcher> matchers, Consumer<Path> consumer, int limit, SearchQuery query) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.limit = limit;
        this.query = query;
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
                .filter(matcher -> match(matcher, fileToCheck))
                .findFirst();

        match.ifPresent($ -> {
            consumer.accept(fileToCheck);
            metrics.recordFileProcessed();
        });
    }

    private boolean match(ContentMatcher matcher, Path file) {
        for (String p : query.patterns) {
            if (matcher.match(file, p)) {
                return true;
            }
        }
        return false;
    }

    private void updateReadBytes(Path fileToCheck) {
        // This is expensive metrics as it involve IO
        //metrics.recordBytesRead(fileToCheck.toFile().length());
    }
}
