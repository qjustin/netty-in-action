package com.network.netty.example.demo02;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ChildChannelHandlerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup channelGroup;

    private AtomicInteger connCounts = new AtomicInteger();

    public ChildChannelHandlerInitializer(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("connections: " + connCounts.get());
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) {
                connCounts.incrementAndGet();
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) {
                connCounts.decrementAndGet();
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                channelGroup.writeAndFlush(msg);
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                // 通知所有已经连接的WebSocket 客户端新的客户端已经连接上了
                channelGroup.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
                // 将新的WebSocket Channel添加到ChannelGroup 中，以便它可以接收到所有的消息
                channelGroup.add(ctx.channel());
            }
        });
    }
}
