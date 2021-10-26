package com.github.wcaleniewolny.nettytest.common.events;

import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.channel.Channel;

public class MsgEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        EventMenager.log.info("Msg packet -> data: {}", ((MsgPacket) packet).getMsg());
    }
}
