package com.github.wcaleniewolny.nettytest.server.game.snakegenerator;

import com.github.wcaleniewolny.nettytest.common.game.Snake;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import io.netty.channel.Channel;

import java.awt.*;

public record DefaultSnakeGenerator(int GAME_UNITS, int UNIT_SIZE) implements SnakeGenerator {

    @Override
    public Snake generateSnake(int count, String name, Channel channel) {
        switch (count) {
            case 0 -> {
                Snake snake = new Snake(Color.GREEN, new Color(45, 180, 0), new int[GAME_UNITS], new int[GAME_UNITS], UNIT_SIZE, name, channel);
                snake.setDirection(SnakeMovePacket.SnakeDirection.DOWN);
                for (int i = 0; i < snake.bodyParts; i++) {
                    snake.move(true);
                }
                return snake;
            }
            case 1 -> {
                Snake snake = new Snake(Color.BLUE, new Color(0, 108, 180), new int[GAME_UNITS], new int[GAME_UNITS], UNIT_SIZE, name, channel);
                snake.setDirection(SnakeMovePacket.SnakeDirection.RIGHT);
                for (int i = 0; i < 23; i++) {
                    snake.move(true);
                }
                snake.setDirection(SnakeMovePacket.SnakeDirection.DOWN);
                return snake;
            }
            default -> {
                return null;
            }
        }
    }
}
