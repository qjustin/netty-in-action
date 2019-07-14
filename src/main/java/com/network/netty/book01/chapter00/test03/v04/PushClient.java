package com.network.netty.book01.chapter00.test03.v04;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import static com.network.netty.book01.chapter00.test03.v03.Constants.BEGIN_PORT;
import static com.network.netty.book01.chapter00.test03.v03.Constants.N_PORT;

public class PushClient {
    private static final PushClientInBoundHandler pushClientInBoundHandler = new PushClientInBoundHandler();

    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group);
            boot.channel(NioSocketChannel.class);
            boot.option(ChannelOption.SO_REUSEADDR, true);
            boot.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline()
                            .addLast(new LineBasedFrameDecoder(1024))
                            .addLast(new StringDecoder())
                            .addLast(new StringEncoder())
                            .addLast(pushClientInBoundHandler);
                }
            });

            for (int i = 0; i < 1; i++) {
                int port = BEGIN_PORT + (i % N_PORT);

                try {
                    ChannelFuture future = boot.connect(Constants.SERVER_HOST, port).addListener(
                            new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                    if (!channelFuture.isSuccess()) {
                                        System.out.println("连接失败, Channel.id:" + channelFuture.channel().id());
                                    }
                                }
                            }
                    );
                    future.get();
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
