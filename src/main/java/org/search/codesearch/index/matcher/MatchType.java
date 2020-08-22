package org.search.codesearch.index.matcher;

public enum MatchType {
    StringContains {
        public ContentMatcher create() {
            return new InMemoryFileContentMatcher(InMemoryFileContentMatcher::bruteMatch);
        }
    },
    BoyerMoor {
        public ContentMatcher create() {
            return new InMemoryFileContentMatcher(InMemoryFileContentMatcher::boyerMatch);
        }
    };

    public ContentMatcher create() {
        throw new IllegalArgumentException("Not supported");
    }

}
