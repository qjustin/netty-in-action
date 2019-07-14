1. EmbeddedChannel 用于测试 ChannelHandler

2. EmbeddedChannel 方法表
writeInbound(Object... msgs):       将入站消息写到EmbeddedChannel 中。如果可以通过readInbound()方法从EmbeddedChannel 中读取数据，则返回true
readInbound():                      从EmbeddedChannel 中读取一个入站消息。任何返回的东西都穿越了整个ChannelPipeline。如果没有任何可供读取的，则返回null
writeOutbound(Object... msgs):      将出站消息写到EmbeddedChannel中。如果现在可以通过readOutbound()方法从EmbeddedChannel 中读取到什么东西，则返回true
readOutbound():                     从EmbeddedChannel 中读取一个出站消息。任何返回的东西都穿越了整个ChannelPipeline。如果没有任何可供读取的，则返回null
finish():                           将EmbeddedChannel 标记为完成，并且如果有可被读取的入站数据或者出站数据，则返回true。这个方法还将会调用EmbeddedChannel 上的close()方法

writeOutbound / readOutbound 写/读出站信息
writeInbound / readInbound 写/读入站信息