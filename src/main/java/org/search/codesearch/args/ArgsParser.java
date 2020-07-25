package org.search.codesearch.args;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {
    public static Map<String, String> cmdParams(String[] args) {
        Map<String, String> params = new HashMap<>();
        for (int index = 0; index < args.length; index += 2) {
            params.put(args[index].substring(1), args[index + 1]);
        }
        return params;
    }
}
