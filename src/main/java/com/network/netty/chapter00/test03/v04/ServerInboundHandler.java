package com.network.netty.chapter00.test03.v04;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class ServerInboundHandler extends SimpleChannelInboundHandler<String> {
    private final AtomicInteger connCounts;
    private final ChannelGroup channels;

    public ServerInboundHandler(AtomicInteger connCounts, ChannelGroup channels) {
        this.connCounts = connCounts;
        this.channels = channels;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        channels.writeAndFlush("[" + ctx.channel().remoteAddress() + "]" + s + "\r\n").addListener(
                new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("boardcast finished : " + s);
                        }
                    }
                }
        );
        System.out.println("server boardcast message '" + s + "' to " + connCounts.get() + " clients.");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.add(ctx.channel());
        System.out.println(ctx.channel().remoteAddress() + " connected to server, current connection counts:" + connCounts.incrementAndGet());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
        System.out.println(ctx.channel().remoteAddress() + " disconnected from server, current connection counts:" + connCounts.decrementAndGet());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("ChatClient:" + ctx.channel().remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}
