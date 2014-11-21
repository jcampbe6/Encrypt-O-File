package com.itec4820.joshua.encrypt_o_file;

import android.os.Build;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Joshua on 11/12/2014.
 */
public class Encryptor {

    private static final String ENCRYPTION_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int SALT_LENGTH = 8;
    private static final int KEY_GEN_ITERATIONS = 1000;
    private static final int OUTPUT_KEY_LENGTH = 256;
    private static final String CHARACTER_ENCODING_SCHEME = "UTF-8";
    private static final String ENCRYPTED_EXTENSION = ".encx";
    private static SecureRandom random = new SecureRandom();

    public static String encrypt(String password, String filePath, String fileName) {
        try {
            byte[] fileData = readFile(filePath);
            byte[] salt = generateSalt();
            SecretKeySpec secretKeySpec = generateSecretKeySpec(password.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            byte[] iv = generateIV(cipher.getBlockSize());
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParamSpec);

            byte[] cipherText = cipher.doFinal(fileData);

            String newFileData = String.format("%s%s%s%s%s%s%s", toBase64(salt), "]",
                    toBase64(iv), "]", toBase64(fileName.getBytes(CHARACTER_ENCODING_SCHEME)), "]", toBase64(cipherText));

            saveData(newFileData.getBytes(CHARACTER_ENCODING_SCHEME), filePath);

            return changeFileExtension(filePath);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String password, String filePath) {
        try {
            byte[] cipherText = readFile(filePath);
            String[] cipherTextFields = new String(cipherText).split("]");
            byte[] salt = fromBase64(cipherTextFields[0]);
            byte[] iv = fromBase64(cipherTextFields[1]);
            String fileName = new String(fromBase64(cipherTextFields[2]));
            byte[] fileData = fromBase64(cipherTextFields[3]);

            SecretKeySpec secretKeySpec = generateSecretKeySpec(password.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            IvParameterSpec ivParamSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParamSpec);

            byte[] plaintext = cipher.doFinal(fileData);

            saveData(plaintext, filePath);

            return restoreOrigFileExtension(filePath, fileName);
        }
        catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] readFile(String filePath) {
        byte[] fileData;
        File file = new File(filePath);
        int size = (int) file.length();
        fileData = new byte[size];

        try {
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            inputStream.read(fileData);
            inputStream.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return fileData;
    }

    private static void saveData(byte[] newFileData, String filePath) {
        File file = new File(filePath);

        try {
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

            outputStream.write(newFileData);
            outputStream.flush();
            outputStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static SecretKeySpec generateSecretKeySpec(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Use compatibility key factory (API level 19 and above)
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1And8bit");
        }
        else {
            // Traditional key factory (API level 18 and below)
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        }

        KeySpec keySpec = new PBEKeySpec(password, salt, KEY_GEN_ITERATIONS, OUTPUT_KEY_LENGTH);
        byte[] keyData = secretKeyFactory.generateSecret(keySpec).getEncoded();

        return new SecretKeySpec(keyData, "AES");
    }

    private static byte[] generateSalt() {
        byte salt[] = new byte[SALT_LENGTH];
        random.nextBytes(salt);

        return salt;
    }

    public static byte[] generateIV(int length) {
        byte[] iv = new byte[length];
        random.nextBytes(iv);

        return iv;
    }

    public static String toBase64(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static byte[] fromBase64(String base64) {
        return Base64.decode(base64, Base64.NO_WRAP);
    }

    private static String changeFileExtension(String filePath) {
        File originalFile = new File(filePath);
        String encryptedFileName = filePath.replaceFirst("[.][^.]+$", "");
        File encryptedFile = new File(encryptedFileName + ENCRYPTED_EXTENSION);

        int fileCount = 1;

        while (encryptedFile.exists()) {
            String newEncryptedFileName = encryptedFileName + "(" + fileCount + ")";
            encryptedFile = new File(newEncryptedFileName + ENCRYPTED_EXTENSION);
            fileCount++;
        }

        originalFile.renameTo(encryptedFile);

        return encryptedFile.getAbsolutePath();
    }

    private static String restoreOrigFileExtension(String filePath, String fileName)
    {
        File encryptedFile = new File(filePath);
        String parentDirectoryPath = encryptedFile.getParent() + "/";
        String originalFileName = parentDirectoryPath + fileName.replaceFirst("[.][^.]+$", "");
        String originalExtension = fileName.substring(fileName.lastIndexOf("."));
        File originalFile = new File(originalFileName + originalExtension);

        int fileCount = 1;

        while (originalFile.exists()) {
            String newFileName = originalFileName + "(" + fileCount + ")";
            originalFile = new File(newFileName + originalExtension);
            fileCount++;
        }

        encryptedFile.renameTo(originalFile);

        return originalFile.getAbsolutePath();
    }
}
