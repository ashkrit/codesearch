package org.search.codesearch.server;

import java.util.Map;

public interface RequestProcessor<I, O> {
    O process(I input, Map<String, String> urlParams);

    Class<I> inputType();
}
