package com.antdev99.learning.netty;

import com.antdev99.learning.http.encoder.MiniHttpEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * An HTTP server that listens for incoming connections and processes HTTP requests using Netty.
 */
public class MiniHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(MiniHttpServer.class);

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public final int port;

    public MiniHttpServer(int port) {
        this.port = port;
    }

    /**
     * Starts the HTTP server
     * @throws IOException if any error
     */
    public void start() throws IOException {
        final ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childOption(ChannelOption.SO_KEEPALIVE, false)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                           logger.debug("Initializing channel for {}", sc.remoteAddress());
                           sc.pipeline().addLast(new MiniHttpEncoder());
                           sc.pipeline().addLast(new MiniHttpHandler());
                        }
                    });
            logger.info("Server listening on port {}", port);
            ChannelFuture f = bootstrap.bind().sync();
            logger.info("Server started successfully on port {}", port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("Error starting server", e);
        }
    }

    /**
     * Shutdowns the HTTP server
     */
    public void shutdown() {
        try {
            logger.info("Shutting down server...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            logger.info("Server shutdown complete");
        } catch (Exception e) {
            logger.error("Error during server shutdown", e);
        }
    }

}
