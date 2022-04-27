package se.callista.blog.service.util;

public interface EncryptionService {
    String decrypt(String strToDecrypt, String secret, String salt);
}
