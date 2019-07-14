package com.network.netty.book01.chapter05.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 知识点
 * 1. 缓冲区类型（再哪里分配内存）
 * 2. 缓冲区管理 （操作API）
 *          访问，读写，派生缓冲区（切片，duplicate）
 * 3. 缓冲区池化与非池化
 *          ByteBufAllocator接口 的两种实现，与Unpooled
 * 4. 引用计数
 */
public class Server {
    public static void main(String[] args) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(1127)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {

                                    if (byteBuf.hasArray()) {
                                        // 获取对数组的引用
                                        byte[] array = byteBuf.array();
                                        // 计算第一个字节的偏移量
                                        int offset = byteBuf.arrayOffset() + byteBuf.readerIndex();
                                        // 获取可读字节数
                                        int length = byteBuf.readableBytes();
                                        System.out.println("offset:" + offset + ",length:" + length);
                                    }

                                    // 直接缓冲区的用法
                                    // 使用场景：直接传输不处理
                                    // 缺点：分配，释放比较昂贵，数据不在堆上需要进行一次复制
                                    // 优点：减少上下文切换，减少减少拷贝次数， DMA，zero copy
                                    // 检查是否有一个支撑数组, hasArray返回false时，尝试访问出发UnsupportedOperationException
                                    if (byteBuf.hasArray()) {
                                        // 获取可读字节数
                                        int length = byteBuf.readableBytes();
                                        // 分配一个新的数组来保存具有该长度的字节数据
                                        byte[] array = new byte[length];
                                        // 将字节复制到该数组
                                        byteBuf.getBytes(byteBuf.readerIndex(), array);
                                    }

                                    // 复合缓冲区：为多个ByteBuff提供一个聚合视图，可以根据需要添加或删除ByteBuff实例，
                                    //            或者将多个缓冲区表示为单个合并缓冲区的虚拟表示。
                                    //            CompositeByteBuf可能包含直接内存和非直接内存，如果CompositeByteBuf
                                    //            中只有一个实例那么hasArray返回该数组的hasArray，如果有多个实例，则永远返回false
                                    // 使用场景：
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
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
