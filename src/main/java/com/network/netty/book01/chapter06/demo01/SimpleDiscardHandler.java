package com.network.netty.book01.chapter06.demo01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SimpleDiscardHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果一个消息被丢弃或被消费，并且没有传递给ChannelPipeline中的下一个Handler，那么就必须调用ReferenceCountUtil.release(msg); 释放资源
        //   ReferenceCountUtil.release(msg);
        System.out.println("SimpleChannelInboundHandler 会自动释放资源，无须调用release方法");
    }
}
