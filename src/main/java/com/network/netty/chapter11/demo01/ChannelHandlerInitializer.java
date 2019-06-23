package com.network.netty.chapter11.demo01;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public class ChannelHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new ToIntegerDecoder());
    }
}
