package com.inesv.util;



import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RsaUtil {

    /**
     * RSA algorithm.
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * digital signature algorithm
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * Gets public key.
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * Gets private key.
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA maximum encryption text size.
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA maximum decryption text size.
     */
    private static final int MAX_DECRYPT_BLOCK = 128;



    /** 安全服务提供者 */
    private static final Provider PROVIDER = new BouncyCastleProvider();

     /** 密钥大小 */
    private static final int KEY_SIZE = 2048;


    public static Map<String, Object> generateKeyPair(){
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(1024);//1024
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return keyMap;
    }

    /** 密钥大小 */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * <p>
     * Use the private key to generate digital signatures for the information.
     * </p>
     *
     * @param data
     *            Encrypted data
     * @param publicKey
     *            Private Key (BASE64 encoding)
     * @return Digit signature (BASE64 encoding)
     * @throws Exception
     */
    public static byte[] sign(byte[] data, String publicKey) {
        try {
            //byte[] keyBytes = Base64Utils.decodeFromUrlSafeString(privateKey);
            byte[] keyBytes = Base64.decodeBase64(publicKey);
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory;
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateK);
            signature.update(data);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {

            e.printStackTrace();
        }

        return null;
    }

    /**
     * <p>
     * Verify digit signature.
     * </p>
     *
     * @param data
     *            Encrypted data
     * @param publicKey
     *            Public Key(BASE64 encoding)
     * @param sign
     *            Digit signature
     *
     * @return result from verify
     * @throws Exception
     *
     */
    public static boolean verify(byte[] data, String publicKey, byte[] sign) {
         //= Base64Utils.decodeFromString(publicKey);
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            PublicKey publicK = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicK);
            signature.update(data);

            return signature.verify(sign);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * <P>
     * Decrypted with the private key
     * </p>
     *
     * @param encryptedData
     *            Encrypted data
     * @param privateKey
     *            Private Key (BASE64 encoding)
     * @return decryptedData
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) {
        //byte[] keyBytes = Base64Utils.decodeFromString(privateKey);
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateK);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int inputLen = encryptedData.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;

            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }

            byte[] decryptedData = out.toByteArray();
            out.close();

            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * <p>
     * Encrypt with the public key.
     * </p>
     *
     * @param data
     *            Plain text
     * @param publicKey
     *            Public key(BASE64 encoding)
     * @return encryptedData
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        //byte[] keyBytes = Base64Utils.decodeFromString(publicKey);
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            Key publicK = keyFactory.generatePublic(x509KeySpec);
            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicK);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int inputLen = data.length;
            int offSet = 0;
            byte[] cache;
            int i = 0;

            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }

                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }

            byte[] encryptedData = out.toByteArray();
            out.close();

            return encryptedData;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | IllegalBlockSizeException
                | BadPaddingException | InvalidKeyException | NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
