package com.network.netty.chapter14.demo01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// 扩展SimpleChannelInboundHandler 以处理LogEvent 消息
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当异常发生时，打印栈跟踪信息，并关闭对应的Channel
        cause.printStackTrace();
        ctx.close();
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        // 创建StringBuilder，并且构建输出的字符串
        StringBuilder builder = new StringBuilder();
        builder.append(event.getReceivedTimestamp());
        builder.append(" [");
        builder.append(event.getSource().toString());
        builder.append("] [");
        builder.append(event.getLogfile());
        builder.append("] : ");
        builder.append(event.getMsg());
        // 打印 LogEvent的数据
        System.out.println(builder.toString());
    }
}