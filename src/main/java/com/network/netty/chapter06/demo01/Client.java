package com.network.netty.chapter06.demo01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;


public class Client {
    public static void main(String[] args) {
        Bootstrap boot = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            boot.group(group).channel(NioSocketChannel.class).remoteAddress("localhost", 1127)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
//                                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello\r\n", CharsetUtil.UTF_8));

                                    ctx.channel().eventLoop().scheduleAtFixedRate(() ->{
                                        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello\r\n", CharsetUtil.UTF_8));
                                    }, 5, 5, TimeUnit.SECONDS);
                                }
                            });
                        }
                    });
            ChannelFuture future = boot.connect().sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    System.out.println("Client：send data complete！");
                }
            });
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
