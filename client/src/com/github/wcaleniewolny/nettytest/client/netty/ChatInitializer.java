package com.github.wcaleniewolny.nettytest.client.netty;

import com.github.wcaleniewolny.nettytest.common.TcpPacketCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

public class ChatInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(Short.MAX_VALUE,0,2,0,2));
        pipeline.addLast("framer-prepender", new LengthFieldPrepender(2, false));
        pipeline.addLast("encryption", new CryptoCodecClient());
        pipeline.addLast("codec", new TcpPacketCodec());
        pipeline.addLast("handler", new ClientHandler());
    }
}
