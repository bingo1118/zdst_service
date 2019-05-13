package com.cloudfire.until.rsautil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Administrator on 2017/3/16.
 */
public class RSAUtil {
    public static final String KEY_ALGORITHM = "RSA";
    /**
     * ò��Ĭ����RSA/NONE/PKCS1Padding��δ��֤
     */
    public static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PRIVATE_KEY = "privateKey";

    /**
     * RSA��Կ���ȱ�����64�ı�������512~65536֮�䡣Ĭ����1024
     */
    public static final int KEY_SIZE = 2048;

    public static final String PLAIN_TEXT = "����GTC1233�ı�";

    public static void main(String[] args) throws UnsupportedEncodingException {
        Map<String, String> keyMap = generateKeyBytes();

        // ����
        PublicKey publicKey = restorePublicKey(keyMap.get(PUBLIC_KEY));

        byte[] encodedText = RSAEncode(publicKey, PLAIN_TEXT.getBytes());
        System.out.println("RSA encoded: " + Base64.encodeBase64String(encodedText));

        
        // ����
        PrivateKey privateKey = restorePrivateKey(keyMap.get(PRIVATE_KEY));
        System.out.println("RSA decoded: " + RSADecode(privateKey, Base64.encodeBase64String(encodedText)));
    }

    /**
     * ������Կ�ԡ�ע��������������Կ��KeyPair��������Կ�Ի�ȡ��˽Կ
     *
     * @return
     */
    public static Map<String, String> generateKeyBytes() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, String> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()));
            keyMap.put(PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded()));
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ԭ��Կ��X509EncodedKeySpec ���ڹ�����Կ�Ĺ淶
     *
     * @param keyBytes
     * @return
     */
    public static PublicKey restorePublicKey(String keyBytes) {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(keyBytes));
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicKey = factory.generatePublic(x509EncodedKeySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ��ԭ˽Կ��PKCS8EncodedKeySpec ���ڹ���˽Կ�Ĺ淶
     *
     * @param keyBytes
     * @return
     */
    public static PrivateKey restorePrivateKey(String keyBytes) {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(keyBytes));
        try {
            KeyFactory factory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateKey = factory.generatePrivate(pkcs8EncodedKeySpec);
            return privateKey;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ���ܣ������ߡ�
     *
     * @param key
     * @param plainText
     * @return
     */
    public static byte[] RSAEncode(PublicKey key, byte[] plainText) {

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * ���ܣ������ߡ�
     *
     * @param key
     * @param encodedText
     * @return
     */
    public static String RSADecode(PrivateKey key, String encodedText) {

        try {
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.decodeBase64(encodedText)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;

    }
}
