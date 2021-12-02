package com.heal.controlcenter.util;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class AECSBouncyCastleUtil {
    private static final String SECURITY_KEY = "ErmLbkvWzYyKnJYZcX1Rra1dgE2Ud+ligErT8B4KH2A=";
    private static final String PLAIN_IV = "appnomicappsone1";

    public AECSBouncyCastleUtil() {
        setPadding(new PKCS7Padding());
        setKey(Base64.decode(SECURITY_KEY));
        ivArray = PLAIN_IV.getBytes(StandardCharsets.UTF_8);
    }

    private final BlockCipher aesCipher = new AESEngine();
    private PaddedBufferedBlockCipher paddedBufferedBlockCipher;
    private KeyParameter keyParameter;
    private byte[] ivArray;

    public void setPadding(BlockCipherPadding blockCipherPadding) {
        this.paddedBufferedBlockCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(aesCipher),
                blockCipherPadding);
    }

    public void setKey(byte[] key) {
        this.keyParameter = new KeyParameter(key);
    }

    public String encrypt(String input) throws InvalidCipherTextException {
        byte[] pwd = input.getBytes(StandardCharsets.UTF_8);
        return new String(Base64.encode(processing(pwd, true, ivArray)), StandardCharsets.UTF_8);
    }

    public String decrypt(String input) throws InvalidCipherTextException {
        byte[] decryptedPassword = processing(Base64.decode(input), false, ivArray);
        int size = 0;
        while (size < decryptedPassword.length) {
            if (decryptedPassword[size] == 0) {
                break;
            }
            size++;
        }
        return new String(decryptedPassword, 0, size, StandardCharsets.UTF_8);
    }

    private byte[] processing(byte[] input, boolean encrypt, byte[] iv) throws InvalidCipherTextException {

        CipherParameters ivAndKey = new ParametersWithIV(keyParameter, iv);
        paddedBufferedBlockCipher.init(encrypt, ivAndKey);

        byte[] output = new byte[paddedBufferedBlockCipher.getOutputSize(input.length)];
        int bytesWrittenOut = paddedBufferedBlockCipher.processBytes(input, 0, input.length, output, 0);

        paddedBufferedBlockCipher.doFinal(output, bytesWrittenOut);

        return output;
    }

    public List<String> getEncryptedMessage(String message) throws Exception {
        AECSBouncyCastleUtil aecsBouncyCastleUtil = new AECSBouncyCastleUtil();
        List<String> pwdString = new ArrayList<>();
        for (Character pwdChar : message.toCharArray()) {
            pwdString.add(aecsBouncyCastleUtil.encrypt(String.valueOf(pwdChar)));
        }
        return pwdString;
    }

    public String getDecryptedMessage(List<String> messages) throws Exception {
        AECSBouncyCastleUtil aecsBouncyCastleUtil = new AECSBouncyCastleUtil();
        StringBuilder stringBuilder = new StringBuilder();
        for (String message : messages) {
            stringBuilder.append(aecsBouncyCastleUtil.decrypt(message));
        }
        return stringBuilder.toString();
    }
}

