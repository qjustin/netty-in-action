package com.network.netty.chapter00.test03.v03;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class PushClientInBoundHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            ctx.writeAndFlush("hello world all guys current thread is : " + Thread.currentThread().getId() + " Time: " + System.currentTimeMillis() + "\r\n");
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        System.out.println(s);
    }
}
