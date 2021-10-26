package com.github.wcaleniewolny.nettytest.common.game;

import lombok.Data;

@Data
public class SnakePosition {
    public final int[] x;
    public final int[] y;
    public final int length;
}
