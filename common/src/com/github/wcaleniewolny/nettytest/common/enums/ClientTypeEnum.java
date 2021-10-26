package com.github.wcaleniewolny.nettytest.common.enums;

public enum ClientTypeEnum {
    CLIENT(0),
    SPIGOT_SERVER(1);

    public int i;

    private ClientTypeEnum(int i) {
        this.i = i;
    }

    public ClientTypeEnum getFromI(int i) {
        if (i == 0) {
            return CLIENT;
        } else if (i == 1) {
            return SPIGOT_SERVER;
        }
        return null;
    }
}
