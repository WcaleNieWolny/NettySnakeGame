package com.github.wcaleniewolny.nettytest.server;

import com.github.wcaleniewolny.nettytest.common.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import com.github.wcaleniewolny.nettytest.common.events.EventMenager;
import com.github.wcaleniewolny.nettytest.common.events.MsgEvent;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import com.github.wcaleniewolny.nettytest.common.packet.handshake.ClientDefinePacket;
import com.github.wcaleniewolny.nettytest.server.events.ClientDefineEvent;
import com.github.wcaleniewolny.nettytest.server.events.SnakeMoveEvent;
import com.github.wcaleniewolny.nettytest.server.game.SnakeGame;
import lombok.Getter;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

public class MainServer {
    private static EventMenager menager;
    private static Logger logger;
    private static HashMap<String, ClientConnection> connections = new HashMap<>();
    @Getter
    private final HashMap<String, String> ChannelNameToSnakeName = new HashMap<>();
    @Getter
    private static SnakeGame snakeGame;
    public static void main(String[] args){
        Protocol.register();
        menager = new EventMenager();
        menager.registerEvent(new MsgEvent(), MsgPacket.class);
        menager.registerEvent(new ClientDefineEvent(), ClientDefinePacket.class);
        menager.registerEvent(new SnakeMoveEvent(), SnakeMovePacket.class);
        logger = LoggerFactory.getLogger("SERVER");
        snakeGame = new SnakeGame(600, 600, 25, (600 * 600)/25, 800);
        snakeGame.init();
        new NettyServer(8000).run();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        while (!input.equalsIgnoreCase("stop")) {
            try {
                input = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (input){
                case "s" -> {
                    System.out.println("GO GO GO !");
                    snakeGame.startGame();
                }
                case "d" -> {
                    System.out.println(snakeGame.getSnakes().size());
                }
                case "kill" -> {
                    getSnakeGame().getSnakes().forEach(snake -> snakeGame.removeSnake(snake));
                }
            }
        }
    }
    public static EventMenager getMenager(){
        return menager;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static HashMap<String, ClientConnection> getConnectionList() {
        return connections;
    }
}
