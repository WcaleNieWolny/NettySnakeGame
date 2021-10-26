package com.github.wcaleniewolny.nettytest.common.packet;

import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;

import java.io.IOException;

public class MsgPacket implements Packet {
    private String msg;

    public String getMsg() {
        return msg;
    }

    public MsgPacket(String msg) {
        this.msg = msg;
    }

    public MsgPacket() {
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.msg = in.readString();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeString(msg);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
