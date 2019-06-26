package com.network.netty.example.demo02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) {



        EventLoopGroup g = new NioEventLoopGroup();
        // 服务器监听起始端口
        int beginPort = 8000;
        // 服务器监听端口数量
        int nPort = 200;

        try {
            Bootstrap b = new Bootstrap();
            b.group(g)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("连接成功, Channel.id:" + ctx.channel().id());
                                }
                            });
                        }
                    });

            ChannelFuture future;

            for (int i = 0, j = 0; i < 500; i++, j++) {
                future = b.connect("localhost", 8000 + (j % nPort));

                if (j % nPort == 0)
                    future.addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            channelFuture.channel().eventLoop().scheduleAtFixedRate(new SendTimer(channelFuture), 5, 5, TimeUnit.SECONDS);
                        }
                    });
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    static final class SendTimer implements Runnable {
        static ByteBuf byteBuf = Unpooled.buffer(1024);
        ChannelFuture future;

        static {
            for (int i = 0; i < byteBuf.capacity(); i++) {
                byteBuf.writeByte((byte) i);
            }
        }

        public SendTimer(ChannelFuture future) {
            this.future = future;
        }

        public void run() {
            future.channel().writeAndFlush(byteBuf.retain());
        }
    }
}
