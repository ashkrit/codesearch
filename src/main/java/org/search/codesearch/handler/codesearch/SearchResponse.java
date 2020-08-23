package org.search.codesearch.handler.codesearch;

import java.util.List;

public class SearchResponse {
    private final List<MatchedRecord> result;

    public SearchResponse(List<MatchedRecord> result) {
        this.result = result;
    }

    public List<MatchedRecord> getResult() {
        return result;
    }

    public static class MatchedRecord {
        private final String file;

        public MatchedRecord(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }
}