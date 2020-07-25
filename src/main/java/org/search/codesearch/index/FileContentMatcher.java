package org.search.codesearch.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FileContentMatcher implements ContentMatcher {
    private static final Logger logger = LoggerFactory.getLogger(FileContentMatcher.class);
    private List<String> textExt = Arrays.asList(".java", ".properties", ".c", ".cpp");

    @Override
    public boolean match(Path p, String pattern) {
        try {
            if (isTextFile(p)) {
                Optional<String> match = Files.lines(p)
                        .map(String::toLowerCase)
                        .filter(line -> line.contains(pattern))
                        .findFirst();
                return match.map($ -> true).orElse(false);
            }
        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
        }
        return false;
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
