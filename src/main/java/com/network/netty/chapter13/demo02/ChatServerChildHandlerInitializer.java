package com.network.netty.chapter13.demo02;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServerChildHandlerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup channelGroup;

    public ChatServerChildHandlerInitializer(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline()
                .addLast(new HttpServerCodec()) // 1 解码
                .addLast(new ChunkedWriteHandler()) // 2 跳过
                .addLast(new HttpObjectAggregator(64 * 1024)) // 3得到完整的request
                .addLast(new HttpRequestHandler("/ws")) // 4 响应request
                .addLast(new WebSocketServerProtocolHandler("/ws")) // 5. 升级websocket
                .addLast(new TextWebSocketFrameHandler(channelGroup)); //
    }
}
