package org.search.codesearch.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class IndexMetricsCollector {

    private static final Logger logger = LoggerFactory.getLogger(IndexMetricsCollector.class);
    private static final SourceFileMetrics javaMetrics = new JavaFileMetrics();

    public static void summarizingFile(IndexMetrics indexMetrics, File file) {
        long current = indexMetrics.noOfFiles.incrementAndGet();
        if (current % 10_000 == 0) {
            logger.info("Reading file {}", current);
        }

        javaMetrics.collect(indexMetrics, file);
    }


}
