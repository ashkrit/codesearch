package org.search.codesearch.parser;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class TriGramParserTest {


    @Test
    public void generate_no_grams() {

        NGramGenerator g = new NGramGenerator();
        List<String> result = new ArrayList<>();
        g.generate("this is simple text", 1, (String s) ->
                result.add(s));
        assertArrayEquals(new String[]{"this", "is", "simple", "text"}, result.toArray(new String[]{}));
    }
}
