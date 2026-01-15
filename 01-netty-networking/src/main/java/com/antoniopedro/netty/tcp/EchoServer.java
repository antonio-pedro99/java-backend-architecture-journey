package com.antoniopedro.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple TCP Echo Server using Netty.
 * Demonstrates basic TCP server setup and channel handling.
 * 
 * @author Antonio Pedro
 */
@Slf4j
public class EchoServer {
    
    private final int port;
    
    public EchoServer(int port) {
        this.port = port;
    }
    
    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            log.info("Starting TCP Echo Server on port {}", port);
            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("TCP Echo Server started successfully");
            
            future.channel().closeFuture().sync();
        } finally {
            log.info("Shutting down TCP Echo Server...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9090;
        
        try {
            new EchoServer(port).start();
        } catch (InterruptedException e) {
            log.error("Server interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
