package org.search.codesearch.index.cache;

import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.suffix.ConcurrentSuffixTree;
import com.googlecode.concurrenttrees.suffix.SuffixTree;
import org.search.codesearch.Search;
import org.search.codesearch.index.ContentMatcher;
import org.search.codesearch.index.FileContentMatcher;
import org.search.codesearch.index.naive.LiveFileTreeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.Files.walkFileTree;

public class CacheFileTreeCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(CacheFileTreeCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;
    private final SuffixTree<Long> fileTreeCache = new ConcurrentSuffixTree<>(new DefaultCharArrayNodeFactory());

    public CacheFileTreeCodeSearch(List<String> rootPaths) {
        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), new FileContentMatcher());

        LiveFileTreeProcessor visitor = new LiveFileTreeProcessor(this::record, Arrays.asList((x, y) -> true), null, Integer.MAX_VALUE);
        long start = System.currentTimeMillis();
        try {
            Stream<Path> paths = rootPath.stream().map(Paths::get);
            paths.forEach(path -> walk(visitor, path));
        } finally {
            long total = System.currentTimeMillis() - start;
            logger.info("Took {} ms to create index", total);
            logger.info("Folder visited {} , files visited {} , files matched {} ", visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed());
        }

    }

    private void record(Path p) {
        fileTreeCache.put(p.toFile().getAbsolutePath(), p.toFile().length());
    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(String pattern, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        CachedFileTreeProcessor processor = new CachedFileTreeProcessor(consumer, matchers, pattern, limit);
        try {
            Stream<Path> paths = rootPath.stream().map(Paths::get);
            paths.parallel().forEach(p -> processor.matchFromLocation(filesToScan(p)));
        } finally {
            long total = System.currentTimeMillis() - start;
            logger.info("Took {} ms for search term {}", total, pattern);
            logger.info("files visited {} , files matched {} ", processor.filesVisited(), processor.filesProcessed());
        }
    }

    private Iterable<CharSequence> filesToScan(Path p) {
        return fileTreeCache.getKeysContaining(p.toFile().getAbsolutePath());
    }

    private void walk(LiveFileTreeProcessor visitor, Path path) {
        try {
            walkFileTree(path, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
