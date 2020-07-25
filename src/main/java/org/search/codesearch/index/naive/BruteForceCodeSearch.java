package org.search.codesearch.index.naive;

import org.search.codesearch.Search;
import org.search.codesearch.index.ContentMatcher;
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
    private final String rootPath;

    public BruteForceCodeSearch(String rootPath) {
        this.rootPath = rootPath;
        this.matchers = Arrays.asList(matchFileName());
    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(String pattern, Consumer<Path> consumer) {
        long start = System.currentTimeMillis();
        FileProcessor visitor = new FileProcessor(consumer, this.matchers, pattern);
        try {
            walkFileTree(Paths.get(rootPath), visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            long total = System.currentTimeMillis() - start;
            logger.info("Took {} ms for search term {}", total, pattern);
            logger.info("Folder visited {} , files visited {} , files processed {} ", visitor.folderVisited(), visitor.filesVisited(), visitor.filesProcessed());
        }
    }

}
