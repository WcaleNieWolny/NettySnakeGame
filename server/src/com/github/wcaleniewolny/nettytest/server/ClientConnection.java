package com.github.wcaleniewolny.nettytest.server;

import com.github.wcaleniewolny.nettytest.common.crypto.EncryptionMenager;

public class ClientConnection {
    private EncryptionMenager encryptionMenager;

    public EncryptionMenager getEncryptionMenager() {
        return encryptionMenager;
    }

    public void setEncryptionMenager(EncryptionMenager encryptionMenager) {
        this.encryptionMenager = encryptionMenager;
    }

}
