package org.search.codesearch.index.naive;

import org.search.codesearch.Search;
import org.search.codesearch.index.ContentMatcher;
import org.search.codesearch.index.FileContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static java.nio.file.Files.walkFileTree;

public class BruteForceCodeSearch implements Search {

    private static final Logger logger = LoggerFactory.getLogger(BruteForceCodeSearch.class);
    private final List<ContentMatcher> matchers;
    private final List<String> rootPath;

    public BruteForceCodeSearch(List<String> rootPaths) {
        this.rootPath = rootPaths;
        this.matchers = Arrays.asList(matchFileName(), new FileContentMatcher());
    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(String pattern, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        FileProcessor visitor = new FileProcessor(consumer, this.matchers, pattern, limit);
        try {
            rootPath.stream().map(Paths::get).forEach(path -> walk(visitor, path));
        } finally {
            long total = System.currentTimeMillis() - start;
            logger.info("Took {} ms for search term {}", total, pattern);
            logger.info("Folder visited {} , files visited {} , files matched {} ", visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed());
        }
    }

    private void walk(FileProcessor visitor, Path path) {
        try {
            walkFileTree(path, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
