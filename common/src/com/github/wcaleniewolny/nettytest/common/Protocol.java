package com.github.wcaleniewolny.nettytest.common;

import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ClientDefinePacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ServerDefinePacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GameStartTimerPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.StopGamePacket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Protocol {
    private static final Map<Integer, Class<? extends Packet>> incoming = new HashMap<>();
    private static final Map<Class<? extends Packet>, Integer> outgoing = new HashMap<>();

    public static void register() {
        registerPacket(0, MsgPacket.class);
        registerPacket(1, ClientDefinePacket.class);
        registerPacket(2, ServerDefinePacket.class);
        registerPacket(3, GamePanelPacket.class);
        registerPacket(4, GameStartTimerPacket.class);
        registerPacket(5, SnakeMovePacket.class);
        registerPacket(6, StopGamePacket.class);
    }

    public static Packet createIncomingPacket(int id) {
        System.out.println("Crt pck " + id);
        Class<? extends Packet> packet = incoming.get(id);
        if (packet == null) {
            log.error(new IllegalArgumentException("Invalid com.github.wcaleniewolny.NettyTest.packet id: " + id).toString());
        }

        try {
            Constructor<? extends Packet> constructor = packet.getDeclaredConstructor();
            if (!constructor.trySetAccessible()) {
                constructor.setAccessible(true);
            }
            System.out.println("CRT SUK");
            return constructor.newInstance();
        } catch (NoSuchMethodError e) {
            log.error(new IllegalStateException("Packet \"" + id + ", " + packet.getName() + "\" does not have a no-params constructor for instantiation.").toString());
        } catch (Exception e) {
            log.error(new IllegalStateException("Failed to instantiate com.github.wcaleniewolny.NettyTest.packet \"" + id + ", " + packet.getName() + "\".", e).toString());
        }
        return null;
    }

    public static void writePacketId(NetOutput output, Class<? extends Packet> c) {
        try {
            int i = outgoing.get(c);
            output.writeVarInt(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerPacket(Integer integer, Class<? extends Packet> c) {
        incoming.put(integer, c);
        outgoing.put(c, integer);
    }
}
