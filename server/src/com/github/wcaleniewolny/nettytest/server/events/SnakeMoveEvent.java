package com.github.wcaleniewolny.nettytest.server.events;

import com.github.wcaleniewolny.nettytest.common.events.EventMenager;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.game.Snake;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import com.github.wcaleniewolny.nettytest.server.MainServer;
import io.netty.channel.Channel;

public class SnakeMoveEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        SnakeMovePacket p = (SnakeMovePacket) packet;
        EventMenager.log.info("NEW MOVE PACKET!!!");
        String name = MainServer.getSnakeGame().getChannelNameToSnakeName().get(channel.id().asLongText());
//        MainServer.getSnakeGame().getSnakes().
//                stream().
//                filter(snake -> snake.getName().equals(name)).
//                forEach(snake -> snake.changeDirection(p.getDirection()));
        System.out.println("All snakes: " + MainServer.getSnakeGame().getSnakes());
        System.out.println("Name " + name);
        MainServer.getSnakeGame().getSnakes().forEach(snake -> {
            System.out.println("Snake name: " + snake.name);
            if(snake.name.equals(name)){
                System.out.println("Found?");
                MainServer.getSnakeGame().doSynch(() -> {
                    System.out.println("SynC@");
                    for (Snake snake1 : MainServer.getSnakeGame().getSnakes()) {
                        if(snake1.getName().equals(name)){
                            snake1.changeDirection(p.getDirection());
                        }
                        //snake1.setDirection(p.getDirection());
                    }
                });
            }
        });
    }
}
