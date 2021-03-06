package org.search.codesearch.parser;

import java.util.function.Consumer;

public class NGramGenerator {

    private final int noOfGrams;
    private final String token;

    public NGramGenerator(int noOfGrams, String token) {
        this.noOfGrams = noOfGrams;
        this.token = token;
    }

    public void generate(String text, Consumer<String> consumer) {
        if (noOfGrams == 0) {
            for (String v : extractParts(text)) {
                consumer.accept(v);
            }
            return;
        }

        if (noOfGrams < 3)
            throw new IllegalArgumentException("Too small");

        String[] words = extractParts(text);
        for (String word : words) {
            if (word.trim().isEmpty()) {
                continue;
            }
            if (isNGramRequired(noOfGrams, word)) {
                generateNGrams(consumer, word);
            } else {
                consumer.accept(word);
            }
        }

    }

    private void generateNGrams(Consumer<String> consumer, String word) {
        int length = word.length() - noOfGrams;
        for (int index = 0; index <= length; index++) {
            consumer.accept(word.substring(index, index + noOfGrams));
        }
    }

    private String[] extractParts(String text) {
        return text.split(token);
    }

    private boolean isNGramRequired(int noOfGrams, String word) {
        return word.trim().length() >= noOfGrams;
    }
}
