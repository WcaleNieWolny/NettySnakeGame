package com.github.wcaleniewolny.nettytest.client.netty.events;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.server.StopGamePacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SnakeWinEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        StopGamePacket packet1 = (StopGamePacket) packet;
        if (packet1.endEnum == StopGamePacket.SnakeEndEnum.YOU_LOSE) {
            log.error("You lose! GG!");
        }
        MainClient.getGamePanel().setGamePacket(packet1);
        MainClient.getGamePanel().setRunning(false);
        MainClient.getGamePanel().repaint();
    }
}
