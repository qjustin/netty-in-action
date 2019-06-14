package com.network.netty.chapter06.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class Server {


    public static void main(String[] args) {

        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", CharsetUtil.UTF_8));
        // ServerBootstrip 有两个EventLoopGroup，一个负责Accept进入的连接，
        // 交给另一个
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // EventLoopGroup(线程组), 包含多个额EventLoop(事件循环线程)，
        // EventLoop与Channel绑定，成为Channel的执行线程
        // 并且整个声明周期内不会改变
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        serverBootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(161127)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                            }
                        });
                    }
                });


    }
}
