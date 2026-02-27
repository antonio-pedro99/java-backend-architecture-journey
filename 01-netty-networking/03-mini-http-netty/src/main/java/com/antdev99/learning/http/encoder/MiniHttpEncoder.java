package com.antdev99.learning.http.encoder;

import com.antdev99.learning.http.MiniHttpRequest;
import com.antdev99.learning.http.parser.MiniHttpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Handler that processes incoming HTTP requests.
 */
public class MiniHttpEncoder extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        MiniHttpRequest request = MiniHttpParser.parseRequest(byteBuf);

        // write to the next handler in the pipeline
        channelHandlerContext.fireChannelRead(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
