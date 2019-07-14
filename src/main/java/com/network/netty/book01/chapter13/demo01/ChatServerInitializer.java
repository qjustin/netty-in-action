package com.network.netty.book01.chapter13.demo01;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ChatServerInitializer extends ChannelInitializer<Channel> {
    private final ChannelGroup group;
    public ChatServerInitializer(ChannelGroup group) {
        this.group = group;
    }

    /**
     * 将所有需要的ChannelHandler添加到ChannelPipeline 中
     *
     * HttpServerCodec：将字节解码为HttpRequest、HttpContent 和LastHttpContent。
     *                  并将HttpRequest、HttpContent 和LastHttpContent 编码为字节
     * ChunkedWriteHandler：写入一个文件的内容
     * HttpObjectAggregator：将一个HttpMessage 和跟随它的多个HttpContent 聚合为单个FullHttpRequest
     *                       或者FullHttpResponse（取决于它是被用来处理请求还是响应）。安装了这个之后，
     *                       ChannelPipeline 中的下一个ChannelHandler 将只会收到完整的HTTP 请求或响应
     * HttpRequestHandler：处理FullHttpRequest（那些不发送到/ws URI 的请求）
     * WebSocketServerProtocolHandler：按照WebSocket 规范的要求，处理WebSocket 升级握手、
     *                                 PingWebSocketFrame 、PongWebSocketFrame 和 CloseWebSocketFrame
     * TextWebSocketFrameHandler：处理TextWebSocketFrame 和握手完成事件
      */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将字节解码为HttpRequest、HttpContent 和LastHttpContent。并将HttpRequest、HttpContent 和
        // LastHttpContent 编码为字节
        pipeline.addLast(new HttpServerCodec());
        // 写入一个文件的内容
        pipeline.addLast(new ChunkedWriteHandler());
        // 将一个HttpMessage 和跟随它的多个HttpContent 聚合为单个FullHttpRequest或者FullHttpResponse
        // （取决于它是被用来处理请求还是响应）。安装了这个之后，ChannelPipeline 中的下一个ChannelHandler
        // 将只会收到完整的HTTP 请求或响应
        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
        // 处理FullHttpRequest（那些不发送到/ws URI 的请求）
        pipeline.addLast(new HttpRequestHandler("/ws"));
        // 按照WebSocket 规范的要求，处理WebSocket 升级握手、PingWebSocketFrame 、PongWebSocketFrame 和 CloseWebSocketFrame
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 处理TextWebSocketFrame 和握手完成事件
        pipeline.addLast(new TextWebSocketFrameHandler(group));
    }
}
