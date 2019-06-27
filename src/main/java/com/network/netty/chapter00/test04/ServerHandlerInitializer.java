package com.network.netty.chapter00.test04;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ServerHandlerInitializer extends ChannelInboundHandlerAdapter {
    private final ChannelGroup channelGroup;

    private AtomicInteger connCounts = new AtomicInteger();

    public ServerHandlerInitializer(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println("connections: " + connCounts.get());
        }, 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }

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
}
