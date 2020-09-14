package se.callista.blog.tenant_management.util;

public interface EncryptionService {

    String encrypt(String strToEncrypt, String secret, String salt);

}
