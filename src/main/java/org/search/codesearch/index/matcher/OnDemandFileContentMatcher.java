package org.search.codesearch.index.matcher;

import org.search.codesearch.index.FileTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class OnDemandFileContentMatcher implements ContentMatcher {
    private static final Logger logger = LoggerFactory.getLogger(OnDemandFileContentMatcher.class);

    @Override
    public boolean match(Path p, String pattern) {
        if (!FileTypes.isTextFile(p))
            return false;

        try {
            Optional<String> match = Files.lines(p)
                    .map(String::toLowerCase)
                    .filter(line -> line.contains(pattern))
                    .findFirst();
            return match.map($ -> true).orElse(false);

        } catch (Exception e) {
            logger.error("Failed to process {}", p, e.getMessage());
            return false;
        }
    }

}
