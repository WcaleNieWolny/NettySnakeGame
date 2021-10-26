package com.github.wcaleniewolny.nettytest.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

public class CryptoCodecServer extends ByteToMessageCodec<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        ClientConnection clientConnection = MainServer.getConnectionList().get(ctx.channel().id().asLongText());
        if(clientConnection == null){
            out.writeBytes(msg);
            System.out.println("con = null");
            return;
        }
        if(clientConnection.getEncryptionMenager() == null){
            out.writeBytes(msg);
            System.out.println("Mg = null");
            return;
        }
        System.out.println("ENCID");
        byte[] bytes = new byte[msg.readableBytes()];
        msg.readBytes(bytes);
        byte[] msg2 = clientConnection.getEncryptionMenager().encryptPacket(bytes);
        out.writeBytes(msg2);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("DDDEECODE");
        ClientConnection clientConnection = MainServer.getConnectionList().get(ctx.channel().id().asLongText());
        if(clientConnection == null){
            out.add(in.readBytes(in.readableBytes()));
            System.out.println("CON = null");
            return;
        }
        if(clientConnection.getEncryptionMenager() == null){
            out.add(in.readBytes(in.readableBytes()));
            System.out.println("Mg = null");
            return;
        }
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        byte[] msg2 = clientConnection.getEncryptionMenager().decryptPacket(bytes);
        System.out.println("decoding packet!!!");
        // add a fucking byte for out.add
        out.add(Unpooled.copiedBuffer(msg2));
        System.out.println("NOT FUCK!");
    }
}
