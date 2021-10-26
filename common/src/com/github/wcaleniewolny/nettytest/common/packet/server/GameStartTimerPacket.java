package com.github.wcaleniewolny.nettytest.common.packet.server;

import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
@NoArgsConstructor
public class GameStartTimerPacket implements Packet {

    @NonNull
    public int time;

    @Override
    public void read(NetInput in) throws IOException {
        this.time = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(time);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
}
