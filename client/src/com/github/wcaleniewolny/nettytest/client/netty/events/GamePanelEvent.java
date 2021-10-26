package com.github.wcaleniewolny.nettytest.client.netty.events;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GamePanelEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        log.info("New GamePanelPacket!, Snake Size: {}", ((GamePanelPacket) packet).snakeCount);
        ((GamePanelPacket) packet).getSnakes().forEach((s, snakePosition) -> {
            log.info("New snake: {} x: {} y: {}", s, snakePosition.getX(), snakePosition.getY());
        });
        MainClient.getGamePanel().setLastPacket((GamePanelPacket) packet);
        MainClient.getGamePanel().repaint();
        System.out.println("REPAINT!!!");
    }
}
