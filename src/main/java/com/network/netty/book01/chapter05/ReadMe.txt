知识点：
1. 数据处理的两个组件 ByteBuf 和 ByteBufHolder
    可扩展，通过内置复合缓冲区类型实现ZeroCopy，自动扩容，读写无须切换，链式调用，引用技术，池化技术
2. ByteBuf write/read操作 index 都会增加， 而get， set 不会。
3. ByteBuf 使用三种模式（使用场景，优缺点）：
    堆缓冲区，直接缓冲区，复合缓冲区

/**
 * 知识点
 * 1. 缓冲区类型（再哪里分配内存）
 * 2. 缓冲区管理 （操作API）
 *          访问，读写，派生缓冲区（切片，duplicate）
 * 3. 缓冲区池化与非池化
 *          ByteBufAllocator接口 的两种实现，与Unpooled
 * 4. 引用计数
 */


1. readIndex/writeIndex

2. readXxx/writeXxx(会改变index)


BtyeBuf读操作主要提供以下功能：
readByte：取1字节的内容；
skipBytes： 跳过内容
readUnsignedByte：取1字节的内容，返回（(short) (readByte() & 0xFF)）；（能把负数转换为无符号吗？）
readShort：取2字节的内容，返回转换后的short类型；
readUnsignedShort：取2字节的内容，返回readShort() & 0xFFFF；
readMedium：取3字节的内容，返回转换后的int类型；
readUnsignedMedium：取3字节的内容，返回转换后的int类型；
readInt：取4字节的内容；
readUnsignedInt：取4字节的内容，返回readInt() & 0xFFFFFFFFL；
readLong：取8字节的内容；
readChar：取1字节的内容；
readFloat：取4字节的int内容，转换为float类型；
readDouble：取8字节的long内容，转换为double类型；
readBytes：取指定长度的内容，返回ByteBuf类型；
readSlice：取指定长度的内容，返回ByteBuf类型；
readBytes：取指定长度的内容到目标容器。

BtyeBuf写操作主要提供以下功能：
writeBoolean
writeByte
writeShort
writeMedium
writeInt
writeLong
writeChar
writeFloat
writeDouble
writeBytes
writeZero

3. getXxx/setXxx (不会改变index)


4. mark/reset
markReaderIndex: 将当前readerIndex备份到markedReaderIndex
resetReaderIndex: 将当前readerIndex设置为markedReaderIndex
markWriterIndex: 将当前readerIndex备份到markedWriterIndex中
resetWriterIndex: 将当前readerIndex设置为markedWriterIndex
