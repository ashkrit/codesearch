package org.search.codesearch.index.cache;

import org.search.codesearch.index.matcher.ContentMatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

public class CachedFileTreeProcessor {
    private final Consumer<Path> consumer;
    private final List<ContentMatcher> matchers;
    private final String pattern;
    private final int limit;

    private final LongAdder filesVisited = new LongAdder();
    private final LongAdder filesProcessed = new LongAdder();
    private final LongAdder bytesRead = new LongAdder();

    public CachedFileTreeProcessor(List<ContentMatcher> matchers, Consumer<Path> consumer, String pattern, int limit) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.pattern = pattern;
        this.limit = limit;
    }

    public long filesProcessed() {
        return filesProcessed.longValue();
    }

    public long filesVisited() {
        return filesVisited.longValue();
    }

    public long bytesRead() {
        return bytesRead.longValue();
    }


    public void search(String f) {
        //filesVisited.increment();
        if (limitReached()) {
            return;
        }
        match(f);
    }

    private boolean limitReached() {
        return filesProcessed.intValue() >= this.limit;
    }

    private void match(String filePath) {
        Path fileToCheck = Paths.get(filePath);
        updateReadBytes(fileToCheck);

        Optional<ContentMatcher> match = matchers.stream()
                .filter(x -> x.match(fileToCheck, pattern))
                .findFirst();

        match.ifPresent($ -> {
            consumer.accept(fileToCheck);
            filesProcessed.increment();
        });
    }

    private void updateReadBytes(Path fileToCheck) {
        //bytesRead.add(fileToCheck.toFile().length());
    }
}
