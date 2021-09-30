package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.service.AesECB;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

@Service
public class AesECBImpl implements AesECB {
    //密钥 (需要前端和后端保持一致)
    @Value("${spring.boot.aes.key}")
    private String key;

    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    /**
     * aes解密
     *
     * @param encrypt 内容
     * @return
     * @throws Exception
     */
    public String aesDecrypt(String encrypt) {
        try {
            return aesDecrypt(encrypt, new String(java.util.Base64.getDecoder().decode(key)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * aes加密
     *
     * @param content
     * @return
     * @throws Exception
     */
    public String aesEncrypt(String content) {
        try {
            return aesEncrypt(content, new String(java.util.Base64.getDecoder().decode(key)));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public String binary(byte[] bytes, int radix) {
        try {
            return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * base 64 encode
     *
     * @param bytes 待编码的byte[]
     * @return 编码后的base 64 code
     */
    public String base64Encode(byte[] bytes) {
        try {
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * base 64 decode
     *
     * @param base64Code 待解码的base 64 code
     * @return 解码后的byte[]
     * @throws Exception
     */
    public byte[] base64Decode(String base64Code) {
        try {
            return StringUtils.hasText(base64Code) ? new Base64().decode(base64Code) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES加密
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的byte[]
     * @throws Exception
     */
    public byte[] aesEncryptToBytes(String content, String encryptKey) {
        try {
            var kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            var cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));

            return cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * AES加密为base 64 code
     *
     * @param content    待加密的内容
     * @param encryptKey 加密密钥
     * @return 加密后的base 64 code
     * @throws Exception
     */
    public String aesEncrypt(String content, String encryptKey) {
        return base64Encode(aesEncryptToBytes(content, encryptKey));
    }

    /**
     * AES解密
     *
     * @param encryptBytes 待解密的byte[]
     * @param decryptKey   解密密钥
     * @return 解密后的String
     * @throws Exception
     */
    public String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) {
        try {
            var kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);

            var cipher = Cipher.getInstance(ALGORITHMSTR);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
            byte[] decryptBytes = cipher.doFinal(encryptBytes);
            return new String(decryptBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    /**
     * 将base 64 code AES解密
     *
     * @param encryptStr 待解密的base 64 code
     * @param decryptKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public String aesDecrypt(String encryptStr, String decryptKey) {
        try {
            return StringUtils.hasText(encryptStr) ? aesDecryptByBytes(base64Decode(encryptStr), decryptKey) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }
}
