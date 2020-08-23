package org.search.codesearch.handler.codesearch;

import org.search.codesearch.handler.codesearch.SearchResponse.MatchedRecord;
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

public class CodeSearchProcessor implements RequestProcessor<String, SearchResponse> {

    private final Search search;

    public CodeSearchProcessor(String source) {
        List<String> rootPaths = Arrays.asList(source.split(";"));
        this.search = new CacheFileTreeCodeSearch(rootPaths, InMemoryFileContentMatcher.create(StringContains));
    }

    @Override
    public SearchResponse process(String input, Map<String, String> urlParams) {
        ArrayList<MatchedRecord> result = new ArrayList<>();
        String pattern = urlParams.get("pattern");
        int limit = Integer.parseInt(urlParams.getOrDefault("limit", "100"));

        search.match(new SearchQuery(pattern), f -> {
            result.add(new MatchedRecord(f.toFile().getAbsolutePath()));
        }, limit);

        return new SearchResponse(result);
    }

    @Override
    public Class<String> inputType() {
        return String.class;
    }
}
