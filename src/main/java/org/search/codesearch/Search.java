package org.search.codesearch;

import java.nio.file.Path;
import java.util.function.Consumer;

public interface Search {
    void match(String pattern, Consumer<Path> consumer);
}
