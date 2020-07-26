package org.search.codesearch.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TriGramParserTest {

    @Test
    public void generate_no_grams() {

        NGramGenerator g = new NGramGenerator();
        String[] values = g.generate("this is simple text", 1);
        assertArrayEquals(new String[]{"this", "is", "simple", "text"}, values);
    }
}
