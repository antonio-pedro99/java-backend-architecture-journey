package com.antdev99.learning.netty;

import com.antdev99.learning.http.MiniHttpHeader;
import com.antdev99.learning.http.MiniHttpRequest;
import com.antdev99.learning.http.MiniHttpResponse;
import com.antdev99.learning.http.serializer.MiniHttpResponseSerializer;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A Netty channel handler that processes incoming MiniHttpRequest messages and generates appropriate MiniHttpResponse messages.
 * This handler reads the HTTP request, constructs a response, and sends it back to the client.
 */
public class MiniHttpHandler extends SimpleChannelInboundHandler<MiniHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(MiniHttpHandler.class);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MiniHttpRequest request;
    private final MiniHttpResponseSerializer responseSerializer = new MiniHttpResponseSerializer();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiniHttpRequest httpRequest) throws Exception {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        this.request = httpRequest;

        logger.debug("Received HTTP request from {}: {}",
                ctx.channel().remoteAddress(),
                httpRequest);

        // INFO level: Basic request information (time, method, path)
        logger.info("{} - {} {} from {}", timestamp, request.method(), request.path(), ctx.channel().remoteAddress());

        ReferenceCountUtil.release(httpRequest);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        handleRequestContext(ctx);
    }

    /**
     * Handles the context of the incoming HTTP request, constructs a response, and sends it back to the client.
     *
     * @param ctx the ChannelHandlerContext associated with the current channel
     */
    private void handleRequestContext(ChannelHandlerContext ctx) {

        final MiniHttpResponse.Builder responseBuilder = new MiniHttpResponse.Builder()
                .withVersion(request.version())
                .withStatusCode(MiniHttpResponse.StatusCode.OK)
                .withHeader(new MiniHttpHeader(MiniHttpHeader.CONTENT_LENGTH, "Hello, World!".length()))
                .withHeader(new MiniHttpHeader(MiniHttpHeader.CONTENT_TYPE, "text/plain"))
                .withBody("Hello, World!");

        String response = responseSerializer.serialize(responseBuilder.build());
        ChannelFuture w = ctx.writeAndFlush(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
        if (w.isSuccess()) {
            logger.info("Response sent successfully to {}",  ctx.channel().remoteAddress());
        } else {
            logger.error("Failed to send response to {}", ctx.channel().remoteAddress(), w.cause());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception caught while handling request from {}", ctx.channel().remoteAddress(), cause);
    }
}
