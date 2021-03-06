package org.search.codesearch.index.cache;

import org.search.codesearch.index.Search;
import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.matcher.ContentMatcher;
import org.search.codesearch.metrics.IndexMetrics;
import org.search.codesearch.metrics.SearchMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.search.codesearch.metrics.IndexMetricsCollector.summarizingFile;

public class CacheFileTreeCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(CacheFileTreeCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;
    private final List<File> files;
    private final IndexMetrics indexMetrics = new IndexMetrics();

    public CacheFileTreeCodeSearch(List<String> rootPaths, ContentMatcher fileContentMatcher) {
        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), fileContentMatcher);
        logger.info("Indexing {}", rootPaths);
        this.files = loadFiles(rootPath.stream().map(Paths::get));
    }

    private List<File> loadFiles(Stream<Path> paths) {
        long start = System.currentTimeMillis();

        List<File> locations = identifyFiles(paths);
        LongSummaryStatistics summary = collectFileSizeMetrics(locations);

        long total = System.currentTimeMillis() - start;

        logger.info("File summary stats Avg {} KB , Max file {} KB, Total {} KB", summary.getAverage() / 1024, summary.getMax() / 1024, summary.getSum() / 1024);
        logger.info("Index Summary {}", indexMetrics);
        logger.info("Loaded {} files and took {} Second", locations.size(), total / 1000);

        return locations;

    }

    private LongSummaryStatistics collectFileSizeMetrics(List<File> locations) {
        LongSummaryStatistics summary = locations
                .stream()
                .parallel()
                .collect(Collectors.summarizingLong(File::length));

        Optional<File> f = locations.stream()
                .parallel().max(Comparator.comparingLong(File::length));
        String summaryText = f.map(file -> file.getAbsolutePath() + " (" + file.length() + ")").orElse("NA");

        logger.info("Max file {}", summaryText);
        return summary;
    }

    private List<File> identifyFiles(Stream<Path> paths) {
        return paths.flatMap(this::walkSingleLocation)
                .map(Path::toFile)
                .filter(File::isFile)
                .parallel()
                .peek(file -> summarizingFile(indexMetrics, file))
                .collect(Collectors.toList());
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
