package org.search.codesearch.index.cache;

import org.search.codesearch.index.matcher.ContentMatcher;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
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

    public CachedFileTreeProcessor(Consumer<Path> consumer, List<ContentMatcher> matchers, String pattern, int limit) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.pattern = pattern;
        this.limit = limit;
    }

    public void matchFromLocation(Iterable<CharSequence> files) {
        files.forEach(file -> {
            filesVisited.incrementAndGet();

            if (filesProcessed.get() >= this.limit) {
                return;
            }
            searchInFile(file.toString());
        });
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

    public void matchFromLocation(Iterator<String> files) {
        while (files.hasNext()) {
            filesVisited.incrementAndGet();

            if (filesProcessed.get() >= this.limit) {
                return;
            }

            searchInFile(files.next());
        }

    }

    public void searchFileContent(String f) {
        filesVisited.incrementAndGet();
        if (filesProcessed.get() >= this.limit) {
            return;
        }
        searchInFile(f);
    }

    private void searchInFile(String next) {
        Path fileToCheck = Paths.get(next);
        bytesRead.addAndGet(fileToCheck.toFile().length());
        Optional<ContentMatcher> match = matchers.stream()
                .filter(x -> x.match(fileToCheck, pattern))
                .findFirst();
        match.ifPresent($ -> {
            consumer.accept(fileToCheck);
            filesProcessed.incrementAndGet();
        });
    }
}
