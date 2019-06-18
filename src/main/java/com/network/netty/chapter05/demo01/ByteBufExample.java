package com.network.netty.chapter05.demo01;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ByteProcessor;

import java.nio.ByteBuffer;
import java.util.Random;

public class ByteBufExample {
    private final static Random random = new Random();
    private static final ByteBuf BYTE_BUF_FROM_SOMEWHERE = Unpooled.buffer(1024);
    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = null;
    private static void handleArray(byte[] array, int offset, int len) {}

    /**
     * 堆缓冲区-----------------------------------------------------------------------------------
     * 直接缓冲区---------------------------------------------------------------------------------
     * 复合缓冲区---------------------------------------------------------------------------------
     */

    /**
     * 代码清单 5-1 支撑数组(堆缓冲区)
     *
     * 使用场景：数据需要处理，
     * 优点：分配，释放比较轻量
     * 缺点：无法避免上下文切换，与拷贝次数 内核态用户态拷贝次数
     *
     * 检查是否有一个支撑数组, hasArray返回false时，尝试访问出发UnsupportedOperationException
     */
    public static void heapBuffer() {
        ByteBuf heapBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        //检查 ByteBuf 是否有一个支撑数组
        if (heapBuf.hasArray()) {
            //如果有，则获取对该数组的引用
            byte[] array = heapBuf.array();
            //计算第一个字节的偏移量
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            //获得可读字节数
            int length = heapBuf.readableBytes();
            //使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, offset, length);
        }
    }



    /**
     * 代码清单 5-2 访问直接缓冲区的数据
     *
     * 使用场景：直接传输不处理
     * 缺点：分配，释放比较昂贵，数据不在堆上需要进行一次复制
     * 优点：减少上下文切换，减少减少拷贝次数， DMA，zero copy
     *
     * 检查是否有一个支撑数组, hasArray返回false时，尝试访问出发UnsupportedOperationException
     *
     * 注意：访问直接缓冲区需要一次拷贝，因此，先通过ByteBuf.readableBytes()获得可读字节数，
     * 然后基于可读字节数创建一个heap byte[] 数组，最后通过ByteBuf.getBytes()将数据从直接内存拷贝到堆数组。然后再访问。
     */
    public static void directBuffer() {
        ByteBuf directBuf = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        //检查 ByteBuf 是否由数组支撑。如果不是，则这是一个直接缓冲区
        if (!directBuf.hasArray()) {
            //获取可读字节数
            int length = directBuf.readableBytes();
            //分配一个新的数组来保存具有该长度的字节数据
            byte[] array = new byte[length];
            //将字节复制到该数组
            directBuf.getBytes(directBuf.readerIndex(), array);
            //使用数组、偏移量和长度作为参数调用你的方法
            handleArray(array, 0, length);
        }
    }

    /**
     * 代码清单 5-3 JDK 中 通过 ByteBuffer 实现的复合缓冲区模式，这种模式效率低下而笨拙
     */
    public static void byteBufferComposite(ByteBuffer header, ByteBuffer body) {
        // Use an array to hold the message parts
        ByteBuffer[] message =  new ByteBuffer[]{ header, body };

        // Create a new ByteBuffer and use copy to merge the header and body
        ByteBuffer message2 = ByteBuffer.allocate(header.remaining() + body.remaining());
        message2.put(header);
        message2.put(body);
        message2.flip();
    }


    /**
     * 代码清单 5-4 使用 CompositeByteBuf 的复合缓冲区模式
     *
     * 复合缓冲区：为多个ByteBuff提供一个聚合视图，可以根据需要添加或删除ByteBuff实例，
     * 或者将多个缓冲区表示为单个合并缓冲区的虚拟表示。
     * CompositeByteBuf可能包含直接内存和非直接内存，如果CompositeByteBuf
     * 中只有一个实例那么hasArray返回该数组的hasArray，如果有多个实例，则永远返回false
     * 使用场景：body不变，head每次都要重新创建
     */
    public static void byteBufComposite() {
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf headerBuf = BYTE_BUF_FROM_SOMEWHERE; // can be backing or direct
        ByteBuf bodyBuf = BYTE_BUF_FROM_SOMEWHERE;   // can be backing or direct
        //将 ByteBuf 实例追加到 CompositeByteBuf
        messageBuf.addComponents(headerBuf, bodyBuf);
        //...
        //删除位于索引位置为 0（第一个组件）的 ByteBuf
        messageBuf.removeComponent(0); // remove the header
        //循环遍历所有的 ByteBuf 实例
        for (ByteBuf buf : messageBuf) {
            System.out.println(buf.toString());
        }
    }


    /**
     * 代码清单 5-5 访问 CompositeByteBuf 中的数据
     *
     * 需要使用类似于访问直接缓冲区的模式
     *
     * 这种模式参考ZeroCopy，你需要知道它带来的影响
     */
    public static void byteBufCompositeArray() {
        CompositeByteBuf compBuf = Unpooled.compositeBuffer();
        //获得可读字节数
        int length = compBuf.readableBytes();
        //分配一个具有可读字节数长度的新数组
        byte[] array = new byte[length];
        //将字节读到该数组中
        compBuf.getBytes(compBuf.readerIndex(), array);
        //使用偏移量和长度作为参数使用该数组
        handleArray(array, 0, array.length);
    }


    /**
     * 字节操作
     *
     * 随机访问索引 ---------------------------------------------------------------------------------
     *          随机的意思时直接指定索引位置(根据下标访问)来访问缓冲区
     *          使用需要一个索引值参数的方法来访问缓冲区，不会改变索引
     *          ByteBuf.capacity() - 1 = 最后一个字节的索引
     * 顺序访问索引----------------------------------------------------------------------------------
     *          ByteBuf 被 readerIndex(已读)，writerIndex(已写)，capacity(容量) 三个索引划分为三个区域
     * 可丢弃字节------------------------------------------------------------------------------------
     *          ByteBuf.discardReadBytes() 调用后后 readerIndex = 0，writeIndex 减少，capacity 增加
     *          该方法导致内存复制（将可读部分前移动），使用需谨慎。
     * 可读字节--------------------------------------------------------------------------------------
     *          ByteBuf.isReadable() 判断是否可读
     *          以 read/skip 开头的方法都会增加readerIndex
     *          以 ByteBuf 为参数并且没有指定索引，writerIndex 增加，例如:sourceBuf.readBytes(ByteBuf dest), 读取dest中的数据写到sourceBuf
     * 可写字节--------------------------------------------------------------------------------------
     *          buffer.writableBytes() >= 4 判断是否可以写入四个字节
     *          以 write 开头的方法增加writeIndex
     *          以 ByteBuf 为参数并且没有指定索引，readerIndex 增加，例如:sourceBuf.writeBytes(ByteBuf dest), 读取sourceBuf中的数据写到dest
     */

    /**
     * 代码清单 5-6 访问数据
     */
    public static void byteBufRelativeAccess() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        for (int i = 0; i < buffer.capacity(); i++) {
            byte b = buffer.getByte(i);
            System.out.println((char) b);
        }
    }

    /**
     * 索引管理
     *
     * mark 标记
     * reset 重置
     * clear 将readerIndex和writerIndex都设置为0，但不清除内容
     *
     */



    /**
     * 查找操作
     *
     * indexOf（）
     * ByteBufProcessor
     */

    /**
     * 代码清单 5-9 使用 ByteBufProcessor 来寻找\r
     *
     * use {@link io.netty.util.ByteProcessor in Netty 4.1.x}
     */
    public static void byteBufProcessor() {
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE;
        int index = buffer.forEachByte(ByteProcessor.FIND_CR);
    }

    /**
     * 派生缓冲区
     * 下面的方法都将返回一个新的ByteBuf实例，具有自己的索引和标记索引，但底层存储是共享的。
     * duplicate()
     * slice();
     * slice(int,int)
     * Unpooled.unmodifiableBuffer()
     * order(ByteOrder)
     * readSlice(int)
     * 如果希望要一个真实的副本，底层数据不共享，请使用Copy。
     */


    /**
     * 读写操作
     *
     * getXxx/setXxx 从给定索引开始，但不改变
     * readXxx/writeXxx 从给定索引开始，根据以访问过的字节数修改索引
     * readableBytes / writeableBytes 返回 可读/可写字节数
     *
     */

    /**
     * ByteBufHolder接口
     *
     * ByteBufHolder接口 为Netty 提供了缓冲区池化，其中可以从池中借用ByteBuf，并且再需要时自动释放。
     *
     * content() 返回ByteBufHolder 持有的所有ByteBuf
     * copy() 返回ByteBufHolder的一个深拷贝，包括一个其所包含的ByteBuf的非共享拷贝
     * duplicate() 返回ByteBufHolder的一个浅拷贝，包括一个其所包含的ByteBuf的共享拷贝
     */


    /**
     *  ByteBuf分配
     *
     *  ByteBufAllocator接口：按需分配，Netty通过ByteBufAllocator接口实现了ByteBuf的池化。
     *
     *      buffer() 返回基于堆或者直接内存存储的ByteBuf
     *      heapBuffer() 返回基于堆的ByteBuf
     *      directBuffer() 返回基于直接内存的ByteBuf
     *      compositeBuffer() 返回基于堆或者直接内存存储的ByteBuf 来扩展CompositeByteBuf
     *      compositeDirectBuffer() 返回基于直接内存的ByteBuf 来扩展CompositeByteBuf
     *      compositeHeapBuffer() 返回基于堆的ByteBuf 来扩展CompositeByteBuf
     *      ioBuffer() 返回用于套接字I/O操作的ByteBuf
     *
     *      可通过 Channel 或 ChannelHandlerContext 来获取一个ByteBufAllocator的引用
     *
     *      Netty提供了两种ByteBufAllocator实现：
     *      PooledByteBufAllocator(默认)：池化的ByteBuf实例以提高性能，并最大限度减少内存碎片（底层jemalloc实现）
     *      UnpolledByteBufAllocator：非池化的ByteBuf，每次它被调用时都会返回一个新的实例
     *
     * Unpooled 缓冲区
     *      Unpooled提供的静态方法用于创建非池化的ByteBuf
     *      buffer() 返回一个非池化基于堆的ByteBuf
     *      directBuffer() 返回一个非池化基于直接内存的ByteBuf
     *      wrappedBuffer() 返回一个包装了给定数据的ByteBuf
     *      copiedBuffer() 返回一个复制了给定数据的ByteBuf
     */

    /**
     * 引用计数
     *
     * ByteBuf 和ByteBufHolder引入了引用计数技术他们都实现了ReferenceCounted接口
     *
     */
    /**
     * 代码清单 5-15 引用计数
     * */
    public static void referenceCounting(){
        Channel channel = CHANNEL_FROM_SOMEWHERE; //get reference form somewhere
        //从 Channel 获取ByteBufAllocator
        ByteBufAllocator allocator = channel.alloc();
        //...
        //从 ByteBufAllocator分配一个 ByteBuf
        ByteBuf buffer = allocator.directBuffer();
        //检查引用计数是否为预期的 1
        assert buffer.refCnt() == 1;
        //...
    }

    /**
     * 代码清单 5-16 释放引用计数的对象
     */
    public static void releaseReferenceCountedObject(){
        ByteBuf buffer = BYTE_BUF_FROM_SOMEWHERE; //get reference form somewhere
        //减少到该对象的活动引用。当减少到 0 时，该对象被释放，并且该方法返回 true
        boolean released = buffer.release();
        //...
    }
}
