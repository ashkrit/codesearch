package org.search.codesearch.index.matcher;

import org.search.codesearch.index.FileTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryFileContentMatcher implements ContentMatcher {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryFileContentMatcher.class);
    public static final InMemoryFileContentMatcher instance = new InMemoryFileContentMatcher();

    private final ConcurrentMap<String, String> fileContent = new ConcurrentHashMap<>();

    public static InMemoryFileContentMatcher create() {
        return instance;
    }

    @Override
    public boolean match(Path p, String pattern) {
        if (!FileTypes.isTextFile(p))
            return false;

        try {
            File file = p.toFile();
            readIfRequired(file);
            String data = fileContent.get(file.getAbsolutePath());
            return data.contains(pattern);
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
            return false;
        }

    }

    private void readIfRequired(File p) {
        fileContent.computeIfAbsent(p.getAbsolutePath(), f -> loadFile(p.toPath()));
    }

    private String loadFile(Path p) {
        try {
            logger.info("Loading {}", p);
            return new String(Files.readAllBytes(p)).toLowerCase();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
