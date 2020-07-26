package org.search.codesearch.index.naive;

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
        this.codeSearch = new BruteForceCodeSearch(rootPaths);
    }

}
