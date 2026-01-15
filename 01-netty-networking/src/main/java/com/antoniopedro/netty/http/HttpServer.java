package com.antoniopedro.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * High-performance HTTP server built with Netty.
 * 
 * Demonstrates:
 * - Non-blocking I/O with Netty
 * - Channel pipeline configuration
 * - HTTP protocol handling
 * - Request routing
 * - Graceful shutdown
 * 
 * @author Antonio Pedro
 */
@Slf4j
public class HttpServer {
    
    private final int port;
    private final Router router;
    
    public HttpServer(int port) {
        this.port = port;
        this.router = new Router();
        configureRoutes();
    }
    
    /**
     * Configure application routes
     */
    private void configureRoutes() {
        // Example routes - to be implemented
        router.get("/", (request) -> 
            "Welcome to Netty HTTP Server - Java Backend Architecture Journey");
        
        router.get("/health", (request) -> 
            "{\"status\":\"UP\",\"service\":\"netty-http-server\"}");
        
        router.get("/api/info", (request) -> 
            "{\"milestone\":\"Networking with Netty\",\"version\":\"1.0.0\"}");
    }
    
    /**
     * Start the HTTP server
     */
    public void start() throws InterruptedException {
        // Boss group handles incoming connections
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // Worker group handles I/O operations
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                // HTTP codec for encoding/decoding HTTP messages
                                .addLast(new HttpServerCodec())
                                // Aggregate HTTP chunks into full HTTP messages
                                .addLast(new HttpObjectAggregator(65536))
                                // Custom handler for request processing
                                .addLast(new HttpServerHandler(router));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            
            log.info("Starting HTTP server on port {}", port);
            
            // Bind and start to accept incoming connections
            ChannelFuture future = bootstrap.bind(port).sync();
            
            log.info("HTTP server started successfully on port {}", port);
            log.info("Available routes:");
            router.getRoutes().forEach(route -> 
                log.info("  {} {}", route.getMethod(), route.getPath()));
            
            // Wait until the server socket is closed
            future.channel().closeFuture().sync();
            
        } finally {
            // Graceful shutdown
            log.info("Shutting down HTTP server...");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        
        try {
            new HttpServer(port).start();
        } catch (InterruptedException e) {
            log.error("Server interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
