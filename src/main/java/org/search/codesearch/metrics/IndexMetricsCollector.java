package org.search.codesearch.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;

public class IndexMetricsCollector {

    private static final Logger logger = LoggerFactory.getLogger(IndexMetricsCollector.class);
    private static final SourceFileMetrics genericFileMetrics = new GenericFileMetrics();

    public static void summarizingFile(IndexMetrics indexMetrics, File file) {
        long current = indexMetrics.noOfFiles.incrementAndGet();
        if (current % 10_000 == 0) {
            logger.info("Reading file {}", current);
        }
        genericFileMetrics.collect(indexMetrics, file, Optional.empty());
    }


}
