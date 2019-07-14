
知识点

1. 传输API的核心接口 Channel接口
2. 新建的Channel会与那些组件绑定呢？
3. Channel提供了那些方法？
4. 内置传输 深入研究
    NIO
    OIO
    Epoll
    Local
    Embedded
5. SCTP

技巧：
1. Channel的用法，Channel是线程安全的，多线程使用同一个Channel写消息 参考demo03
      扩散写是否可以使用这种方式呢?
