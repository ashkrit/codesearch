package org.search.codesearch.index;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class CodeSearchTest {

    public Search codeSearch;

    @BeforeEach
    public void setup() throws URISyntaxException {
        create();
    }

    @Test
    public void no_result_match_for_random_string() {

        List<Path> result = new ArrayList<>();
        String text = "gsak jksd ";
        codeSearch.match(new SearchQuery(text), x -> result.add(x), 10);
        assertEquals(0, result.size());
    }


    @Test
    public void result_based_on_file_name() {

        List<Path> result = new ArrayList<>();
        String text = "tinylog";
        codeSearch.match(new SearchQuery(text), x -> result.add(x), 10);
        assertEquals("tinylog.properties", result.get(0).toFile().getName());
    }

    @Test
    public void result_based_on_file_content() {
        List<Path> result = new ArrayList<>();
        String text = "stacktrace";
        codeSearch.match(new SearchQuery(text), x -> result.add(x), 10);
        assertEquals("tinylog.properties", result.get(0).toFile().getName());
    }

    public abstract void create() throws URISyntaxException;
}
