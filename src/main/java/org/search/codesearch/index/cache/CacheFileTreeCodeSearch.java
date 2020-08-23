package org.search.codesearch.index.cache;

import org.search.codesearch.index.Search;
import org.search.codesearch.index.SearchMetrics;
import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.matcher.ContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheFileTreeCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(CacheFileTreeCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;
    private final List<File> files;

    public CacheFileTreeCodeSearch(List<String> rootPaths, ContentMatcher fileContentMatcher) {
        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), fileContentMatcher);
        this.files = loadFiles(rootPath.stream().map(Paths::get));
    }

    private List<File> loadFiles(Stream<Path> paths) {
        long start = System.currentTimeMillis();

        AtomicLong fCount = new AtomicLong();
        List<File> locations = paths.flatMap(this::walkSingleLocation)
                .map(Path::toFile)
                .filter(File::isFile)
                .peek(x -> {
                    long current = fCount.incrementAndGet();
                    if (current % 10_000 == 0) {
                        logger.info("Reading file {}", current);
                    }
                })
                .collect(Collectors.toList());

        LongSummaryStatistics summary = locations
                .stream()
                .parallel()
                .collect(Collectors.summarizingLong(File::length));

        Optional<File> f = locations.stream()
                .parallel().max(Comparator.comparingLong(File::length));
        String summaryText = f.map(file -> file.getAbsolutePath() + " (" + file.length() + ")").orElse("NA");
        logger.info("Max file {}", summaryText);
        long total = System.currentTimeMillis() - start;

        logger.info("File summary stats Avg {} KB , Max file {} KB, Total {} KB", summary.getAverage() / 1024, summary.getMax() / 1024, summary.getSum() / 1024);
        logger.info("Loaded {} files and took {} Second", locations.size(), total / 1000);

        return locations;

    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(SearchQuery query, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        CachedFileTreeProcessor processor = new CachedFileTreeProcessor(matchers, consumer, limit, query);

        files.parallelStream()
                .forEach(f -> processor.search(f.getAbsolutePath()));

        long total = System.currentTimeMillis() - start;
        logger.info("Took {} ms for search term {}", total, query);
        SearchMetrics metrics = processor.getMetrics();
        logger.info("files visited {} , files matched {} , bytes read {} KB ",
                metrics.filesVisited(), metrics.filesProcessed(), metrics.bytesRead() / 1024);
    }

    private Stream<Path> walkSingleLocation(Path path) {
        try {
            return Files.walk(path)
                    .filter(this::isNonGit);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isNonGit(Path f) {
        return !f.toFile().getAbsolutePath().contains(".git");
    }

}
