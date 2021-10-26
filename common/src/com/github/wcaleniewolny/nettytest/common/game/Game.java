package com.github.wcaleniewolny.nettytest.common.game;

public interface Game {
    void stopGame();
    int SCREEN_WIDTH();
    int SCREEN_HEIGHT();
    int UNIT_SIZE();
    int GAME_UNITS();
    int DELAY();
}
