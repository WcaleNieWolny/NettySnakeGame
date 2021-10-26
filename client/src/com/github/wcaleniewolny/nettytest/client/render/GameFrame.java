package com.github.wcaleniewolny.nettytest.client.render;

import com.github.wcaleniewolny.nettytest.client.netty.NettyClient;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame(NettyClient client) {
        this.add(new GamePanel(client));
        this.setTitle("Snake");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
