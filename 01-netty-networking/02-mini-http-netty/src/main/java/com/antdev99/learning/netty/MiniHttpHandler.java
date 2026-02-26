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

public class MiniHttpHandler extends SimpleChannelInboundHandler<MiniHttpRequest> {
    private MiniHttpRequest request;
    private final MiniHttpResponseSerializer responseSerializer = new MiniHttpResponseSerializer();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiniHttpRequest httpRequest) throws Exception {
        this.request = httpRequest;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        handleRequestContext(ctx);
    }

    private void handleRequestContext(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().remoteAddress() + ": " + request.method() + " " + request.path());

        final MiniHttpResponse.Builder responseBuilder = new MiniHttpResponse.Builder().
                withVersion(request.version())
                .withStatusCode(MiniHttpResponse.StatusCode.OK)
                .withHeaders(request.headers().entrySet().stream()
                        .map(entry -> new MiniHttpHeader(entry.getKey(), entry.getValue().toString()))
                        .toList())
                .withBody("Hello, World!");

        String response = responseSerializer.serialize(responseBuilder.build());
        ChannelFuture w = ctx.writeAndFlush(Unpooled.copiedBuffer(response, CharsetUtil.UTF_8));
        if (w.isSuccess()) {
            System.out.println("Response sent successfully to " + ctx.channel().remoteAddress());
        } else {
            System.err.println("Failed to send response to " + ctx.channel().remoteAddress());
            w.cause().printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
