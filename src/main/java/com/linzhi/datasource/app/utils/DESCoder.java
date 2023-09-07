/**
 * @(#) DESCoder.java
 * module  : FrameworkSecurity
 * version : 版本管理系统中的文件版本
 * date    : 2009-5-10
 * name    : 马仁配
 */
package com.linzhi.datasource.app.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.SecureRandom;


/**  
 * DES安全编码组件  
 *   
 * <pre>  
 * 支持 DES、DESede(TripleDES,就是3DES)、AES、Blowfish、RC2、RC4(ARCFOUR)  
 * DES                  key size must be equal to 56  
 * DESede(TripleDES)    key size must be equal to 112 or 168  
 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available  
 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive)  
 * RC2                  key size must be between 40 and 1024 bits  
 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits  
 * 具体内容 需要关注 JDK Document http://.../docs/technotes/guides/security/SunProviders.html  
 * </pre>  
 *   
 * @author 梁栋  
 * @version 1.0  
 * @since 1.0  
 */  
public abstract class DESCoder extends Coder {   
    /**  
     * ALGORITHM 算法 <br>  
     * 可替换为以下任意一种算法，同时key值的size相应改变  
     *   
     * <pre>  
     * DES                  key size must be equal to 56  
     * DESede(TripleDES)    key size must be equal to 112 or 168  
     * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available  
     * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive)  
     * RC2                  key size must be between 40 and 1024 bits  
     * RC4(ARCFOUR)         key size must be between 40 and 1024 bits  
     * </pre>  
     */  
    public final static String ALGORITHM = "DES";
    private final static String TRANSFORMATION = "DES/CBC/Nopadding"; 
    private static final byte[] EncryptionSpec = { 0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};   
    /**  
     * 转换密钥<br>  
     *   
     * @param key  
     * @return  
     * @throws Exception  
     */  
    private static Key toKey(byte[] key) throws Exception {   
        DESKeySpec dks = new DESKeySpec(key);   
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);   
        SecretKey secretKey = keyFactory.generateSecret(dks);   
  
        return secretKey;   
    }   
  
    /**  
     * 解密  
     *   
     * @param data  
     * @param key  
     * @return  
     * @throws Exception  
     */  
    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {   
        Key k = toKey(key);   
  
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);   
        IvParameterSpec spec = new IvParameterSpec(EncryptionSpec);   
        cipher.init(Cipher.DECRYPT_MODE, k,spec);   
  
        return cipher.doFinal(data);   
    }   
  
    /**  
     * 加密  
     *   
     * @param data  
     * @param key
     * @return  
     * @throws Exception  
     */  
    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {   
        Key k = toKey(key);   
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);   
        IvParameterSpec spec = new IvParameterSpec(EncryptionSpec);   
        cipher.init(Cipher.ENCRYPT_MODE, k,spec);   
  
        return cipher.doFinal(data);   
    }   
    
 
    /**  
     * 生成密钥  
     *   
     * @return  
     * @throws Exception  
     */  
    public static byte[] initKey() throws Exception {   
        return initKey(null);   
    }   
  
    /**  
     * 生成密钥  
     *   
     * @param seed  
     * @return  
     * @throws Exception  
     */  
    public static byte[] initKey(String seed) throws Exception {   
        SecureRandom secureRandom = null;   
  
        if (seed != null) {   
            secureRandom = new SecureRandom(decryptBASE64(seed));   
        } else {   
            secureRandom = new SecureRandom();   
        }   
  
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM);   
        kg.init(secureRandom);   
  
        SecretKey secretKey = kg.generateKey();   
  
        return secretKey.getEncoded();   
    }   
}  

