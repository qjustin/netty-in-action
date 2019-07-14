package com.network.netty.book01.chapter12.demo10;

import io.netty.channel.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedStream;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.FileInputStream;

/**
 * 当Channel 的状态变为活动的时，WriteStreamHandler 将会逐块地把来自文件中的数
 * 据作为ChunkedStream 写入。数据在传输之前将会由SslHandler 加密。
 */
public class ChunkedWriteHandlerInitializer extends ChannelInitializer<Channel> {
    private final File file;
    private final SslContext sslCtx;

    public ChunkedWriteHandlerInitializer(File file, SslContext sslCtx) {
        this.file = file;
        this.sslCtx = sslCtx;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 将SslHandler 添加到ChannelPipeline 中
        pipeline.addLast(new SslHandler(sslCtx.newEngine(ch.alloc())));
        // 添加ChunkedWriteHandler以处理作为ChunkedInput传入的数据
        // 在需要将数据从文件系统复制到用户内存中时，可以使用ChunkedWriteHandler，
        // 它支持异步写大型数据流，而又不会导致大量的内存消耗。
        // 逐块输入 要使用你自己的ChunkedInput 实现，请在ChannelPipeline 中安装一个 ChunkedWriteHandler。
        pipeline.addLast(new ChunkedWriteHandler());
        // 一旦连接建立，WriteStreamHandler就开始写文件数据
        pipeline.addLast(new WriteStreamHandler());
    }
    public final class WriteStreamHandler extends ChannelInboundHandlerAdapter {
        // 当连接建立时，channelActive()方法将使用ChunkedInput写文件数据v
        // 注意:逐块输入 要使用你自己的ChunkedInput 实现，请在ChannelPipeline 中安装一个 ChunkedWriteHandler。
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            // ChunkedStream 从 InputStream 中逐块传输内容
            ctx.writeAndFlush(new ChunkedStream(new FileInputStream(file)));
        }
    }
}
