package com.github.wcaleniewolny.nettytest.client;

import com.github.wcaleniewolny.nettytest.client.netty.NettyClient;
import com.github.wcaleniewolny.nettytest.client.netty.events.GamePanelEvent;
import com.github.wcaleniewolny.nettytest.client.netty.events.ServerDefineEvent;
import com.github.wcaleniewolny.nettytest.client.netty.events.SnakeWinEvent;
import com.github.wcaleniewolny.nettytest.client.render.GameFrame;
import com.github.wcaleniewolny.nettytest.client.render.GamePanel;
import com.github.wcaleniewolny.nettytest.common.Protocol;
import com.github.wcaleniewolny.nettytest.common.crypto.EncryptionMenager;
import com.github.wcaleniewolny.nettytest.common.enums.ClientTypeEnum;
import com.github.wcaleniewolny.nettytest.common.events.EventMenager;
import com.github.wcaleniewolny.nettytest.common.events.MsgEvent;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ClientDefinePacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ServerDefinePacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.StopGamePacket;
import lombok.Getter;
import lombok.Setter;

import java.security.KeyPair;
import java.util.Random;

public class MainClient {
    private static NettyClient client;
    @Getter
    @Setter
    private static GamePanel gamePanel;

    public static NettyClient getClient() {
        return client;
    }

    public static EncryptionMenager encryptionMenager;
    private static EventMenager menager;
    public static String token;
    @Getter
    public static GameFrame frame;

    private static void setClient(NettyClient client) {
        MainClient.client = client;
    }

    public static void main(String[] args) throws InterruptedException {
        connect();
        frame = new GameFrame(client);
    }

    public static EventMenager getMenager() {
        return menager;
    }

    private static void connect() throws InterruptedException {
        Protocol.register();
        NettyClient c = new NettyClient(8000, "0.0.0.0");
        setClient(c);
        menager = new EventMenager();
        menager.registerEvent(new ServerDefineEvent(), ServerDefinePacket.class);
        menager.registerEvent(new GamePanelEvent(), GamePanelPacket.class);
        menager.registerEvent(new MsgEvent(), MsgPacket.class);
        menager.registerEvent(new SnakeWinEvent(), StopGamePacket.class);
        KeyPair keyPair = EncryptionMenager.generateKeyPair();
        //token = UUID.randomUUID().toString();
        token = String.valueOf(new Random().nextInt(9) + 1);
        encryptionMenager = new EncryptionMenager(keyPair, c.getChannel(), token);
        c.run();
        //c.sendPacket(new ClientDefinePacket(ClientTypeEnum.CLIENT, "WOLNY-TOKEN"));
        System.out.println(encryptionMenager.getPublicKey().getEncoded().length);
        c.sendPacket(new ClientDefinePacket(ClientTypeEnum.CLIENT, token, encryptionMenager.getPublicKey().getEncoded()));
        //new com.github.wcaleniewolny.NettyTest.NettyServer(8000).run();
    }
}
