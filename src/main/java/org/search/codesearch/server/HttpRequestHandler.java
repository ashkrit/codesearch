package org.search.codesearch.server;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.search.codesearch.server.RequestPipeLineBuilder.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler extends AbstractHandler {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final Map<String, RequestProcessor> contextToRequest = new HashMap<>();
    private final RequestPipeLineBuilder pipelineBuilder = new RequestPipeLineBuilder();

    public void map(String context, RequestProcessor processor) {
        contextToRequest.put(context, processor);
    }

    @Override
    public void handle(String contextPath, Request jettyRequest, HttpServletRequest request, HttpServletResponse response) {

        logger.info("Processing {}?{}", contextPath, request.getQueryString());
        RequestProcessor processor = contextToRequest.get(contextPath);
        RequestContext context = new RequestContext(request, response, processor, jettyRequest);
        pipelineBuilder.pipeline().apply(context);

    }
}
