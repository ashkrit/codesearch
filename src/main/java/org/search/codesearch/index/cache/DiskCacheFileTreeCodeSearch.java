package org.search.codesearch.index.cache;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.search.codesearch.index.Search;
import org.search.codesearch.index.matcher.ContentMatcher;
import org.search.codesearch.index.naive.LiveFileTreeProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.Files.walkFileTree;

public class DiskCacheFileTreeCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(DiskCacheFileTreeCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;
    private final MVMap<String, Long> fileTreeCache;
    private final MVStore store;

    public DiskCacheFileTreeCodeSearch(List<String> rootPaths, ContentMatcher fileContentMatcher, File cache) {

        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), fileContentMatcher);
        this.store = MVStore.open(cache.getAbsolutePath());
        this.fileTreeCache = this.store.openMap("filecache");
        buildFileTreeIndex();
    }

    private void buildFileTreeIndex() {

        logger.info("Building index");
        LiveFileTreeProcessor visitor = new LiveFileTreeProcessor(this::record, Arrays.asList((x, y) -> true), null, Integer.MAX_VALUE);
        long start = System.currentTimeMillis();

        Stream<Path> paths = rootPath.stream().map(Paths::get);
        paths.forEach(path -> walk(visitor, path));

        long total = System.currentTimeMillis() - start;

        logger.info("Total files indexed {} ", fileTreeCache.size());
        logger.info("Took {} ms to create index", total);
        logger.info("Folder visited {} , files visited {} , files matched {} , bytes read {} kb ",
                visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed(), visitor.bytesRead() / 1024);

    }

    private void record(Path p) {
        long s = fileTreeCache.size();
        if (s % 10_000 == 0) {
            logger.info("Indexed {} files", s);
        }
        File file = p.toFile();
        fileTreeCache.put(file.getAbsolutePath(), file.length());
    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(String pattern, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        CachedFileTreeProcessor processor = new CachedFileTreeProcessor(matchers, consumer, pattern, limit);

        fileTreeCache
                .keyList()
                .parallelStream()
                .forEach(processor::search);

        long total = System.currentTimeMillis() - start;
        logger.info("Took {} ms for search term {}", total, pattern);
        logger.info("files visited {} , files matched {} , bytes read {} KB ",
                processor.filesVisited(), processor.filesProcessed(), processor.bytesRead() / 1024);

    }


    private void walk(LiveFileTreeProcessor visitor, Path path) {
        try {
            walkFileTree(path, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
