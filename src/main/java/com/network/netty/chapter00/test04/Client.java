package com.network.netty.chapter00.test04;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * java -Xmx3100m -Xms3100m -jar iotest.one-jar.jar
 */
public class Client {
    public static void main(String[] args) {
        // 服务器监听起始端口
        int beginPort = 8000;
        // 服务器监听端口数量
        int nPort = 100;

        EventLoopGroup g = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(g)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ClientHandlerInitializer());

            for (int i = 0, j = 0; i < 50000; i++, j++) {
                try {
                    ChannelFuture future = b.connect("192.168.141.103", 8000 + (j % nPort));
                    if (j % nPort == 0) {
                        future.addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                if (channelFuture.isSuccess()) {
                                    channelFuture.channel().eventLoop().scheduleAtFixedRate(new SendTimer(channelFuture), 5, 5, TimeUnit.SECONDS);
                                }
                            }
                        });
                    }
//                    future.get();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
            Thread.sleep(1000000000L);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                g.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    static final class SendTimer implements Runnable {
        static ByteBuf byteBuf = Unpooled.buffer(1024);
        ChannelFuture future;

        static {
            for (int i = 0; i < byteBuf.capacity(); i++) {
                byteBuf.writeByte((byte) i);
            }
        }

        public SendTimer(ChannelFuture future) {
            this.future = future;
        }

        public void run() {
            future.channel().writeAndFlush(byteBuf.retain());
        }
    }
}
