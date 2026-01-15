package com.antoniopedro.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * Handler for Echo Server - echoes back all received data.
 * 
 * @author Antonio Pedro
 */
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buffer = (ByteBuf) msg;
        try {
            byte[] bytes = new byte[buffer.readableBytes()];
            buffer.readBytes(bytes);
            String message = new String(bytes);
            
            log.debug("Received: {}", message.trim());
            
            // Echo back the received data
            // Create a new buffer with the same content for writing
            ByteBuf response = ctx.alloc().buffer(bytes.length);
            response.writeBytes(bytes);
            ctx.write(response);
        } finally {
            // Always release the original buffer
            buffer.release();
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception in echo handler", cause);
        ctx.close();
    }
}
