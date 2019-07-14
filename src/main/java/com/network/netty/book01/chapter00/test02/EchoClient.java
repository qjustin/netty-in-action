package com.network.netty.book01.chapter00.test02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class EchoClient {

    public static void main(String[] args) {
        EventLoopGroup g = new NioEventLoopGroup();
        ByteBuf byteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello matrix\r\n", CharsetUtil.UTF_8));

        try {
            Bootstrap b = new Bootstrap();
            b.group(g).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(byteBuf);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(((ByteBuf)msg).toString(CharsetUtil.UTF_8));
                                    ctx.write(msg);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    ctx.close();
                                }
                            });
                        }
                    });
            ChannelFuture future = b.connect("localhost", 1127).sync();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                g.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
