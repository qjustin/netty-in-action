package com.network.netty.book01.chapter13.demo02;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 扩展SimpleChannelInboundHandler 以处理FullHttpRequest 消息
 * 如果该HTTP 请求指向了地址为/ws 的URI，那么HttpRequestHandler 将调用FullHttp-
 * Request 对象上的retain()方法，并通过调用fireChannelRead(msg)方法将它转发给下一
 * 个ChannelInboundHandler 。之所以需要调用retain()方法，是因为调用channelRead()
 * 方法完成之后，它将调用FullHttpRequest 对象上的release()方法以释放它的资源。（参见
 * 我们在第6 章中对于SimpleChannelInboundHandler 的讨论。）
 * 如果客户端发送了HTTP 1.1 的HTTP 头信息Expect: 100-continue，那么Http-
 * RequestHandler 将会发送一个100 Continue 响应。在该HTTP 头信息被设置之后，Http-
 * RequestHandler 将会写回一个HttpResponse 给客户端。这不是一个FullHttp-
 * Response，因为它只是响应的第一个部分。此外，这里也不会调用writeAndFlush()方法，
 * 在结束的时候才会调用。
 * 如果不需要加密和压缩，那么可以通过将index.html 的内容存储到DefaultFile-
 * Region 中来达到最佳效率。这将会利用零拷贝特性来进行内容的传输。为此，你可以检查一下，
 * 是否有SslHandler 存在于在ChannelPipeline 中。否则，你可以使用ChunkedNioFile。
 * HttpRequestHandler 将写一个LastHttpContent 来标记响应的结束。如果没有请
 * 求keep-alive ，那么HttpRequestHandler 将会添加一个ChannelFutureListener
 * 到最后一次写出动作的ChannelFuture，并关闭该连接。在这里，你将调用writeAndFlush()
 * 方法以冲刷所有之前写入的消息。
 * 这部分代码代表了聊天服务器的第一个部分，它管理纯粹的HTTP 请求和响应。接下来，我
 * 们将处理传输实际聊天消息的WebSocket 帧。
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 如果请求了WebSocket协议升级，则增加引用计数（调用retain()方法），并将它传递给下一个ChannelInboundHandler
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {

            /**
             * 100 Continue
             * 是这样的一种情况：HTTP客户端程序有一个实体的主体部分要发送给服务器，但希望在发送之前查看下服务器是否会
             * 接受这个实体，所以在发送实体之前先发送了一个携带100
             * Continue的Expect请求首部的请求。服务器在收到这样的请求后，应该用 100 Continue或一条错误码来进行响应。
             */
            if (HttpHeaders.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            // 读取index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");
            HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
            boolean keepAlive = HttpHeaders.isKeepAlive(request);

            // 如果请求了keep-alive，则添加所需要的HTTP头信息
            if (keepAlive) {
                response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            }

            // 将HttpResponse写到客户端
            ctx.write(response);
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            // 写LastHttpContent并冲刷至客户端
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

            // 如果没有请求keep-alive，则在写操作完成后关闭Channel
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}


