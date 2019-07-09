package com.network.netty.chapter00.test06.v02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class CustomerEncoder extends MessageToByteEncoder<String> {
    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        byte[] data = msg.getBytes(CharsetUtil.UTF_8);

        out.writeByte((byte)'$');
        out.writeInt(data.length);
        out.writeBytes(data);
    }
}
