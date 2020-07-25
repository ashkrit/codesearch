package org.search.codesearch.index.matcher;

import java.nio.file.Path;

public interface ContentMatcher {
    boolean match(Path p, String pattern);
}
