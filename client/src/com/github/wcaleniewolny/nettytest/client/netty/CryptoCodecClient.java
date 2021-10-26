package com.github.wcaleniewolny.nettytest.client.netty;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.sun.tools.javac.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class CryptoCodecClient extends ByteToMessageCodec<ByteBuf> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        if(!MainClient.encryptionMenager.enabled()){
            out.writeBytes(msg);
            return;
        }
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        byte[] msg2 = MainClient.encryptionMenager.encryptPacket(bytes);
        out.writeBytes(msg2);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(!MainClient.encryptionMenager.enabled()){
            out.add(in.readBytes(in.readableBytes()));
            return;
        }
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        byte[] msg2 = MainClient.encryptionMenager.decryptPacket(bytes);
        // add a fucking byte for out.add
        out.add(Unpooled.copiedBuffer(msg2));
    }
}
