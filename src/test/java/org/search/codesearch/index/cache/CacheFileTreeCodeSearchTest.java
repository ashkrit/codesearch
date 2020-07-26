package org.search.codesearch.index.cache;

import org.search.codesearch.index.CodeSearchTest;
import org.search.codesearch.index.matcher.InMemoryFileContentMatcher;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class CacheFileTreeCodeSearchTest extends CodeSearchTest {

    @Override
    public void create() throws URISyntaxException {
        Path p = Paths.get(this.getClass().getResource("/").toURI());
        List<String> rootPaths = Arrays.asList(p.toFile().getAbsolutePath());
        super.codeSearch = new CacheFileTreeCodeSearch(rootPaths, InMemoryFileContentMatcher.create());
    }

}
