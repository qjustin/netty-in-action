/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.network.netty.book01.chapter00.test06.v01;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * Decodes the binary representation of a {@link BigInteger} prepended
 * with a magic number ('F' or 0x46) and a 32-bit integer length prefix into a
 * {@link BigInteger} instance.  For example, { 'F', 0, 0, 0, 1, 42 } will be
 * decoded into new BigInteger("42").
 */
public class BigIntegerDecoder extends ByteToMessageDecoder {
    private String remark;

    public BigIntegerDecoder(String remark) {
        this.remark = remark;

    }
    /**
     * 每条完整数据的组成：'F'+4个字节的长度+数据
     * 将传进来的number编码为二进制，在其前边加上'F'和4个字节的长度，作为前缀。
     * 例如：42被编码为：'F',0,0,0,1,42
     * @param ctx
     * @param in
     * @param out
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        System.out.println(remark + " call decode");
        // readableBytes获取可读字节数
        if (in.readableBytes() < 5) {
            return;
        }

        in.markReaderIndex();

        // readUnsignedByte：取1字节的内容;
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        // readInt：取4字节的内容
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }

        // readBytes：取指定长度的内容，返回ByteBuf类型；
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);

        // 字节转换成BigInteger
        out.add(new BigInteger(decoded));
    }
}
