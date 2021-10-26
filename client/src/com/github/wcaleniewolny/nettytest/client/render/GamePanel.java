package com.github.wcaleniewolny.nettytest.client.render;

import com.github.wcaleniewolny.nettytest.client.MainClient;
import com.github.wcaleniewolny.nettytest.client.netty.NettyClient;
import com.github.wcaleniewolny.nettytest.common.game.Apple;
import com.github.wcaleniewolny.nettytest.common.game.Game;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.StopGamePacket;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;

public class GamePanel extends JPanel implements Game {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_HEIGHT * SCREEN_WIDTH) / UNIT_SIZE;
    static final int DELAY = 125;
    @Getter
    @Setter
    public GamePanelPacket lastPacket;
    @Setter
    @Getter
    boolean running = true;
    NettyClient client;
    @Setter
    public StopGamePacket gamePacket;


    public GamePanel(NettyClient client) {
        MainClient.setGamePanel(this);
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.client = client;
        this.lastPacket = GamePanelPacket.nullPacket();
        repaint();
        //snakes.add(snake2);

    }

    //SetLastPacket -> Repaint every packet recive

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        System.out.println("REPAINT@!!!");
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            g.setColor(Color.red);
            for (Apple a : lastPacket.getApples()) {
                g.fillOval(a.getAppleX(), a.getAppleY(), UNIT_SIZE, UNIT_SIZE);
            }
            lastPacket.getSnakes().forEach((name, snake) -> {
                for (int i = 0; i < snake.getY().length; i++) {
                    if (i == 0) {
                        g.setColor(Color.GREEN);
                        g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
                        System.out.println("Render head: " + snake.x[i] + " " + snake.y[i] + " " + i);
                    } else {
                        g.setColor(new Color(45, 180, 0));
                        g.fillRect(snake.x[i], snake.y[i], UNIT_SIZE, UNIT_SIZE);
                        System.out.println("Render body: " + snake.x[i] + " " + snake.y[i] + " " + i);
                    }
                }
            });
        } else {
            gameOver(g);
        }
    }

    public void gameOver(Graphics g) {
        Objects.requireNonNull(gamePacket);
        switch (gamePacket.endEnum) {
            case NO_WINNERS -> {
                g.setColor(Color.red);
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 75));
                FontMetrics metrics = g.getFontMetrics(g.getFont());
                g.drawString("Game Over!", (SCREEN_WIDTH - metrics.stringWidth("Game Over!")) / 2, SCREEN_HEIGHT / 2);
            }
            case ONE_WINNER -> {
                g.setColor(Color.red);
                g.setFont(new Font("Comic Sans MS", Font.BOLD, 75));
                FontMetrics metrics = g.getFontMetrics(g.getFont());
                g.drawString(gamePacket.winner + " wins!", (SCREEN_WIDTH - metrics.stringWidth(gamePacket.winner + "wins!")) / 2, SCREEN_HEIGHT / 2);
            }
        }

    }

    @Override
    public void stopGame() {
        running = false;
    }

    @Override
    public int SCREEN_WIDTH() {
        return SCREEN_WIDTH;
    }

    @Override
    public int SCREEN_HEIGHT() {
        return SCREEN_HEIGHT;
    }

    @Override
    public int UNIT_SIZE() {
        return UNIT_SIZE;
    }

    @Override
    public int GAME_UNITS() {
        return GAME_UNITS;
    }

    @Override
    public int DELAY() {
        return DELAY;
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_A:
                    client.sendPacket(new SnakeMovePacket(SnakeMovePacket.SnakeDirection.LEFT));
                    break;
                case KeyEvent.VK_D:
                    client.sendPacket(new SnakeMovePacket(SnakeMovePacket.SnakeDirection.RIGHT));
                    break;
                case KeyEvent.VK_W:
                    client.sendPacket(new SnakeMovePacket(SnakeMovePacket.SnakeDirection.UP));
                    break;
                case KeyEvent.VK_S:
                    client.sendPacket(new SnakeMovePacket(SnakeMovePacket.SnakeDirection.DOWN));
                    break;
            }
        }
    }
}
