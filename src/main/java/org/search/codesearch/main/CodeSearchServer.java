package org.search.codesearch.main;

import org.eclipse.jetty.server.Server;
import org.search.codesearch.handler.codesearch.CodeSearchProcessor;
import org.search.codesearch.main.args.ArgsParser;
import org.search.codesearch.server.HttpRequestHandler;

import java.util.Map;

public class CodeSearchServer {

    public static void main(String[] args) throws Exception {

        Map<String, String> params = ArgsParser.cmdParams(args);
        int port = Integer.parseInt(params.getOrDefault("port", "8080"));
        String source = params.get("source");

        Server server = new Server(port);
        server.setHandler(new HttpRequestHandler() {{
            map("/search", new CodeSearchProcessor(source));
        }});
        server.start();
        server.join();

    }
}
