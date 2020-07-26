package org.search.codesearch.parser;

import java.util.function.Consumer;

public class NGramGenerator {

    public void generate(String text, int noOfGrams, Consumer<String> consumer) {
        if (noOfGrams == 0) {
            for (String v : text.split(" ")) {
                consumer.accept(v);
            }
        }

        if (noOfGrams == 3) {
            String[] words = text.split(" ");
            for (String word : words) {
                if (word.length() >= noOfGrams) {
                    for (int index = 0; index + 3 <= word.length(); index++) {
                        consumer.accept(word.substring(index, index + 3));
                    }
                } else {
                    consumer.accept(word);
                }
            }
        }

    }
}
