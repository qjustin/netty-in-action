//定义一个消息，其包装了另一个消息并带有发送者和接收者地址。其中M 是消息类型；A 是地址类型
interface AddressedEnvelope <M, A extends SocketAddress> extends ReferenceCounted

//提供了interface AddressedEnvelope 的默认实现
class DefaultAddressedEnvelope <M, A extends SocketAddress> implements AddressedEnvelope<M,A>

//扩展了DefaultAddressedEnvelope 以使用ByteBuf 作为消息数据容器
class DatagramPacket extends DefaultAddressedEnvelope <ByteBuf, InetSocketAddress> implements ByteBufHolder

//扩展了Netty 的Channel 抽象以支持UDP 的 多播组管理
interface DatagramChannel extends Channel

//定义了一个能够发送和接收AddressedEnvelope 消息的Channel 类型
class NioDatagramChannnel extends AbstractNioMessageChannel implements DatagramChannel
