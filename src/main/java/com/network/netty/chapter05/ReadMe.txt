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