package com.github.wcaleniewolny.nettytest.client.netty.events;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.github.wcaleniewolny.nettytest.common.events.EventMenager;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ServerDefinePacket;
import io.netty.channel.Channel;

public class ServerDefineEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        System.out.println("GOT DEFINE PACKET");
        MainClient.encryptionMenager.init(((ServerDefinePacket) packet).getPublicKey(), ((ServerDefinePacket) packet).getIvOne(), ((ServerDefinePacket) packet).getIvTwo());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EventMenager.log.info("ENB???");
        MainClient.encryptionMenager.enable();
        MainClient.getClient().sendPacket(new MsgPacket("ext"));
    }
}
