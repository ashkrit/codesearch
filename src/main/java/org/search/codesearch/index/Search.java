package org.search.codesearch.index;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Search {
    void match(String pattern, Consumer<Path> consumer, int limit);
}
