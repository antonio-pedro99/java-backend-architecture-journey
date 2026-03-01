package com.antdev99.learning.http.encoder;

import com.antdev99.learning.http.MiniHttpRequest;
import com.antdev99.learning.http.parser.MiniHttpParser;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler that processes incoming HTTP requests.
 */
public class MiniHttpEncoder extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger logger = LoggerFactory.getLogger(MiniHttpEncoder.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        logger.debug("Decoding incoming HTTP request from {}", channelHandlerContext.channel().remoteAddress());
        MiniHttpRequest request = MiniHttpParser.parseRequest(byteBuf);
        logger.debug("Successfully decoded HTTP request");

        // write to the next handler in the pipeline
        channelHandlerContext.fireChannelRead(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception caught while encoding HTTP request from {}", ctx.channel().remoteAddress(), cause);
    }
}
