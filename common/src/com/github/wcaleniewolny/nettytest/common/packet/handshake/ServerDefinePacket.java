package com.github.wcaleniewolny.nettytest.common.packet.handshake;

import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;

import java.io.IOException;

public class ServerDefinePacket implements Packet {
    private byte[] publicKey;
    private byte[] ivOne;
    private byte[] ivTwo;

    @Override
    public void read(NetInput in) throws IOException {
        this.publicKey = in.readBytes(91);
        this.ivOne = in.readBytes(96);
        this.ivTwo = in.readBytes(96);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeBytes(publicKey);
        out.writeBytes(ivOne);
        out.writeBytes(ivTwo);
    }

    @Override
    public boolean isPriority() {
        return false;
    }
    public ServerDefinePacket(){

    }

    public ServerDefinePacket(byte[] publicKey, byte[] ivOne, byte[] ivTwo) {
        this.publicKey = publicKey;
        this.ivOne = ivOne;
        this.ivTwo = ivTwo;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getIvOne() {
        return ivOne;
    }

    public byte[] getIvTwo() {
        return ivTwo;
    }
}
