package org.search.codesearch.parser;

public class NGramGenerator {
    public String[] generate(String text, int noOfGrams) {
        if (noOfGrams == 1) {
            return text.split(" ");
        }
        return new String[]{};
    }
}
