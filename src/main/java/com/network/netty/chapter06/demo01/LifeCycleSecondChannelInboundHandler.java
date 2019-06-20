package com.network.netty.chapter06.demo01;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class LifeCycleSecondChannelInboundHandler implements ChannelInboundHandler {
    @Override
    public void channelRegistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelRegistered:当Channel已经注册到它的EventLoop并且能够处理I/O时被调用");
        channelHandlerContext.fireChannelRegistered();
//        channelHandlerContext.pipeline().fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelUnregistered:当Channel从它的EventLoop注销并且无法处理任何I/O时被调用");
        channelHandlerContext.fireChannelUnregistered();
//        channelHandlerContext.pipeline().fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelActive:当Channel处于活动状态时被调用;Channel已经连接/绑定并且已经就绪");
        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("hello\r\n", CharsetUtil.UTF_8));
        channelHandlerContext.fireChannelActive();
//        channelHandlerContext.pipeline().fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelInactive:当Channel离开活动状态并且不再连接它的远程节点时被调用");
        channelHandlerContext.fireChannelInactive();
//        channelHandlerContext.pipeline().fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("channelRead:当从Channel读取数据时被调用");
        //丢弃已接收得消息
        ReferenceCountUtil.release(o);
        channelHandlerContext.fireChannelRead(o);
//        channelHandlerContext.pipeline().fireChannelRead(o);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelReadComplete:当Channel上的一个读操作完成时被调用");
        channelHandlerContext.fireChannelReadComplete();
//        channelHandlerContext.pipeline().fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        System.out.println("userEventTriggered:当ChannelInboundHandler.fireUserEventTriggered()方法被调用时调用该方法，因为一个POJO被传经了ChannelPipeline");
        channelHandlerContext.fireUserEventTriggered(o);
        //        channelHandlerContext.pipeline().fireUserEventTriggered(o);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("channelWritabilityChanged:当Channel的写状态发生改变时被调用。用户可以确保写操作不会完成得太快(以避免发生OOM)，或者可以在Channel变为在次可写时恢复写入。" +
                "可以通过调用Channel得isWritable()方法来检测Channel得可写性。与可写性相关得阈值可以通过Channel.config().setWriteHighWaterMark()和" +
                "Channel.config().setWriteLowWaterMark()方法来设置");
        channelHandlerContext.fireChannelWritabilityChanged();
//        channelHandlerContext.pipeline().fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
        System.out.println("exceptionCaught:当处理过程中在ChannelPipeline中有错误产生时被调用");
        channelHandlerContext.fireExceptionCaught(throwable);
//        channelHandlerContext.pipeline().fireExceptionCaught(throwable);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("handlerAdded:当把ChannelHandler添加到ChannelPipeline中时被调用");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext channelHandlerContext) throws Exception {
        System.out.println("handlerRemoved:当从ChannelPipeline中移除ChannelHandler时被调用");
    }
}
