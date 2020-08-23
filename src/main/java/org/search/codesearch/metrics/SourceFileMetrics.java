package org.search.codesearch.metrics;

import java.io.File;

public interface SourceFileMetrics {
    void collect(IndexMetrics metrics, File file);
}
