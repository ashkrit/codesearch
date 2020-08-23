package org.search.codesearch.metrics;

import java.io.File;
import java.util.List;
import java.util.Optional;

public interface SourceFileMetrics {
    void collect(IndexMetrics metrics, File file, Optional<List<String>> lines);
}
