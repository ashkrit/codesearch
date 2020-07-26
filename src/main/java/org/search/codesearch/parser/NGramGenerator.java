package org.search.codesearch.parser;

import java.util.function.Consumer;

public class NGramGenerator {

    public void generate(String text, int noOfGrams, Consumer<String> consumer) {
        if (noOfGrams == 1) {
            for (String v : text.split(" ")) {
                consumer.accept(v);
            }
        }
    }
}
