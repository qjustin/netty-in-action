package com.network.netty.book01.chapter11.demo04;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CharToByteEncoder extends MessageToByteEncoder<Character> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Character msg, ByteBuf out) throws Exception {
        out.writeChar(msg);
    }
}
