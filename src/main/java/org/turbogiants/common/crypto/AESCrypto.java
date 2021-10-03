package org.turbogiants.common.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

/**
 * Date: 06.09.2021
 * Desc: AESCrypto Class manages the Encryption and Decryption of the Packet
 * @author https://github.com/Raitou
 * @version 1.2
 * @since 1.0
 */
public final class AESCrypto {

    private static final int PASS_KEY_ITER = 4096;

    /**
     * Date: --.--.--
     * Desc: Android don't like big keys for some reason reduced the key size by half to fix it
     */
    private static final char[] PASS_KEY = {
            0x4b04, 0x481d, 0x4c77, 0xd5ef, 0x136f, 0x2eff, 0x2d7e, 0xbe6e, 0xc89c, 0xb9b7, 0xe818, 0xc60a, 0xea96, 0x1751, 0xae47, 0x45fa,
            0x744, 0xf274, 0xf4ad, 0x1a0a, 0x2871, 0xf053, 0x539c, 0x3044, 0x2e8e, 0x7201, 0x3b55, 0x4757, 0x79f7, 0xaf9e, 0x7f9b, 0x65ae,
            0x6100, 0xa0fe, 0x89e9, 0x6dcc, 0x3769, 0xd095, 0xf7a8, 0x5daa, 0xb963, 0x4116, 0xee98, 0x8cae, 0xb015, 0x2c23, 0xc81c, 0x9358,
            0x4217, 0x81a, 0x785f, 0x985b, 0x5a2c, 0xd1df, 0xf1a7, 0x99ea, 0x6523, 0x1f1, 0x5ba9, 0x314e, 0x22f2, 0x7111, 0x1d1c, 0x30d0,
            0x238b, 0x14c0, 0x76e1, 0x944d, 0xbf53, 0x9c0a, 0x7c08, 0xa9b3, 0x8fd3, 0x30c9, 0xf58d, 0x1b33, 0x4a91, 0xb5aa, 0x533f, 0xedc9,
            0xd741, 0x8e86, 0x5485, 0x7ab2, 0x3612, 0x251d, 0x6583, 0x59c1, 0xe812, 0xcf4, 0x739a, 0x8747, 0x178b, 0x85fe, 0x22e2, 0x7576,
            0xed18, 0x7484, 0xcf4d, 0x3858, 0xa2c, 0xcf00, 0xcd7b, 0x510f, 0x32d5, 0xa9da, 0x2864, 0x1456, 0x73d9, 0xd502, 0xf1e4, 0x2f9a,
    };
    private static final int SALT_KEY_SIZE = 256;
    private static final byte[] SALT_KEY = {
            0x76, (byte) 0x86, 0x6e, (byte) 0xf9, (byte) 0x84, 0x5, 0x4c, 0x43, (byte) 0x8b, 0x7, 0x3, 0x5f, (byte) 0xc7, (byte) 0xaf, 0x14, (byte) 0x95,
            0xd, 0x28, 0x10, 0x13, 0x21, 0x32, 0x75, 0x6a, (byte) 0xdf, (byte) 0xdf, (byte) 0xa3, 0x4d, 0x6e, (byte) 0xed, 0x22, (byte) 0xe3,
            0x6b, (byte) 0xe7, (byte) 0xb0, 0x4c, 0x38, (byte) 0x92, 0x5c, 0x72, 0x19, (byte) 0x8c, (byte) 0xf5, (byte) 0xd0, (byte) 0x9a, (byte) 0xd2, (byte) 0xe0, 0x4a,
            0x65, (byte) 0x9c, 0x27, (byte) 0x84, (byte) 0xa0, (byte) 0xe8, 0x43, (byte) 0xb9, 0x6a, (byte) 0xff, (byte) 0x88, 0x10, 0x7f, 0x29, (byte) 0xdb, 0x3e,
            0x37, 0x8, 0x11, 0x69, (byte) 0xe0, (byte) 0xcc, 0x4a, (byte) 0xf5, (byte) 0xde, (byte) 0xcd, (byte) 0xcd, 0x57, (byte) 0xe3, 0x29, 0x24, (byte) 0xa4,
            (byte) 0xf5, 0x5b, 0x5a, 0x1f, 0x5d, 0x73, 0x36, (byte) 0x85, 0x7f, 0x53, (byte) 0x85, (byte) 0xe4, (byte) 0x90, (byte) 0xa1, 0x6b, 0x19,
            (byte) 0xf0, (byte) 0xd2, 0x60, 0x20, (byte) 0xdf, (byte) 0x93, (byte) 0xb5, 0x26, (byte) 0xd3, 0x7e, 0x4e, 0x59, 0x18, 0xa, 0x48, 0x57,
            0x50, 0x6b, 0x12, (byte) 0x92, (byte) 0xfe, 0xc, 0x44, (byte) 0xe6, (byte) 0xfa, (byte) 0xdd, 0x2a, (byte) 0x8d, (byte) 0x9c, 0x32, 0xc, 0x3d,
            (byte) 0xee, 0x74, 0x4a, (byte) 0xab, (byte) 0xab, 0xe, 0x26, (byte) 0xc5, 0x75, 0x4f, 0x15, 0x2d, 0x37, (byte) 0xa1, 0x1d, 0x71,
            (byte) 0xc2, 0x7a, (byte) 0xa4, (byte) 0xcc, (byte) 0xb3, 0x2d, (byte) 0xa8, (byte) 0xb8, (byte) 0xbc, 0x7c, 0x7b, 0x42, 0x45, 0x3a, 0x5c, 0x6e,
            (byte) 0xf3, (byte) 0x9e, (byte) 0xcf, 0x27, (byte) 0xd7, (byte) 0xff, 0xb, (byte) 0x91, 0x9, 0x21, 0x54, (byte) 0x9d, 0x28, (byte) 0xeb, (byte) 0xfb, (byte) 0x85,
            (byte) 0xf8, (byte) 0xdf, 0x72, (byte) 0xdd, 0x39, 0xe, 0x6c, 0x4, (byte) 0xd4, 0x4, 0x62, 0x2c, 0x6a, 0x78, (byte) 0xfd, 0xe,
            0x46, (byte) 0xbd, (byte) 0xdf, (byte) 0xaf, (byte) 0xdc, 0x7f, 0x63, (byte) 0xc7, (byte) 0xe2, 0x3f, 0x52, (byte) 0x89, 0x72, 0x6d, (byte) 0xa7, 0x2b,
            0x47, (byte) 0xcb, 0x6b, (byte) 0x92, (byte) 0xd7, (byte) 0xfb, (byte) 0xe5, 0x41, (byte) 0xf6, (byte) 0xe1, 0x9, 0x62, 0x28, (byte) 0xdf, 0x26, (byte) 0x98,
            0x4e, (byte) 0xee, (byte) 0x90, 0x2b, 0x78, (byte) 0xa3, (byte) 0xba, (byte) 0xb5, 0x45, (byte) 0xf6, (byte) 0xd1, (byte) 0xe2, (byte) 0x9a, (byte) 0xda, 0x65, (byte) 0xc4,
            0x33, 0x4f, 0x59, (byte) 0x9f, 0x4b, (byte) 0xc2, (byte) 0xd6, 0x5e, (byte) 0xed, (byte) 0x83, (byte) 0xfd, 0x4c, (byte) 0xc8, 0x47, 0x6d, 0x3e,
    };
    private Cipher cipher;
    private SecretKey sKey;
    private byte[] clientIV; // will be only used by Client
    private byte[] serverIV; // will be only used by Client

    /**
     * Date: --.--.--
     * @author https://github.com/Raitou
     * Desc: Initialization of AESCryptography
     */
    public AESCrypto() {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            this.sKey = generateKey();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Date: --.--.--
     * Desc: Calculation of Sha256 of a packet although this isn't used as PacketReplay and
     * PacketMasking
     * @author https://github.com/Raitou
     * @param byte[] - Byte Input to calculate Checksum
     * @return String - Sha256 Checksum
     */
    public static String getSha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input);
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashtext = new StringBuilder(no.toString(16));
            while (hashtext.length() < 32) {
                hashtext.insert(0, "0");
            }
            return hashtext.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Date: --.--.--
     * Desc: Generation of IV this is used mainly in the server
     * @author https://github.com/Raitou
     * @return byte[] IV
     * @throws GeneralSecurityException
     */
    public static byte[] generateIV() throws GeneralSecurityException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] byteIV = new byte[16];
        random.nextBytes(byteIV);
        return byteIV;
    }

    /**
     * Date: --.--.--
     * Desc: Generation of SecretKey based on the parameters hardcoded in this class
     * @author https://github.com/Raitou
     * @return SecretKey
     * @throws GeneralSecurityException
     */
    private SecretKey generateKey() throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(PASS_KEY, SALT_KEY, PASS_KEY_ITER, SALT_KEY_SIZE);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    /**
     * Date: --.--.--
     * Desc: Encryption of buffer requires IV received from the server or generated
     * @author https://github.com/Raitou
     * @param byte[] Byte to Encrypt
     * @param IvParameterSpec IV
     * @return byte[] Encrypted Byte
     * @throws GeneralSecurityException
     */
    public byte[] encrypt(byte[] byteToEncrypt, IvParameterSpec IV) throws GeneralSecurityException {
        this.cipher.init(Cipher.ENCRYPT_MODE, this.sKey, IV);
        return this.cipher.doFinal(byteToEncrypt);
    }

    /**
     * Date: --.--.--
     * Desc: Encryption of buffer requires IV received from the server or generated
     * @author https://github.com/Raitou
     * @param byte[] Byte to Encrypt
     * @param byte[] IV
     * @return byte[] Encrypted Byte
     * @throws GeneralSecurityException
     */
    public byte[] encrypt(byte[] byteToEncrypt, byte[] IV) throws GeneralSecurityException {
        return encrypt(byteToEncrypt, new IvParameterSpec(IV));
    }

    /**
     * Date: --.--.--
     * Desc: Decryption of buffer requires IV received from the server or generated
     * @author https://github.com/Raitou
     * @param byte[] Byte to Decrypt
     * @param IvParameterSpec IV
     * @return byte[] Decrypted Byte
     * @throws GeneralSecurityException
     */
    public byte[] decrypt(byte[] byteToDecrypt, IvParameterSpec IV) throws GeneralSecurityException {
        this.cipher.init(Cipher.DECRYPT_MODE, this.sKey, IV);
        return this.cipher.doFinal(byteToDecrypt);
    }

    /**
     * Date: --.--.--
     * Desc: Decryption of buffer requires IV received from the server or generated
     * @author https://github.com/Raitou
     * @param byteToDecrypt Byte to Decrypt
     * @param IV Byte IV
     * @return byte[] Decrypted Byte
     * @throws GeneralSecurityException
     */
    public byte[] decrypt(byte[] byteToDecrypt, byte[] IV) throws GeneralSecurityException {
        return decrypt(byteToDecrypt, new IvParameterSpec(IV));
    }

    /**
     * Date: --.--.--
     * Desc: Return Server's IV used by both Client and Server
     * @author https://github.com/Raitou
     * @return  byte[] ServerIV
     */
    public byte[] getServerIV() {
        return serverIV;
    }

    /**
     * Date: --.--.--
     * Desc: Set Server's IV used by both Client and Server
     * @author https://github.com/Raitou
     * @param byte[] ServerIV
     */
    public void setServerIV(byte[] serverIV) {
        this.serverIV = serverIV;
    }

    /**
     * Date: --.--.--
     * Desc: Return's Client's IV used by both Client and Server
     * @author https://github.com/Raitou
     * @return byte[] ClientIV
     */
    public byte[] getClientIV() {
        return clientIV;
    }

    /**
     * Date: --.--.--
     * Desc: Set Client's IV used by both Client and Server
     * @author https://github.com/Raitou
     * @param byte[] ClientIV
     */
    public void setClientIV(byte[] clientIV) {
        this.clientIV = clientIV;
    }


}
