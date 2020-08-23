package org.search.codesearch.server;

import com.google.gson.Gson;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RequestPipeLineBuilder {

    public Function<RequestContext, RequestContext> pipeline() {
        return pipeline;
    }

    private Function<RequestContext, RequestContext> markComplete = (context) -> {
        context.baseRequest.setHandled(true);
        return context;
    };

    private Function<RequestContext, RequestContext> process = (context) -> {
        Gson jsonParser = new Gson();

        Reader textStream = toReader(context.request);

        Object parsedValue = jsonParser.fromJson(textStream, context.processor.inputType());

        context.output = context.processor.process(parsedValue, context.urlParameters());

        return context;
    };

    private Reader toReader(HttpServletRequest request) {
        try {
            return new InputStreamReader(request.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private PrintWriter toWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private Function<RequestContext, RequestContext> prepareForWrite = (context) -> {
        context.response.setContentType("application/json; charset=utf-8");
        context.response.setStatus(HttpServletResponse.SC_OK);
        return context;
    };

    private Function<RequestContext, RequestContext> writeResponse = (context) -> {
        PrintWriter out = toWriter(context.response);
        out.println(new Gson().toJson(context.output));
        return context;
    };

    private final Function<RequestContext, RequestContext> pipeline = process
            .andThen(prepareForWrite).andThen(writeResponse).andThen(markComplete);

    public static class RequestContext {
        final Request baseRequest;
        final HttpServletRequest request;
        final HttpServletResponse response;
        final RequestProcessor processor;
        Object output;

        public RequestContext(HttpServletRequest request, HttpServletResponse response, RequestProcessor processor,
                              Request baseRequest) {
            this.request = request;
            this.response = response;
            this.processor = processor;
            this.baseRequest = baseRequest;
        }

        public Map<String, String> urlParameters() {
            return baseRequest.getParameterMap().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));
        }

    }
}
