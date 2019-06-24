package com.network.netty.chapter12.demo09;

import io.netty.channel.*;

import java.io.FileInputStream;

/**
 * 这个示例只适用于文件内容的直接传输，不包括应用程序对数据的任何处理。
 *
 * 在需要将数据从文件系统复制到用户内存中时，可以使用ChunkedWriteHandler，
 * 它支持异步写大型数据流，而又不会导致大量的内存消耗。参考demo10
 */
public class FileRegionHandlerInitializer extends ChannelInitializer<Channel> {
    private String file=null;
    private FileInputStream in = null;
    private FileRegion region = null;

    public FileRegionHandlerInitializer(String file) {
        this.file = file;
        try {
            in = new FileInputStream(file);
            region = new DefaultFileRegion(in.getChannel(), 0, file.length());
        } catch (Exception ex) {
            ;
        }
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                channel.writeAndFlush(region).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (!channelFuture.isSuccess()) {
                            System.err.println(channelFuture.cause());
                        }
                    }
                });
            }
        });
    }
}
