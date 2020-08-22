package org.search.codesearch.matcher;

import org.junit.jupiter.api.Test;
import org.search.codesearch.index.BoyerMoore;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoyerMooreMatcherTest {

    @Test
    public void check_contains() {
        String pattern = "this";
        String content = "nice this is sample";
        assertEquals(content.indexOf(pattern), new BoyerMoore(pattern).search(content));
    }

    @Test
    public void check_contains_when_value_does_not_match() {
        String pattern = "thisismissing";
        String content = "nice this is sample";
        assertEquals(content.indexOf(pattern), new BoyerMoore(pattern).search(content));
    }
}
