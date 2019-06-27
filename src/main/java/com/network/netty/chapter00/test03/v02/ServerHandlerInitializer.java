package com.network.netty.chapter00.test03.v02;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ServerHandlerInitializer extends SimpleChannelInboundHandler<String> {
    public static AtomicInteger connCounts = new AtomicInteger();
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public ServerHandlerInitializer() {
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            System.out.println("connections: " + connCounts.get());
//            channels.writeAndFlush("boardcast: hahahahahah \n");
//        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception { // (4)
        // 最好排除当前的channel， 这里没有排除
        channels.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + s + "\n");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (5)
        connCounts.incrementAndGet();
        channels.add(ctx.channel());
        System.out.println("ChatClient:" + ctx.channel().remoteAddress() + "在线");
        System.out.println("connections: " + connCounts.get());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
        connCounts.decrementAndGet();
        channels.remove(ctx.channel());
        System.out.println("ChatClient:" + ctx.channel().remoteAddress() + "掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (7)
        System.out.println("ChatClient:" + ctx.channel().remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}
