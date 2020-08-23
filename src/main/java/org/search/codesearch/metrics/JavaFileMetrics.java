package org.search.codesearch.metrics;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class JavaFileMetrics implements SourceFileMetrics {

    @Override
    public void collect(IndexMetrics metrics, File file, Optional<List<String>> mayBeLines) {
        collectMetrics(metrics, file, mayBeLines);
    }

    private static void collectMetrics(IndexMetrics indexMetrics, File x, Optional<List<String>> mayBeLines) {
        if (isJava(x)) {
            List<String> lines = mayBeLines.get();
            long methodCount = lines.stream().filter(JavaFileMetrics::isMethod).count();
            long variableCount = lines.stream().filter(JavaFileMetrics::isInstanceVariable).count();
            indexMetrics.recordFunction(methodCount);
            indexMetrics.recordVariable(variableCount);
        }
    }

    private static boolean isJava(File x) {
        return x.getName().endsWith("java");
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
