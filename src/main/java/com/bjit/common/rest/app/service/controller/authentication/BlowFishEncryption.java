/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bjit.common.rest.app.service.controller.authentication;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author BJIT
 */
public final class BlowFishEncryption implements IEncryptionProcessors {

    //private static final Logger BLOWFISH_ENCRYPTION_LOGGER = LogManager.getLogger(BlowFishEncryption.class);
    private static final Logger BLOWFISH_ENCRYPTION_LOGGER = Logger.getLogger(BlowFishEncryption.class);

    public static void main(String[] args) throws Exception {

        /*final BlowFishEncryption blowFish = new BlowFishEncryption();
        final String i_am_okay_and_gone2 = blowFish.encrypt("I am okay and gone");
        System.out.println("Encrypted System : " + i_am_okay_and_gone2);

        final BlowFishEncryption blowFish2 = new BlowFishEncryption();
        blowFish2.setSalt("(*&^%GHYJ");
        final String i_am_okay_and_gone = blowFish2.encrypt("I am okay and gone");
        System.out.println("Encrypted Custom : " + i_am_okay_and_gone);

        final BlowFishEncryption blowFish3 = new BlowFishEncryption();
        blowFish3.setSalt(true);
        final String i_am_okay_and_gone3 = blowFish3.encrypt("I am okay and gone");
        System.out.println("Encrypted Fixed : " + i_am_okay_and_gone3);*/
 /*BlowFishEncryption blowFishEncryption = new BlowFishEncryption();
        blowFishEncryption.setSalt(")(*&%^&457984651FGHJKL:erfd65!$%^!@$*");
        
        String encrypted = blowFishEncryption.encrypt("admin_platform");
        System.out.println("Encrypted : " + encrypted);
        
        String decrypted = blowFishEncryption.decrypt(encrypted);
        System.out.println("Decrypted : " + decrypted);*/
    }

    private Key key;
    private Cipher cipher;
    private String algorithm;
    private String cipherName;

    @Override
    public void setSalt() throws Exception {
        try {
            final KeyGenerator keyGenerator = __init__();
            this.setKey(keyGenerator.generateKey());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public void setSalt(String salt) throws Exception {
        try {
            __init__();
            this.setKeyString(salt);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    @Override
    public void setSalt(boolean isFixed) throws Exception {
        try {
            __init__();
            if (isFixed) {
                this.setKeyString("#$%^&*(*&^TYHUJ");
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    private KeyGenerator __init__() throws NoSuchAlgorithmException, NoSuchPaddingException {
        try {
            this.setAlgorithm("Blowfish");
            this.setCipherName("Blowfish/ECB/PKCS5Padding");

            KeyGenerator keyGenerator = KeyGenerator.getInstance(this.getAlgorithm());
            keyGenerator.init(128);

            // Create a cipher using that key to initialize it
            this.setCipher(Cipher.getInstance(this.getCipherName()));
            return keyGenerator;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    @Override
    public String encrypt(String stringToBeEncrypted) throws Exception {
        try {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Encryption process is running");
            return encryptToHexString(encryptionProcess(stringToBeEncrypted));
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
         finally {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Encryption process has completed");
        }
    }

    private String encryptToHexString(String stringToBeHexed) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        try {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Encryption process is running");
            String reEncrypted = encryptionProcess(stringToBeHexed);
            String encodeHexString = Hex.encodeHexString(reEncrypted.getBytes("UTF-8"));

            return encodeHexString;
        } catch (UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Encryption process has completed");
        }
    }

    private String encryptionProcess(String StringToBeEncrypted) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        try {
            this.getCipher().init(Cipher.ENCRYPT_MODE, this.getKey());

            byte[] plaintext = StringToBeEncrypted.getBytes("UTF8");
            byte[] ciphertext = this.getCipher().doFinal(plaintext);

            String encodeBase64String = Base64.encodeBase64String(ciphertext);

            return encodeBase64String;
        } catch (InvalidKeyException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    @Override
    public String decrypt(String encryptedData) throws Exception {
        try {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Decryption process is running");
            encryptedData = new String(Hex.decodeHex(encryptedData));
            return decryptToRealString(decryptionProcess(encryptedData));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        } finally {
            BLOWFISH_ENCRYPTION_LOGGER.debug("Decryption process has completed");
        }
    }

    public String decryptToRealString(String encryptedData) throws Exception {
        try {
            return decryptionProcess(encryptedData);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    private String decryptionProcess(String encryptedData) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        try {
            final byte[] ciphertext = Base64.decodeBase64(encryptedData);
            this.getCipher().init(Cipher.DECRYPT_MODE, this.getKey());

            byte[] decryptedText = this.getCipher().doFinal(ciphertext);
            return new String(decryptedText, "UTF8");
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException exp) {
            BLOWFISH_ENCRYPTION_LOGGER.error("Error occurred due to : " + exp.getMessage());
            throw exp;
        }
    }

    //region Getters and Setters
    private Key getKey() {
        return key;
    }

    private void setKey(Key key) {
        this.key = key;
    }

    private String getKeyString() {
        return Base64.encodeBase64String(this.getKey().getEncoded());
    }

    private void setKeyString(String key) {
        try {
            final byte[] bytes = Base64.decodeBase64(key);
            SecretKey originalKey = new SecretKeySpec(bytes, 0, bytes.length, this.getAlgorithm());
            this.setKey(originalKey);
        } catch (Exception exp) {
            exp.printStackTrace(System.out);
            throw exp;
        }
    }

    private Cipher getCipher() {
        return cipher;
    }

    private void setCipher(Cipher cipher) {
        this.cipher = cipher;
    }

    private String getAlgorithm() {
        return algorithm;
    }

    private void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    private String getCipherName() {
        return cipherName;
    }

    private void setCipherName(String cipherName) {
        this.cipherName = cipherName;
    }
}
