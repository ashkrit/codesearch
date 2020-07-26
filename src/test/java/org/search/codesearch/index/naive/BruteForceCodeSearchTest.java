package org.search.codesearch.index.naive;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.search.codesearch.index.Search;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BruteForceCodeSearchTest {

    private Search codeSearch;

    @BeforeEach
    public void pathToSearch() throws URISyntaxException {
        Path p = Paths.get(this.getClass().getResource("/").toURI());
        List<String> rootPaths = Arrays.asList(p.toFile().getAbsolutePath());
        this.codeSearch = new BruteForceCodeSearch(rootPaths);
    }

    @Test
    public void no_result_match_for_random_string() {

        List<Path> result = new ArrayList<>();
        codeSearch.match("gsak jksd ", x -> result.add(x), 10);
        assertEquals(0, result.size());
    }


    @Test
    public void result_based_on_file_name() {

        List<Path> result = new ArrayList<>();
        codeSearch.match("tinylog", x -> result.add(x), 10);
        assertEquals("tinylog.properties", result.get(0).toFile().getName());
    }

    @Test
    public void result_based_on_file_content() {
        List<Path> result = new ArrayList<>();
        codeSearch.match("stacktrace", x -> result.add(x), 10);
        assertEquals("tinylog.properties", result.get(0).toFile().getName());
    }

}
