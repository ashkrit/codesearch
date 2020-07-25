package org.search.codesearch.index;

import java.io.File;

public class FileTypes {
    public static boolean isGitFolder(File file) {
        return file.isDirectory() && file.getName().equals(".git");
    }
}
