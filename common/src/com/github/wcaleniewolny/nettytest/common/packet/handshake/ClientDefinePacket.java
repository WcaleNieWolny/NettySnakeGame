package com.github.wcaleniewolny.nettytest.common.packet.handshake;

import com.github.wcaleniewolny.nettytest.common.enums.ClientTypeEnum;
import com.github.wcaleniewolny.nettytest.common.io.NetInput;
import com.github.wcaleniewolny.nettytest.common.io.NetOutput;
import com.github.wcaleniewolny.nettytest.common.packet.Packet;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class ClientDefinePacket implements Packet {
    private ClientTypeEnum clientTypeEnum;
    private String token;
    private byte[] publicKey;

    @Override
    public void read(NetInput in) throws IOException {
        int i = in.readVarInt();
        this.clientTypeEnum = ClientTypeEnum.CLIENT.getFromI(i);
        this.token = in.readString();
        this.publicKey = in.readBytes(91);
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(clientTypeEnum.i);
        out.writeString(token);
        out.writeBytes(publicKey);
    }

    @Override
    public boolean isPriority() {
        return false;
    }

    public ClientDefinePacket(ClientTypeEnum clientTypeEnum, String token, byte[] rsaPublic) {
        this.clientTypeEnum = clientTypeEnum;
        this.token = token;
        this.publicKey = rsaPublic;
    }
    public ClientDefinePacket(){ }

    public ClientTypeEnum getClientTypeEnum() {
        return clientTypeEnum;
    }

    public String getToken() {
        return token;
    }

    public byte[] getPublic() {
        return publicKey;
    }
}
