package com.github.wcaleniewolny.nettytest.server.events;

import com.github.wcaleniewolny.nettytest.common.PacketUtils;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.game.SnakePosition;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashMap;

public class MsgEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        System.out.println(((MsgPacket) packet).getMsg());
        if(((MsgPacket) packet).getMsg().contains("ext")){
            System.out.println("yes");
        }
    }
}
