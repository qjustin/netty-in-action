package com.network.netty.chapter00.test03.v04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.atomic.AtomicInteger;

import static com.network.netty.chapter00.test03.v03.Constants.BEGIN_PORT;
import static com.network.netty.chapter00.test03.v03.Constants.N_PORT;

public class Server {
    private static final AtomicInteger connCounts = new AtomicInteger();
    private static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final ServerInboundHandler serverInboundHandler = new ServerInboundHandler(connCounts, channels);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup);
            boot.channel(NioServerSocketChannel.class);
            boot.option(ChannelOption.SO_BACKLOG, 128);
            boot.childOption(ChannelOption.TCP_NODELAY,true);
            boot.childOption(ChannelOption.SO_REUSEADDR, true);
            boot.childOption(ChannelOption.SO_KEEPALIVE, true);
            boot.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline()
                            .addLast(new LineBasedFrameDecoder(1024))
                            .addLast("decoder", new StringDecoder())
                            .addLast("encoder", new StringEncoder())
                            .addLast("serverInbound", serverInboundHandler);
                }
            });

            for (int i = 0; i < N_PORT; i++) {
                int port = BEGIN_PORT + i;

                boot.bind(port).addListener(
                        new GenericFutureListener<Future<? super Void>>() {
                            @Override
                            public void operationComplete(Future<? super Void> future) throws Exception {
                                if (future.isSuccess()) {
                                    System.out.println("Server bind to port " + port);
                                }
                            }
                        }
                );
            }
            // future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
//                bossGroup.shutdownGracefully().sync();
//                workerGroup.shutdownGracefully().sync();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
