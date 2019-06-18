package com.network.netty.chapter06.demo01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class DiscardHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 如果一个消息被丢弃或被消费，并且没有传递给ChannelPipeline中的下一个Handler，那么就必须调用ReferenceCountUtil.release(msg); 释放资源
        ReferenceCountUtil.release(msg);
        System.out.println("如果一个消息被丢弃或被消费，并且没有传递给ChannelPipeline中的下一个Handler，那么就必须调用ReferenceCountUtil.release(msg); 释放资源");
    }
}
