package com.network.netty.book01.chapter04.demo02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

/*
 *   客户端读取完消息后回复给服务端，服务端读取消息再回复给客户端，如此循环。
 */
public class Client {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group).channel(NioSocketChannel.class).remoteAddress("localhost", 1127)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("client say hello: " + System.nanoTime(), CharsetUtil.UTF_8));
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(((ByteBuf) msg).toString(CharsetUtil.UTF_8));
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("client reply hello: " + System.nanoTime(), CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect().sync();
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
