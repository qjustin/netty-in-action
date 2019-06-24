package com.network.netty.chapter11.demo02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ShortToByteEncoder extends MessageToByteEncoder<Short> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Short msg, ByteBuf byteBuf) throws Exception {
        // 写入ByteBuf 中，其将随后被转发给ChannelPipeline 中的下一个ChannelOutboundHandler
        byteBuf.writeShort(msg);
    }
}
