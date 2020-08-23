package org.search.codesearch.index.naive;

import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.io.FileTypes;
import org.search.codesearch.matcher.ContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import static java.nio.file.FileVisitResult.*;

public class LiveFileTreeProcessor extends SimpleFileVisitor<Path> {

    private static final Logger logger = LoggerFactory.getLogger(LiveFileTreeProcessor.class);

    private final Consumer<Path> consumer;
    private final List<ContentMatcher> matchers;
    private final int limit;
    private final AtomicLong filesVisited = new AtomicLong();
    private final AtomicLong folderVisited = new AtomicLong();
    private final AtomicLong filesProcessed = new AtomicLong();
    private final AtomicLong bytesRead = new AtomicLong();
    private final SearchQuery query;


    public LiveFileTreeProcessor(SearchQuery query, Consumer<Path> consumer, List<ContentMatcher> matchers, int limit) {
        this.consumer = consumer;
        this.matchers = matchers;
        this.limit = limit;
        this.query = query;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        File file = dir.toFile();
        if (skipProcessing(file)) {
            return SKIP_SUBTREE;
        } else {
            return CONTINUE;
        }
    }

    private boolean skipProcessing(File file) {
        return file.isHidden() || FileTypes.isGitFolder(file);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        filesVisited.incrementAndGet();
        if (FileTypes.isCompiledFile(file.toFile())) {
            return SKIP_SIBLINGS;
        }

        bytesRead.addAndGet(file.toFile().length());
        Optional<ContentMatcher> match = matchers.stream()
                .filter(x -> match(file, x))
                .findFirst();

        match.ifPresent($ -> {
            consumer.accept(file);
            filesProcessed.incrementAndGet();
        });

        return filesProcessed() < this.limit ? CONTINUE : FileVisitResult.TERMINATE;
    }

    private boolean match(Path fileToCheck, ContentMatcher x) {
        for (String p : query.patterns) {
            if (x.match(fileToCheck, p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        folderVisited.incrementAndGet();
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        logger.error("Failed", exc);
        return CONTINUE;
    }

    public long filesVisited() {
        return filesVisited.longValue();
    }


    public long folderVisited() {
        return folderVisited.longValue();
    }

    public long filesProcessed() {
        return filesProcessed.longValue();
    }

    public long bytesRead() {
        return bytesRead.get();
    }
}
