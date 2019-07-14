package com.network.netty.book01.chapter12.demo08;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;

public class LengthBasedInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 使用LengthFieldBasedFrameDecoder 解码将帧长度编码到帧起始的前8 个字节中的消息
        // LengthFieldBasedFrameDecoder用前两个字节表示内容的长度。解码前14字节，解码后12字节，因为头两个字节表示内容的实际长度。
        pipeline.addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 8));
        // 添加FrameHandler以处理每个帧
        pipeline.addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            System.out.println(msg.toString(CharsetUtil.UTF_8));
        }
    }
}
