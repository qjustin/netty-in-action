1. WebSocket
从标准的HTTP或者HTTPS协议切换到WebSocket时，将会使用一种称为升级握手①的机
制。因此，使用WebSocket的应用程序将始终以HTTP/S作为开始，然后再执行升级。这个升级动
作发生的确切时刻特定于应用程序；它可能会发生在启动时，也可能会发生在请求了某个特定的
URL之后。

2. WebSocketFrame 6种帧
BinaryWebSocketFrame：包含了二进制数据
TextWebSocketFrame：包含了文本数据
ContinuationWebSocketFrame：包含属于上一个BinaryWebSocketFrame或TextWebSocketFrame 的文本数据或者二进制数据
CloseWebSocketFrame：表示一个CLOSE 请求，包含一个关闭的状态码和关闭的原因
PingWebSocketFrame：请求传输一个PongWebSocketFrame
PongWebSocketFrame：作为一个对于PingWebSocketFrame 的响应被发送

我们的聊天应用程序将使用下面几种帧类型：
CloseWebSocketFrame；
PingWebSocketFrame；
PongWebSocketFrame；
TextWebSocketFrame。

TextWebSocketFrame 是我们唯一真正需要处理的帧类型。为了符合WebSocket RFC，Netty 提供了WebSocketServerProtocolHandler 来处理其他类型的帧。

websocket 聊天室
    处理HTTP 请求首
    处理WebSocket帧
    初始化ChannelPipeline
    引导

WebSocket 协议升级之前的ChannelPipeline 的状态：
HttpRequestDecoder
    -> HttpResponseEncoder
        -> HttpObjectAggregator
            -> HttpReqeustHandler
                -> WebSocketServerProtocolHandler
                    -> TextWebSocketFrameHandler

当WebSocket 协议升级完成之后的ChannelPipeline 的状态:为了性能最大化，它将移除任何不再被WebSocket 连接所需要的ChannelHandler
WebSocketFrameDecoder13
    -> WebSocketFrameEncoder13
        -> WebSocketServerProtocolHandler
            -> TextWebSocketFrameHandler
引导
