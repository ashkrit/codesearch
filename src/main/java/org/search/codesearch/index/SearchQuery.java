package org.search.codesearch.index;

public class SearchQuery {
    public final String[] patterns;
    public final String rawQuery;

    public SearchQuery(String searchQuery) {
        this.patterns = searchQuery.split(";");
        this.rawQuery = searchQuery;
    }

    @Override
    public String toString() {
        return rawQuery;
    }

}
