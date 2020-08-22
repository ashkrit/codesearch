package org.search.codesearch.index.naive;

import org.search.codesearch.index.Search;
import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.index.matcher.ContentMatcher;
import org.search.codesearch.index.matcher.OnDemandFileContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.file.Files.walkFileTree;

public class BruteForceCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(BruteForceCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;

    public BruteForceCodeSearch(List<String> rootPaths) {
        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), new OnDemandFileContentMatcher());
    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(SearchQuery query, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        LiveFileTreeProcessor visitor = new LiveFileTreeProcessor(query, consumer, this.matchers, limit);
        try {
            Stream<Path> paths = rootPath.stream().map(Paths::get);
            paths.forEach(path -> walk(visitor, path));
        } finally {
            long total = System.currentTimeMillis() - start;
            logger.info("Took {} ms for search term {}", total, query);
            logger.info("Folder visited {} , files visited {} , files matched {} , bytes read {} KB ",
                    visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed(), visitor.bytesRead() / 1024);
        }
    }

    private void walk(LiveFileTreeProcessor visitor, Path path) {
        try {
            walkFileTree(path, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
