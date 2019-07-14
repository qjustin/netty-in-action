1. 为什么引导类时Cloneable的？
因为可能需要创建配置完全相同的Channel，当调用clone()方法立即返回一个可以立即使用的引导类实例。
但是需要注意的是：这个通过clone()方法创建的引导类实例的EventLoopGroup是一个浅拷贝，这个浅拷贝的EventLoopGroup
将在所有通过Clone方法创建的Channel之间共享。这些Channel生命周期很短，用于创建一个Channel进行一次Http请求。


2. Channel 与 EventLoopGroup 的兼容性?
    不能使用不同前缀的组件

    GROUP:NioEventLoopGroup
            Channel: NioDatagramChannel (用于UDP)
                     NioServerSocketChannel (Server)
                     NioSocketChannel (Client)
    GROUP:OIoEventLoopGroup
            Channel: OioDatagramChannel (用于UDP)
                     OioServerSocketChannel (Server)
                     OioSocketChannel (Client)

3. ServerChannel 创建子Channel 这些Channel代表已经接受的连接。因此会有childHandler(),chailOption(),chailAttr()方法
    ServerBootstrap 在bind() 方法调用时创建了一个ServerChannel，该ServerChannel用于监听端口创建连接(Channel)，以及管理多个子Channel

4. 如何从Channel引导客户端
代理是最好的例子：客户端通过代理服务器查询数据；
流程：客户端连接到代理服务器 -> 代理服务器作为客户端连接到 -> 数据服务器
方法1：创建全新的Bootstrap与dataChannel，然后dataChannel获取数据，写入clientChannel 这种方式效率低下
方法2：clientChannel直接引导过去查询数据 demo2