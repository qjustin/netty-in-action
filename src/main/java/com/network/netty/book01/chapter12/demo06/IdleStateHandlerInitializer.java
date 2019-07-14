package com.network.netty.book01.chapter12.demo06;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 这个示例演示了如何使用IdleStateHandler 来测试远程节点是否仍然还活着，并且在它
 * 失活时通过关闭连接来释放资源。
 * 如果连接超过60 秒没有接收或者发送任何的数据，那么IdleStateHandler 将会使用一个
 * IdleStateEvent 事件来调用fireUserEventTriggered()方法。HeartbeatHandler 实现
 * 了userEventTriggered()方法，如果这个方法检测到IdleStateEvent 事件，它将会发送心
 * 跳消息，并且添加一个将在发送操作失败时关闭该连接的ChannelFutureListener 。
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        // IdleStateHandler 将在被触发时发送一个IdleStateEvent 事件
        pipeline.addLast(new IdleStateHandler(0,0,60, TimeUnit.SECONDS));
        // 将一个HeartbeatHandler添加到ChannelPipeline中
        pipeline.addLast(new HeartbeatHandler());
    }

    public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEARTBEAT", CharsetUtil.ISO_8859_1));

        // 实现userEventTriggered()方法以发送心跳消息
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                // 发送心跳消息, 并在发送失败时关闭该连接
                ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                // 不是IdleStateEvent事件，所以将它传递给下一个ChannelInboundHandler
                super.userEventTriggered(ctx, evt);
            }
        }
    }

}
