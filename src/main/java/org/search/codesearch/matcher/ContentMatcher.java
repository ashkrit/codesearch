package org.search.codesearch.matcher;

import java.nio.file.Path;

public interface ContentMatcher {
    boolean match(Path p, String pattern);
}
