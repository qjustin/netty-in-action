package com.network.netty.chapter00.test03.v04;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class PushClientInBoundHandler extends SimpleChannelInboundHandler<String> {
    public static AtomicInteger id = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            ctx.writeAndFlush("hello world all guys current thread id:" + Thread.currentThread().getId() + " / message id: " + id.getAndIncrement() + "\r\n");

        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }
}
