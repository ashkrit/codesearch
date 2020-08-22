package org.search.codesearch.index.cache;

import org.search.codesearch.index.Search;
import org.search.codesearch.index.matcher.ContentMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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

        List<File> locations = paths.flatMap(this::walkSingleLocation)
                .map(Path::toFile)
                .filter(File::isFile)
                .collect(Collectors.toList());

        long total = System.currentTimeMillis() - start;
        logger.info("Loaded {} locations and took {}", locations.size(), total);

        return locations;

    }

    private ContentMatcher matchFileName() {
        return (p, t) -> p.toFile().getName().toLowerCase().contains(t);
    }

    @Override
    public void match(String pattern, Consumer<Path> consumer, int limit) {
        long start = System.currentTimeMillis();
        CachedFileTreeProcessor processor = new CachedFileTreeProcessor(matchers, consumer, pattern, limit);

        files.parallelStream()
                .forEach(f -> processor.search(f.getAbsolutePath()));

        long total = System.currentTimeMillis() - start;
        logger.info("Took {} ms for search term {}", total, pattern);
        logger.info("files visited {} , files matched {} , bytes read {} KB ",
                processor.filesVisited(), processor.filesProcessed(), processor.bytesRead() / 1024);

    }

    private Stream<Path> walkSingleLocation(Path path) {
        try {
            return Files.walk(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
