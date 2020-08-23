package org.search.codesearch.matcher;

import org.junit.jupiter.api.Test;
import org.search.codesearch.string.BoyerMooreMatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoyerMooreMatcherMatcherTest {

    @Test
    public void check_contains() {
        String pattern = "this";
        String content = "nice this is sample";
        assertEquals(content.indexOf(pattern), new BoyerMooreMatcher(pattern).search(content));
    }

    @Test
    public void check_contains_when_value_does_not_match() {
        String pattern = "thisismissing";
        String content = "nice this is sample";
        assertEquals(content.indexOf(pattern), new BoyerMooreMatcher(pattern).search(content));
    }
}
