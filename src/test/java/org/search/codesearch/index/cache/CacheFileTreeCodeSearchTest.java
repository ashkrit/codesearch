package org.search.codesearch.index.cache;

import org.search.codesearch.index.CodeSearchTest;
import org.search.codesearch.matcher.InMemoryFileContentMatcher;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.search.codesearch.matcher.MatchType.StringContains;

public class CacheFileTreeCodeSearchTest extends CodeSearchTest {

    @Override
    public void create() throws URISyntaxException {
        Path p = Paths.get(this.getClass().getResource("/").toURI());
        List<String> rootPaths = Arrays.asList(p.toFile().getAbsolutePath());
        super.codeSearch = new CacheFileTreeCodeSearch(rootPaths, InMemoryFileContentMatcher.create(StringContains));
    }

}
