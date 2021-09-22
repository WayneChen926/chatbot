package com.opendata.chatbot.service;

import org.springframework.stereotype.Service;

@Service
public interface AesECB {
    String aesDecrypt(String encrypt);

    String aesEncrypt(String content);

    String binary(byte[] bytes, int radix);

    String base64Encode(byte[] bytes);

    byte[] base64Decode(String base64Code);

    byte[] aesEncryptToBytes(String content, String encryptKey);

    String aesEncrypt(String content, String encryptKey);

    String aesDecryptByBytes(byte[] encryptBytes, String decryptKey);

    String aesDecrypt(String encryptStr, String decryptKey);
}
