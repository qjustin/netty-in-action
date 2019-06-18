package com.network.netty.chapter06.demo01;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

public class LifeCycleChannelOutboundHandler implements ChannelOutboundHandler {
    @Override
    public void bind(ChannelHandlerContext channelHandlerContext, SocketAddress socketAddress, ChannelPromise channelPromise) throws Exception {
        System.out.println("bind: 当请求将Channel绑定到本地地址时被调用");
    }

    @Override
    public void connect(ChannelHandlerContext channelHandlerContext, SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) throws Exception {
        System.out.println("connect: 当请求将Channel连接远程节点时被调用");
    }

    @Override
    public void disconnect(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) throws Exception {
        System.out.println("disconnect: 当请求将Channel从远程节点断开时被调用");
    }

    @Override
    public void close(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) throws Exception {
        System.out.println("close: 当情求关闭Channel时被调用");
    }

    @Override
    public void deregister(ChannelHandlerContext channelHandlerContext, ChannelPromise channelPromise) throws Exception {
        System.out.println("deregister: 当请求将Channel从它的EventLoop注销时被调用");
    }

    @Override
    public void read(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("read: 当请求从Channel读取更多的数据时被调用");
    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {
        System.out.println("write: 当请求通过Channel将入队数据冲刷到远程节点时被调用");
    }

    @Override
    public void flush(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("flush: 当请求通过Channel将数据写道远程节点时被调用");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("handlerAdded:当把ChannelHandler添加到ChannelPipeline中时被调用");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("handlerRemoved:当从ChannelPipeline中移除ChannelHandler时被调用");
    }

    /**
     * @param channelHandlerContext
     * @param throwable
     * @deprecated
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        System.out.println("exceptionCaught:当处理过程中在ChannelPipeline中有错误产生时被调用");
    }
}
