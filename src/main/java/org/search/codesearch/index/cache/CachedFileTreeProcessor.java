package org.search.codesearch.index.cache;

import org.search.codesearch.index.matcher.ContentMatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class CachedFileTreeProcessor {
    private final Consumer<Path> consumer;
    private final List<ContentMatcher> matchers;
    private final String pattern;
    private final int limit;

    private final AtomicLong filesVisited = new AtomicLong();
    private final AtomicLong filesProcessed = new AtomicLong();
    private final AtomicLong bytesRead = new AtomicLong();

    public CachedFileTreeProcessor(List<ContentMatcher> matchers, Consumer<Path> consumer, String pattern, int limit) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.pattern = pattern;
        this.limit = limit;
    }

    public long filesProcessed() {
        return filesProcessed.get();
    }

    public long filesVisited() {
        return filesVisited.get();
    }

    public long bytesRead() {
        return bytesRead.get();
    }


    public void search(String f) {
        filesVisited.incrementAndGet();
        if (limitReached()) {
            return;
        }
        match(f);
    }

    private boolean limitReached() {
        return filesProcessed.get() >= this.limit;
    }

    private void match(String filePath) {
        Path fileToCheck = Paths.get(filePath);
        updateReadBytes(fileToCheck);

        Optional<ContentMatcher> match = matchers.stream()
                .filter(x -> x.match(fileToCheck, pattern))
                .findFirst();

        match.ifPresent($ -> {
            consumer.accept(fileToCheck);
            filesProcessed.incrementAndGet();
        });
    }

    private void updateReadBytes(Path fileToCheck) {
        bytesRead.addAndGet(fileToCheck.toFile().length());
    }
}
