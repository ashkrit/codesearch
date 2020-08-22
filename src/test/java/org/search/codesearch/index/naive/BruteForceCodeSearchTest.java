package org.search.codesearch.index.naive;

import org.search.codesearch.index.CodeSearchTest;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class BruteForceCodeSearchTest extends CodeSearchTest {

    @Override
    public void create() throws URISyntaxException {
        Path p = Paths.get(this.getClass().getResource("/").toURI());
        List<String> rootPaths = Arrays.asList(p.toFile().getAbsolutePath());
        super.codeSearch = new BruteForceCodeSearch(rootPaths);
    }

}
