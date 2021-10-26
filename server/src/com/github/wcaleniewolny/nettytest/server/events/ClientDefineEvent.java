package com.github.wcaleniewolny.nettytest.server.events;

import com.github.wcaleniewolny.nettytest.common.PacketUtils;
import com.github.wcaleniewolny.nettytest.common.crypto.EncryptionMenager;
import com.github.wcaleniewolny.nettytest.common.events.EventMenager;
import com.github.wcaleniewolny.nettytest.common.events.PacketListener;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ClientDefinePacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ServerDefinePacket;
import com.github.wcaleniewolny.nettytest.server.ClientConnection;
import com.github.wcaleniewolny.nettytest.server.MainServer;
import io.netty.channel.Channel;

import java.security.KeyPair;

public class ClientDefineEvent implements PacketListener {
    @Override
    public void recivePacket(Packet packet, Channel channel) {
        EventMenager.log.info("Got client define packet!, {}", ((ClientDefinePacket) packet).getToken());
        if (!MainServer.getConnectionList().containsKey(channel.id().asLongText())) {
            EventMenager.log.info("Connection list do not have that channel id???");
        }
        ClientConnection clientConnection = MainServer.getConnectionList().get(channel.id().asLongText());
        KeyPair keyPair = EncryptionMenager.generateKeyPair();
        String token = ((ClientDefinePacket) packet).getToken();
        byte[] ivOne = EncryptionMenager.generateIv();
        byte[] ivTwo = EncryptionMenager.generateIv();
        byte[] pb = ((ClientDefinePacket) packet).getPublic();
        EncryptionMenager encryptionMenager = new EncryptionMenager(keyPair, channel, token);
        encryptionMenager.init(pb, ivOne, ivTwo);
        clientConnection.setEncryptionMenager(encryptionMenager);
        PacketUtils.sendPacket(new ServerDefinePacket(keyPair.getPublic().getEncoded(), ivOne, ivTwo), channel);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        encryptionMenager.enable();
        EventMenager.log.info("ENB???");
        MainServer.getSnakeGame().initPlayer(((ClientDefinePacket) packet).getToken(), channel);
        //PacketUtils.sendPacket(new MsgPacket("XZ"), channel);
    }
}
