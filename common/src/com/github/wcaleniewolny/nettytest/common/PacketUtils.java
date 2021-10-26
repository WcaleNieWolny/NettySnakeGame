package com.github.wcaleniewolny.nettytest.common;

import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;


public class PacketUtils {
    public static void sendPacket(Packet packet, Channel channel) {
        if (!channel.isActive()) {
            return;
        }
        channel.writeAndFlush(packet).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("SUKCESS PACKET SEND! PACKET: " + packet.getClass().getName());
            } else {
                System.out.println("FUCK!");
            }
        });
    }
}
