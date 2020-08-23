package org.search.codesearch.metrics;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class JavaFileMetrics implements SourceFileMetrics {

    @Override
    public void collect(IndexMetrics metrics, File file) {
        collectMetrics(metrics, file);
    }

    private static void collectMetrics(IndexMetrics indexMetrics, File x) {
        try {
            List<String> lines = fileContent(x);
            if (isJava(x)) {
                long methodCount = lines.stream().filter(JavaFileMetrics::isMethod).count();
                long variableCount = lines.stream().filter(JavaFileMetrics::isInstanceVariable).count();
                indexMetrics.noOfFunction.addAndGet(methodCount);
                indexMetrics.noOfVariable.addAndGet(variableCount);
            }
            indexMetrics.noOfLines.addAndGet(lines.size());
        } catch (IOException e) {
            //
        }
    }

    private static boolean isJava(File x) {
        return x.getName().endsWith("java");
    }

    private static List<String> fileContent(File file) throws IOException {
        return Files.readAllLines(file.toPath()).stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private static boolean isMethod(String line) {
        if ((hasAccessModifier(line)) && line.contains("{")) {
            int startBracket = line.indexOf("(");
            if (startBracket > -1) {
                int endBracket = line.indexOf(")", startBracket);
                if (endBracket > -1) {
                    return line.indexOf(";", endBracket) == -1 ? true : false;
                }
            }
        }
        return false;
    }

    private static boolean isInstanceVariable(String line) {
        if (hasAccessModifier(line) && line.contains(";")) {
            return true;
        }
        return false;
    }

    private static boolean hasAccessModifier(String line) {
        return line.contains("public") || line.contains("private") || line.contains("protected");
    }
}
