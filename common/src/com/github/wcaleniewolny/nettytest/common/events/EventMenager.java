package com.github.wcaleniewolny.nettytest.common.events;

import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventMenager {
    public static Logger log = LoggerFactory.getLogger("EVENT");
    HashMap<PacketListener, Class<?>> listenerList = new HashMap<>();
    public void registerEvent(PacketListener listener, Class<? extends Packet> c) {
        listenerList.put(listener, c);
    }
    public void callEvent(Packet packet, Channel channel){
        listenerList.forEach((packetListener, aClass) -> {
            if(packet.getClass().getName().equals(aClass.getName())){
                packetListener.recivePacket(packet, channel);
            }
        });
    }
}
