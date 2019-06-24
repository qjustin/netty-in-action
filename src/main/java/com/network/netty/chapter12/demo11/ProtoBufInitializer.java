package com.network.netty.chapter12.demo11;

import com.google.protobuf.MessageLite;
import io.netty.channel.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

public class ProtoBufInitializer extends ChannelInitializer<Channel> {
    private final MessageLite lite;

    public ProtoBufInitializer(MessageLite lite) {
        this.lite = lite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 添加ProtobufVarint32FrameDecoder以分隔帧
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        // 添加ProtobufEncoder以处理消息的编码
        pipeline.addLast(new ProtobufEncoder());
        // 添加ProtobufDecoder以解码消息
        pipeline.addLast(new ProtobufDecoder(lite));
        // 添加Object Handler以处理解码消息
        pipeline.addLast(new ObjectHandler());
    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Object> {
        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg.toString());
        }
    }
}
