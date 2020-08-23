package org.search.codesearch.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GenericFileMetrics implements SourceFileMetrics {

    private final List<SourceFileMetrics> sourceFileTypesCollector = Arrays.asList(new JavaFileMetrics());

    @Override
    public void collect(IndexMetrics metrics, File file, Optional<List<String>> lines) {
        collectMetrics(metrics, file);
    }

    private void collectMetrics(IndexMetrics indexMetrics, File x) {
        try {
            List<String> lines = fileContent(x);
            indexMetrics.recordLines(lines.size());
            sourceFileTypesCollector.forEach(sourceFile -> sourceFile.collect(indexMetrics, x, Optional.of(lines)));
        } catch (IOException e) {
            //
        }
    }

    private static List<String> fileContent(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
