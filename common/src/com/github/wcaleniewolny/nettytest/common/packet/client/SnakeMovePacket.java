package com.github.wcaleniewolny.nettytest.common.packet.client;

import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
@NoArgsConstructor
public class SnakeMovePacket implements Packet {

    @Getter
    @NonNull
    SnakeDirection direction;

    @Override
    public void read(NetInput in) throws IOException {
        SnakeDirection direction = SnakeDirection.getFromChar(in.readChar());
        Objects.requireNonNull(direction, "SnakeMovePacket can't be decoded!");
        this.direction = direction;
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeChar(direction.getDirection());
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    public enum SnakeDirection {
        RIGHT('R'),
        LEFT('L'),
        UP('U'),
        DOWN('D');

        char direction;

        SnakeDirection(char direction) {
            this.direction = direction;
        }

        public char getDirection() {
            return direction;
        }

        public static SnakeDirection getFromChar(char c) {
            switch (c) {
                case 'R' -> {
                    return RIGHT;
                }
                case 'L' -> {
                    return LEFT;
                }
                case 'U' -> {
                    return UP;
                }
                case 'D' -> {
                    return DOWN;
                }
            }
            return null;
        }
    }
}
