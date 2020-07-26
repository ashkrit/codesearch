package org.search.codesearch.parser;

import java.util.function.Consumer;

public class NGramGenerator {

    private final int noOfGrams;

    public NGramGenerator(int noOfGrams) {
        this.noOfGrams = noOfGrams;
    }

    public void generate(String text, Consumer<String> consumer) {
        if (noOfGrams == 0) {
            for (String v : extractParts(text)) {
                consumer.accept(v);
            }
        }

        if (noOfGrams < 3)
            throw new IllegalArgumentException("Too small");


        if (noOfGrams == 3) {
            String[] words = extractParts(text);
            for (String word : words) {
                if (nGramRequired(noOfGrams, word)) {
                    for (int index = 0; index + noOfGrams <= word.length(); index++) {
                        consumer.accept(word.substring(index, index + noOfGrams));
                    }
                } else {
                    consumer.accept(word);
                }
            }
        }
    }

    private String[] extractParts(String text) {
        return text.split(" ");
    }

    private boolean nGramRequired(int noOfGrams, String word) {
        return word.length() >= noOfGrams;
    }
}
