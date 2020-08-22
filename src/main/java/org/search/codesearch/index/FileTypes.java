package org.search.codesearch.index;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FileTypes {
    public static final List<String> textExt = Arrays.asList(".java", ".properties", ".c", ".cpp");

    public static boolean isGitFolder(File file) {
        return file.isDirectory() && file.getName().equals(".git");
    }

    public static boolean isCompiledFile(File f) {
        return f.getName().endsWith(".class");
    }

    public static boolean isTextFile(Path p) {
        File f = p.toFile();
        int size = textExt.size();
        for (int index = 0; index < size; index++) {
            if (f.getName().endsWith(textExt.get(index))) {
                return true;
            }
        }
        return false;
    }
}
