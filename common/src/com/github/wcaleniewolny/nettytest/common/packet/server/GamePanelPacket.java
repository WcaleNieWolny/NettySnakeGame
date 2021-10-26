package com.github.wcaleniewolny.nettytest.common.packet.server;

import com.github.wcaleniewolny.nettytest.common.game.Apple;
import com.github.wcaleniewolny.nettytest.common.game.SnakePosition;
import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@NoArgsConstructor
@Slf4j
public class GamePanelPacket implements Packet {


    @Getter
    public int snakeCount;
    @Getter
    @NonNull
    public HashMap<String, SnakePosition> snakes = new HashMap<>();
    @Getter
    public int appleCount;
    @Getter
    @NonNull
    public ArrayList<Apple> apples = new ArrayList<>();

    public GamePanelPacket(@NonNull HashMap<String, SnakePosition> snakes, @NonNull ArrayList<Apple> apples) {
        this.snakes = snakes;
        this.apples = apples;
    }

    public static GamePanelPacket nullPacket() {
        return new GamePanelPacket(new HashMap<>(), new ArrayList<>());
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.snakeCount = in.readVarInt();
        for (int i = 0; i < snakeCount; i++) {
            String name = in.readString();
            int length = in.readVarInt();
            int[] x = new int[length];
            int[] y = new int[length];
            for (int j = 0; j < length; j++) {
                x[j] = in.readVarInt();
                y[j] = in.readVarInt();
            }
            snakes.put(name, new SnakePosition(x, y, length));
        }
        this.appleCount = in.readVarInt();
        System.out.println("SSS");
        for (int i = 0; i < appleCount; i++) {
            int x = in.readVarInt();
            System.out.println(x);
            int y = in.readVarInt();
            System.out.println(y);
            Apple apple = new Apple(x, y);
            System.out.println("APP " + apple.toString());
            apples.add(apple);
        }
        System.out.println("TABLE YES");
    }

    @Override
    public void write(NetOutput out) throws IOException {
        System.out.println("SNAKE SIZZ: " + snakes.size());
        out.writeVarInt(snakes.size());
        snakes.forEach((s, snakePosition) -> {
            try {
                out.writeString(s);
                out.writeVarInt(snakePosition.getLength());
                for (int i = 0; i < snakePosition.length; i++) {
                    out.writeVarInt(snakePosition.getX()[i]);
                    out.writeVarInt(snakePosition.getY()[i]);
                }
            } catch (IOException e) {
                log.error("Can't write packet? {}", e.toString());
            }
        });
        System.out.println("APPLE Z: " + apples.size());
        out.writeVarInt(apples.size());
        apples.forEach(apple -> {
            try {
                out.writeVarInt(apple.getAppleX());
                out.writeVarInt(apple.getAppleY());
            } catch (Exception e) {
                log.error("Can't write packet? {}", e.toString());
            }
        });
        //out.flush();
        System.out.println("TABLE2 YES");
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    public static byte[] trimByte(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static int[] trimInt(int[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }
}
