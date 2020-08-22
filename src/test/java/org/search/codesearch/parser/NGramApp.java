package org.search.codesearch.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class NGramApp {

    public static void main(String[] args) throws IOException {
        NGramGenerator g = new NGramGenerator(3, " ");
        byte[] b = Files.readAllBytes(Paths.get("C:\\_work\\coding\\github\\jdk\\src\\java.sql\\share\\classes\\java\\sql\\DriverManager.java"));
        System.out.println(b.length);
        StringBuilder sb = new StringBuilder();
        StringBuilder figurePrint = new StringBuilder();
        NavigableSet<String> values = new TreeSet<>();
        for (byte bb : b) {
            if (bb == '\n' || bb == '\r') {

                String value = sb.toString().trim().toLowerCase();
                if (!(value.startsWith("/*") || value.startsWith("*") || value.isEmpty() || value.startsWith("//"))) {
                    //System.out.println(sb);
                    if (isClass(value)) {
                        value = sanitized(value);
                        System.out.println("Class->" + value);
                        figurePrint.append(value);
                        g.generate(value, s -> values.add(s));
                    } else if (isAccessModifier(value) && hasMethodStartAndEnd(value) && !value.endsWith(";")) {
                        value = sanitized(value);
                        System.out.println("Method->" + value);
                        figurePrint.append(value);
                        g.generate(value, s -> values.add(s));
                    } else if (isAccessModifier(value) & value.endsWith(";")) {
                        value = sanitized(value);
                        System.out.println("Variable->" + value);
                        figurePrint.append(value);
                        g.generate(value, s -> values.add(s));
                    }
                }

                sb.setLength(0);
            } else {
                sb.append((char) bb);
            }
        }

        System.out.println(figurePrint);
        System.out.println(figurePrint.toString().getBytes().length);
        System.out.println(values);

        Scanner s = new Scanner(System.in);
        while (s.hasNext()) {
            String text = s.nextLine();

            g.generate(text, q -> {
                        System.out.println(values.contains(q) + " ->" + q);
                    }
            );

        }

    }

    private static boolean hasMethodStartAndEnd(String value) {
        return value.contains("(") && value.contains(")");
    }

    private static boolean isAccessModifier(String value) {
        return value.startsWith("public") || value.startsWith("private") || value.startsWith("protected");
    }

    private static boolean isClass(String value) {
        return value.contains("class ");
    }

    private static Set<String> keyWords = new HashSet<>(Arrays.asList("()", "private ", "public ", "protected ", "static ", "volatile ", "void ", "extends ",
            "implements ", "class ", "final ", "int ", "float ", "long ", "double ", "byte ", "char ", "boolean ", ";", "{", "}"));

    public static String sanitized(String value) {
        for (String key : keyWords) {
            value = value.replace(key, "");
        }
        return value;
    }

}
