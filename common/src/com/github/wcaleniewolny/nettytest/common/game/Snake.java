package com.github.wcaleniewolny.nettytest.common.game;

import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;

@Data
@Slf4j
public class Snake {
    public final Color HeadColor;
    public final Color BodyColor;
    public int bodyParts = 6;
    public int applesEaten;
    public SnakeMovePacket.SnakeDirection direction = SnakeMovePacket.SnakeDirection.RIGHT;
    public final int[] x;
    public final int[] y;
    private final int UNIT_SIZE;
    public final String name;
    public final Channel channel;
    public void move(boolean body){
        if(body){
            for (int i = bodyParts; i>0 ; i--) {
                x[i] = x[i-1];
                y[i] = y[i-1];
            }
        }
        switch (direction) {
            case UP -> y[0] = y[0] - UNIT_SIZE;
            case DOWN -> y[0] = y[0] + UNIT_SIZE;
            case LEFT -> x[0] = x[0] - UNIT_SIZE;
            case RIGHT -> x[0] = x[0] + UNIT_SIZE;
        }
    }
    public boolean checkApple(Apple apple){
        for(int i = bodyParts;i>0;i--){
            if ((apple.getAppleY() == x[i]) && (apple.getAppleX() == y[i])) {
                return true;
            }
        }
        return false;
    }
    public void changeDirection(SnakeMovePacket.SnakeDirection direction){
        System.out.println("? < M");
        System.out.println(Thread.currentThread().getName());
        switch (direction){
            case UP -> {
                if(this.direction == SnakeMovePacket.SnakeDirection.DOWN){
                    return;
                }
                setDirection(direction);
            }
            case DOWN -> {
                if(this.direction ==  SnakeMovePacket.SnakeDirection.UP){
                    return;
                }
                setDirection(direction);
            }
            case LEFT -> {
                if(this.direction == SnakeMovePacket.SnakeDirection.RIGHT){
                    return;
                }
                setDirection(direction);
            }
            case RIGHT -> {
                if (this.direction == SnakeMovePacket.SnakeDirection.LEFT) {
                    return;
                }
                setDirection(direction);
            }
        }
    }
}
