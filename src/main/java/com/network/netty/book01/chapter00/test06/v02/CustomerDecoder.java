package com.network.netty.book01.chapter00.test06.v02;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * $ + 4 byte（4字节的int来表示data长度） + data
 */
public class CustomerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 一条消息由 $ + 4 byte（表示data长度） + data
        // 因此需要先将$ + 4 byte（表示data长度）读取出来，也就是5个字节。
        // F表示消息的起始位置，4 byte表示数据长度，只有读取了数据长度我们才能完整地读取数据
        // 判断是否有足够的字节数可读
        if (in.readableBytes() < 5) {
            return;
        }

        // 将当前readerIndex备份到markedReaderIndex
        in.markReaderIndex();

        // 取1字节的内容, 读取F
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != '$') {
            // 将当前readerIndex设置为markedReaderIndex
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        // 读取8字节的内容，读取data长度
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // 开辟一个空间，用户存放数据
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        out.add(new String(data, CharsetUtil.UTF_8));
    }
}
