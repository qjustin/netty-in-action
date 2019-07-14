package com.network.netty.book01.chapter12.demo03;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpAggregatorInitializer extends ChannelInitializer<Channel> {
    private final boolean isClient;
    public HttpAggregatorInitializer(boolean isClient) {
        this.isClient = isClient;
    }
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (isClient) {
            pipeline.addLast("codec", new HttpClientCodec());
        } else {
            pipeline.addLast("codec", new HttpServerCodec());
        }

        // 将最大的消息大小为512KB的HttpObjectAggregator添加到ChannelPipeline
        pipeline.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
    }
}
