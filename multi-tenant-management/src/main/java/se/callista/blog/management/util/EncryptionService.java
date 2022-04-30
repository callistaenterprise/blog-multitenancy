package se.callista.blog.management.util;

public interface EncryptionService {

    String encrypt(String strToEncrypt, String secret, String salt);

}
