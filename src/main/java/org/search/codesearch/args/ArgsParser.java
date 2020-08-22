package org.search.codesearch.args;

import java.util.HashMap;
import java.util.Map;

public class ArgsParser {
    public static Map<String, String> cmdParams(String[] args) {
        Map<String, String> params = new HashMap<String, String>() {
            @Override
            public String get(Object k) {
                String value = super.get(k);
                if (value == null) {
                    throw new IllegalArgumentException("Param " + k + " is missing");
                }
                return value;
            }
        };
        for (int index = 0; index < args.length; index += 2) {
            if (!args[index].startsWith("-")) {
                throw new IllegalArgumentException("Parameter must start from -");
            }
            params.put(args[index].substring(1), args[index + 1]);
        }
        return params;
    }
}
