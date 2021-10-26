package com.github.wcaleniewolny.nettytest.server.game.snakegenerator;

import com.github.wcaleniewolny.nettytest.common.game.Snake;
import io.netty.channel.Channel;

public interface SnakeGenerator {
    Snake generateSnake(int count, String name, Channel channel);
}
