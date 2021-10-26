package com.github.wcaleniewolny.nettytest.server.game;

import com.github.wcaleniewolny.nettytest.common.PacketUtils;
import com.github.wcaleniewolny.nettytest.common.game.Apple;
import com.github.wcaleniewolny.nettytest.common.game.Game;
import com.github.wcaleniewolny.nettytest.common.game.Snake;
import com.github.wcaleniewolny.nettytest.common.game.SnakePosition;
import com.github.wcaleniewolny.nettytest.common.java.SynchRunnable;
import com.github.wcaleniewolny.nettytest.common.packet.MsgPacket;
import com.github.wcaleniewolny.nettytest.common.packet.client.SnakeMovePacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GamePanelPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.GameStartTimerPacket;
import com.github.wcaleniewolny.nettytest.common.packet.server.StopGamePacket;
import com.github.wcaleniewolny.nettytest.server.game.snakegenerator.DefaultSnakeGenerator;
import com.github.wcaleniewolny.nettytest.server.game.snakegenerator.SnakeGenerator;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Slf4j
public class SnakeGame implements Game, ActionListener {
    @NonNull
    private final int SCREEN_WIDTH;
    @NonNull
    private final int SCREEN_HEIGHT;
    @NonNull
    private final int UNIT_SIZE;
    @NonNull
    private final int GAME_UNITS;
    @NonNull
    private final int DELAY;
    @Getter
    private ArrayList<Snake> snakes;
    private ArrayList<Apple> apples;
    private final ArrayList<SynchRunnable> toDo = new ArrayList<>();
    @Getter
    private SnakeGenerator generator;

    public void doSynch(SynchRunnable runnable){
        toDo.add(runnable);
    }

    @Getter
    private final HashMap<String, String> channelNameToSnakeName = new HashMap<>();

    @Getter
    boolean running = false;
    Timer timer;
    Random random;

    public void init(){
        generator = new DefaultSnakeGenerator(GAME_UNITS, UNIT_SIZE);
        random = new Random();
        this.snakes = new ArrayList<>();
        this.apples = new ArrayList<>();
    }

    public void initPlayer(String name, Channel channel){
        Snake snake = this.getGenerator().generateSnake(snakes.size(), name, channel);
        if(snake != null){
            channelNameToSnakeName.put(channel.id().asLongText(), name);
            snakes.add(snake);
        }else {
            System.out.println("CLOSING SNAKE");
            //PacketUtils.sendPacket(new MsgPacket("Server is full!"), channel);
            channel.close();
        }

    }

    public void startGame(){
        snakes.forEach(snake ->{
            PacketUtils.sendPacket(new GameStartTimerPacket(5000), snake.getChannel());
        });
        Timer timer2 = new Timer(100, arg0 -> {
            running = true;
            apples.add(newApple());
            timer = new Timer(DELAY, this);
            timer.start();
        });
        timer2.setRepeats(false); // Only execute once
        timer2.start(); // Go go go!
    }

    public Apple newApple(){
        return new Apple(random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE, random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE);
    }

    private void checkApple(){
        snakes.forEach(snake -> {
            apples.forEach(apple -> {
                if(snake.checkApple(apple)){
                    snake.setApplesEaten(snake.getApplesEaten() + 1);
                    snake.setBodyParts(snake.getBodyParts() + 1);
                    doSynch(() -> apples.remove(apple));
                    doSynch(() -> apples.add(newApple()));
                }
            });
        });
    }

    private void checkCollisions(){
        ArrayList<SnakeRecord> records = new ArrayList<>();
        for (Snake snake : snakes) {
            for (int i = snake.bodyParts; i > 0; i--) {
                if(records.contains(new SnakeRecord(snake.getX()[i], snake.getY()[i]))){
                    removeSnake(snake);
                }else {
                    records.add(new SnakeRecord(snake.getX()[i], snake.getY()[i]));
                }
                if((snake.getX()[0] == snake.getX()[i]) && (snake.getY()[0] == snake.getY()[i])){
                    removeSnake(snake);
                }
            }
            records = null;
            if(snake.getX()[0] < 0){
                removeSnake(snake);
            }
            if(snake.getX()[0] > SCREEN_WIDTH()-UNIT_SIZE){
                removeSnake(snake);
            }
            if(snake.getY()[0] < 0){
                removeSnake(snake);
            }
            if(snake.getY()[0] > SCREEN_HEIGHT()){
                removeSnake(snake);
            }
        }
    }

    @Override
    public void stopGame() {
        for (Snake snake : getSnakes()) {
            PacketUtils.sendPacket(new StopGamePacket(StopGamePacket.SnakeEndEnum.NO_WINNERS, ""), snake.channel);
        }
        System.exit(0);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        toDo.forEach(SynchRunnable::run);
        toDo.clear();
        if(running){
            HashMap<String, SnakePosition> snakes2 = new HashMap<>();
            snakes.forEach(snake ->{
                snake.move(true);
                checkApple();
                snakes2.put(snake.getName(), new SnakePosition(snake.getX(), snake.getY(), snake.getBodyParts()));
            });
            checkCollisions();
            GamePanelPacket packet = new GamePanelPacket(snakes2, apples);
            snakes.forEach(snake -> PacketUtils.sendPacket(packet, snake.channel));
        }else {
            timer.stop();
            timer.setRepeats(false);
            timer.removeActionListener(this);
        }
    }

    public void surrenderSnake(String name){
        String snakeName = channelNameToSnakeName.get(name);
        assert snakeName != null;
        snakes.stream().filter(snake -> snake.getName().equals(snakeName)).forEach(this::removeSnake);
    }
    public void removeSnake(Snake snake){
        doSynch(() -> {
            if(snakes.size() - 1 == 1){
                StopGamePacket packet = new StopGamePacket(StopGamePacket.SnakeEndEnum.ONE_WINNER, snakes.get(0).getName());
                snakes.forEach(snake1 -> PacketUtils.sendPacket(packet, snake1.channel));
                running = false;
                log.info("Snake " + snakes.get(0).getName() + " wins!");
                log.info("Server will exit!");
                System.exit(0);
            }else if (snakes.size() - 1 == 0){
                PacketUtils.sendPacket(new StopGamePacket(StopGamePacket.SnakeEndEnum.NO_WINNERS, "null"), snake.channel);
                log.info("No one wins!");
                log.info("Server will exit!");
                System.exit(0);
            }else {
                snakes.remove(snake);
                PacketUtils.sendPacket(new StopGamePacket(StopGamePacket.SnakeEndEnum.YOU_LOSE, "null"), snake.channel);
            }
        });
    }

}
