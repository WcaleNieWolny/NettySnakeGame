package com.github.wcaleniewolny.nettytest.common;

import com.github.wcaleniewolny.nettytest.common.io.ByteBufNetInput;
import com.github.wcaleniewolny.nettytest.common.io.ByteBufNetOutput;
import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class TcpPacketCodec extends ByteToMessageCodec<Packet> {


    @Override
    public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) {
        System.out.println("ENCODE -> " + buf.readableBytes());
        int initial = buf.writerIndex();
        try {
            NetOutput out = new ByteBufNetOutput(buf);
            Protocol.writePacketId(out, packet.getClass());
            packet.write(out);
        } catch (Throwable t) {
            // Reset writer index to make sure incomplete data is not written out.
            buf.writerIndex(initial);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
        System.out.println("DECODE -> " + buf.readableBytes());
        int initial = buf.readerIndex();
        try {
            NetInput in = new ByteBufNetInput(buf);
            int id = in.readVarInt();
            if (id == -1) {
                buf.readerIndex(initial);
                System.out.println("INVALID PACKET ID! ABORD");
                return;
            }

            if (id == 3) {
                System.out.println("DEBUG GO");
            }
            ;

            Packet packet = Protocol.createIncomingPacket(id);
            if (packet == null) {
                return;
            }
            packet.read(in);
            System.out.println("PCKT " + id + " SUKKK");

            if (packet instanceof GamePanelPacket) {
                System.out.println("JHWEjwoiekfwjolifewk");
            }
            System.out.println("KJKJKJ " + id);
            if (buf.readableBytes() > 0) {
                System.out.println("FKK " + id);
                log.error(String.valueOf(new IllegalStateException("Packet \"" + packet.getClass().getSimpleName() + "\" not fully read.")));
            }
            System.out.println("PCKT@@@ " + id + " SUKKK");
            out.add(packet);
            System.out.println("PKKT$$$" + id + " SUKKK");

        } catch (Throwable t) {
            // Advance buffer to end to make sure remaining data in this com.github.wcaleniewolny.NettyTest.packet is skipped.
            buf.readerIndex(buf.readerIndex() + buf.readableBytes());

        }
    }
}