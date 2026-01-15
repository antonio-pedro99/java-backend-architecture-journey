package com.antoniopedro.netty.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles HTTP requests by routing them to appropriate handlers.
 * 
 * @author Antonio Pedro
 */
@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    
    private final Router router;
    
    public HttpServerHandler(Router router) {
        this.router = router;
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        HttpMethod method = request.method();
        
        log.debug("Received {} request for {}", method, uri);
        
        // Extract path (without query parameters) using proper URI parsing
        String path = uri;
        int queryIndex = uri.indexOf('?');
        if (queryIndex != -1) {
            path = uri.substring(0, queryIndex);
        }
        
        try {
            // Find and execute route handler
            String response = router.handle(method.name(), path, request);
            sendResponse(ctx, request, HttpResponseStatus.OK, response);
        } catch (RouteNotFoundException e) {
            log.warn("Route not found: {} {}", method, path);
            sendResponse(ctx, request, HttpResponseStatus.NOT_FOUND, 
                "{\"error\":\"Not Found\",\"path\":\"" + path + "\"}");
        } catch (Exception e) {
            log.error("Error processing request", e);
            sendResponse(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR, 
                "{\"error\":\"Internal Server Error\"}");
        }
    }
    
    private void sendResponse(ChannelHandlerContext ctx, FullHttpRequest request,
                             HttpResponseStatus status, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
        );
        
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        
        // Handle keep-alive
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response);
        } else {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in HTTP handler", cause);
        ctx.close();
    }
}
