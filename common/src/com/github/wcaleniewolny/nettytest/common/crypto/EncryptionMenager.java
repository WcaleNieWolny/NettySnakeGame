package com.github.wcaleniewolny.nettytest.common.crypto;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class EncryptionMenager {
    private boolean encyptionEnabled;
    private final KeyPair mineKey;
    private final Channel channel;
    private final String salt;
    private SecretKey aesKey;
    public static Logger log = LoggerFactory.getLogger("encryption");
    private byte[] firstIV;
    private byte[] secondIV;
    private byte[] lastIV;

    public byte[] encryptPacket(byte[] packetBytes) {
        if (!enabled()) {
            return packetBytes;
        }
        // Get Cipher Instance
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getEncoded(), "AES");

            //increase IV
            increeseIV();

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, firstIV);

            // Initialize Cipher for ENCRYPT_MODE
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Encryption
            byte[] cipherText = cipher.doFinal(packetBytes);
            System.out.println("END2, " + Arrays.toString(cipherText) + " !" + Arrays.toString(packetBytes));

            return cipherText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decryptPacket(byte[] packetBytes) {
        if (!enabled()) {
            return packetBytes;
        }
        increeseIV();
        // Get Cipher Instance
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            // Create SecretKeySpec
            SecretKeySpec keySpec = new SecretKeySpec(aesKey.getEncoded(), "AES");

            // Create GCMParameterSpec
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(16 * 8, firstIV);

            // Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // Perform Decryption
            byte[] b = cipher.doFinal(packetBytes);
            System.out.println("END, " + Arrays.toString(b));
            return b;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public EncryptionMenager(KeyPair mineKey, Channel channel, String salt) {
        this.mineKey = mineKey;
        this.channel = channel;
        this.salt = salt;
    }

    public void init(byte[] pubicKey, byte[] ivOne, byte[] ivTwo) {
        //Gen aes public key!
        byte[] sharedSecret = deriveSharedSecret(pubicKey, (ECPrivateKey) mineKey.getPrivate());
        assert sharedSecret != null;
        String secretString = encodeHexString(sharedSecret);
        this.aesKey = getKeyFromPassword(secretString, salt);
        firstIV = ivOne;
        secondIV = ivTwo;
    }

    public boolean enabled() {
        return encyptionEnabled;
    }

    private void aboardConnection() {
        this.channel.close();
        log.error("Closing connection due to encryption error!");
    }

    private SecretKey getKeyFromPassword(String password, String salt) {

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
            return secret;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            aboardConnection();
            log.warn("Generating aes key caused exeption!", e);
        }
        return null;
    }

    private byte[] deriveSharedSecret(byte[] otherPublicKey, ECPrivateKey yourPrivateKey) {
        try {
            final KeyFactory ec = KeyFactory.getInstance("EC");
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(otherPublicKey);
            final PublicKey publicKey = ec.generatePublic(keySpec);
            KeyAgreement ecdh = KeyAgreement.getInstance("ECDH");
            ecdh.init(yourPrivateKey);
            ecdh.doPhase(publicKey, true);
            return ecdh.generateSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            aboardConnection();
            log.warn("Generating SharedSecret key caused exeption!", e);
        }
        return null;
    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(256); // Set p=256
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    private byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }

    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private void increeseIV() {
        this.lastIV = Arrays.copyOf(firstIV, firstIV.length);
        for (int i = 0; i < firstIV.length; i++) {
            if (!(firstIV[i] + 1 > 127)) {
                firstIV[i]++;
                return;
            }
        }
        log.warn("Making IV bigger was impossible!");
        if (secondIV != null) {
            log.warn("Running IV safety protocol!");
            this.firstIV = secondIV;
            return;
        }
        log.error("Something just go REALY BAD, abording connection!");
        aboardConnection();
    }

    public static byte[] generateIv() {
        byte[] iv = new byte[96];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public PublicKey getPublicKey() {
        return mineKey.getPublic();
    }

    public void enable() {
        this.encyptionEnabled = true;
    }
}
