package com.network.netty.chapter13.demo01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * TextWebSocketFrameHandler 只有一组非常少量的责任。当和新客户端的WebSocket
 * 握手成功完成之后，它将通过把通知消息写到ChannelGroup 中的所有Channel 来通知所
 * 有已经连接的客户端，然后它将把这个新Channel 加入到该ChannelGroup 中。
 * 如果接收到了TextWebSocketFrame 消息，TextWebSocketFrameHandler 将调用
 * TextWebSocketFrame 消息上的retain()方法，并使用writeAndFlush()方法来将它传
 * 输给ChannelGroup，以便所有已经连接的WebSocket Channel 都将接收到它。
 * 和之前一样，对于retain()方法的调用是必需的，因为当channelRead0()方法返回时，
 * TextWebSocketFrame 的引用计数将会被减少。由于所有的操作都是异步的，因此，writeAnd-
 * Flush()方法可能会在channelRead0()方法返回之后完成，而且它绝对不能访问一个已经失
 * 效的引用。
 * 因为Netty 在内部处理了大部分剩下的功能，所以现在剩下唯一需要做的事情就是为每个新创建
 * 的Channel 初始化其ChannelPipeline。为此，我们将需要一个ChannelInitializer。
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final ChannelGroup group;

    public TextWebSocketFrameHandler(ChannelGroup group) {
        this.group = group;
    }

    // 重写userEventTriggered()方法以处理自定义事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            // 如果该事件表示握手成功，则从该Channelipeline中移除HttpRequestHandler因为将不会接收到任何HTTP 消息了
            ctx.pipeline().remove(HttpRequestHandler.class);
            // 通知所有已经连接的WebSocket 客户端新的客户端已经连接上了
            group.writeAndFlush(new TextWebSocketFrame("Client " + ctx.channel() + " joined"));
            // 将新的WebSocket Channel添加到ChannelGroup 中，以便它可以接收到所有的消息
            group.add(ctx.channel());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 增加消息的引用计数，并将它写到ChannelGroup 中所有已经连接的客户端
        group.writeAndFlush(msg.retain());
    }
}

