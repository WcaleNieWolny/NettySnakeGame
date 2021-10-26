package com.github.wcaleniewolny.nettytest.common.events;

import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.channel.Channel;

public interface PacketListener {

    void recivePacket(Packet packet, Channel channel);
}
