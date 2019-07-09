package com.network.netty.chapter00.test06.v02;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline()
                            .addLast(new CustomerDecoder())
                            .addLast(new CustomerEncoder())
                            .addLast(new SimpleChannelInboundHandler<String>() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.channel().eventLoop().execute(() -> {
                                        int counter = 0;
                                        for(;;) {
                                            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                                            try {
                                                ctx.write(reader.readLine());
                                                counter++;
                                            } catch (IOException ex) {
                                                ex.printStackTrace();
                                            }
                                            if (counter % 3 == 0) {
                                                ctx.flush();
                                            }
                                        }
                                    });
                                }

                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
                                    System.out.println("client received message:" + s);
                                }
                            });
                }
            });

            ChannelFuture future = boot.connect("localhost", 1127).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("connected to localhost:1127");
                }
            });
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
