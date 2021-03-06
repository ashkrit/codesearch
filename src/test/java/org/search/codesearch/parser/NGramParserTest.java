package org.search.codesearch.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NGramParserTest {

    @Test
    public void generate_no_grams() {
        NGramGenerator g = new NGramGenerator(0, " ");
        List<String> result = new ArrayList<>();

        g.generate("this is simple text", (String s) -> result.add(s));

        assertArrayEquals(new String[]{"this", "is", "simple", "text"}, result.toArray(new String[]{}));
    }


    @Test
    public void generate_3_grams() {
        NGramGenerator g = new NGramGenerator(3, " ");

        List<String> result = new ArrayList<>();
        g.generate("this is simple text", s -> result.add(s));

        assertArrayEquals(new String[]{"thi", "his", "is", "sim", "imp", "mpl", "ple", "tex", "ext"}, result.toArray(new String[]{}));
    }

    @Test
    public void generate_grams_for_3letter_word() {
        NGramGenerator g = new NGramGenerator(3, " ");

        List<String> result = new ArrayList<>();
        g.generate("cat rat mat", s -> result.add(s));

        assertArrayEquals(new String[]{"cat", "rat", "mat"}, result.toArray(new String[]{}));
    }


    @Test
    public void generate_grams_for_multiple_space_text() {
        NGramGenerator g = new NGramGenerator(3, " ");

        List<String> result = new ArrayList<>();
        g.generate("cat   rat  mat", s -> result.add(s));
        System.out.println(result);
        assertArrayEquals(new String[]{"cat", "rat", "mat"}, result.toArray(new String[]{}));
    }

    @Test
    public void failed_when_grams_are_too_small() {
        NGramGenerator g = new NGramGenerator(1, " ");
        assertThrows(IllegalArgumentException.class, () -> g.generate("this is simple text", s -> {
        }));
    }
}
