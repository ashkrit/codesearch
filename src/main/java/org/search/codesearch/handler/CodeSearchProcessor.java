package org.search.codesearch.handler;

import org.search.codesearch.index.Search;
import org.search.codesearch.index.SearchQuery;
import org.search.codesearch.index.cache.CacheFileTreeCodeSearch;
import org.search.codesearch.matcher.InMemoryFileContentMatcher;
import org.search.codesearch.server.RequestProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.search.codesearch.matcher.MatchType.StringContains;

public class CodeSearchProcessor implements RequestProcessor<String, CodeSearchProcessor.SearchReply> {

    private final Search search;

    public CodeSearchProcessor(String source) {
        List<String> rootPaths = Arrays.asList(source.split(";"));
        this.search = new CacheFileTreeCodeSearch(rootPaths, InMemoryFileContentMatcher.create(StringContains));
    }

    @Override
    public SearchReply process(String input, Map<String, String> urlParams) {
        ArrayList<SearchResult> result = new ArrayList<>();
        String pattern = urlParams.get("pattern");
        int limit = Integer.parseInt(urlParams.getOrDefault("limit", "100"));

        search.match(new SearchQuery(pattern), f -> {
            result.add(new SearchResult(f.toFile().getAbsolutePath()));
        }, limit);

        return new SearchReply(result);
    }

    @Override
    public Class<String> inputType() {
        return String.class;
    }

    public static class SearchReply {
        private final List<SearchResult> result;

        public SearchReply(List<SearchResult> result) {
            this.result = result;
        }

        public List<SearchResult> getResult() {
            return result;
        }
    }

    public static class SearchResult {
        private final String file;

        public SearchResult(String file) {
            this.file = file;
        }

        public String getFile() {
            return file;
        }
    }
}
