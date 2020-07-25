package org.search.codesearch.index.matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryFileContentMatcher implements ContentMatcher {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFileContentMatcher.class);
    private final List<String> textExt = Arrays.asList(".java", ".properties", ".c", ".cpp");
    private final ConcurrentMap<String, String> fileContent = new ConcurrentHashMap<>();
    public static final InMemoryFileContentMatcher instance = new InMemoryFileContentMatcher();

    public static InMemoryFileContentMatcher create() {
        return instance;
    }

    @Override
    public boolean match(Path p, String pattern) {
        try {
            if (isTextFile(p)) {
                File file = p.toFile();
                readIfRequired(p);
                String data = fileContent.get(file.getAbsolutePath());
                return data.contains(pattern);
            }
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
        }
        return false;
    }

    private void readIfRequired(Path p) {
        fileContent.computeIfAbsent(p.toFile().getAbsolutePath(), f -> loadFile(p));
    }

    private String loadFile(Path p) {
        logger.info("Loading {}", p);
        try {
            return new String(Files.readAllBytes(p));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private boolean isTextFile(Path p) {
        File f = p.toFile();
        for (int index = 0; index < textExt.size(); index++) {
            if (f.getName().endsWith(textExt.get(index))) {
                return true;
            }
        }
        return false;
    }
}
