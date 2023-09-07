package com.linzhi.datasource.app.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

public class DESUtil {

	/**
	 * <pre>
	 * 3-DES加密.
	 * 
	 * @param aSrc 源数据，字节数必须为8的整数倍
	 * @param aDESKey 密钥数据，支持双倍、三倍长的密钥
	 * （若密钥长度是单倍长就是单DES）
	 * @return 加密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] tripleDesEncrypt(byte[] aSrc, byte[] aDESKey) throws Exception {
		byte[] desKey = new byte[24];
		if (16 == aDESKey.length) {// 如果密钥长度为16位，则将密钥数据k1+k2转化为k1+k2+k1
			System.arraycopy(aDESKey, 0, desKey, 0, 16);
			System.arraycopy(aDESKey, 0, desKey, 16, 8);
		} else {
			System.arraycopy(aDESKey, 0, desKey, 0, aDESKey.length);
		}
		SecretKey deskey = new SecretKeySpec(desKey, "DESede"); // 算法：DES,DESede(3DES),Blowfish
		try {
			Cipher c1 = Cipher.getInstance("TripleDES/ECB/NoPadding"); // 填充方式
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(aSrc);
		} catch (Exception e) {
			throw new Exception("3-DES加密失败", e);
		}
	}

	/**
	 * <pre>
	 * 3-DES解密.
	 * 
	 * @param aSrc 已加密的数据
	 * @param aDESKey 密钥数据，支持双倍、三倍长的密钥
	 * （若密钥长度是单倍长就是单DES）
	 * @return 解密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] tripleDESDecrypt(byte[] aSrc, byte[] aDESKey) throws Exception {
		try {
			byte[] desKey = new byte[24];
			if (16 == aDESKey.length) {// 如果密钥长度为16位，则将密钥数据k1+k2转化为k1+k2+k1
				System.arraycopy(aDESKey, 0, desKey, 0, 16);
				System.arraycopy(aDESKey, 0, desKey, 16, 8);
			} else {
				System.arraycopy(aDESKey, 0, desKey, 0, aDESKey.length);
			}
			KeySpec keySpec = new DESedeKeySpec(desKey);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede"); // 算法：DES,DESede(3DES),Blowfish
			Cipher cipher = Cipher.getInstance("TripleDES/ECB/NoPadding");// 填充方式
			SecretKey key = keyFactory.generateSecret(keySpec);
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher.doFinal(aSrc);
		} catch (Exception e) {
			throw new Exception("3-DES加密失败", e);
		}

	}
	/**
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, byte [] key) throws Exception {
		DESKeySpec desKeySpec = new DESKeySpec(key);// 前8个字节做为密钥
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte []bs= cipher.doFinal(data);
		return bs;
	}
	/**
	 * <pre>
	 * 单DES加密.
	 * 
	 * @param aSrc 源数据，字节数必须为8的整数倍
	 * @param aDESKey 密钥数据，必须是单倍长的密钥，即8个字节
	 * @return 加密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] singleDesEncrypt(byte[] aSrc, byte[] aDESKey) throws Exception {
		try {
			SecretKey deskey = new SecretKeySpec(aDESKey, "DES");// 算法：DES,DESede(3DES),Blowfish
			Cipher c1 = Cipher.getInstance("DES/ECB/NoPadding");// 填充方式
			c1.init(Cipher.ENCRYPT_MODE, deskey);
			return c1.doFinal(aSrc);
		} catch (Exception e) {
			throw new Exception("单DES加密失败", e);
		}
	}

	/**
	 * <pre>
	 * 单DES解密.
	 * 
	 * @param aSrc 已加密的数据
	 * @param aDESKey 密钥数据，必须是单倍长的密钥，即8个字节
	 * @return 解密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] singleDesDecrypt(byte[] aSrc, byte[] aDESKey) throws Exception {
		try {
			SecretKey deskey = new SecretKeySpec(aDESKey, "DES");// 算法：DES,DESede(3DES),Blowfish
			Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");// 填充方式
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			return cipher.doFinal(aSrc);
		} catch (Exception e) {
			throw new Exception("单DES解密", e);
		}

	}

	/**
	 * <pre>
	 * 3-DES加密.
	 * @param aSrc 源数据，字节数必须为8的整数倍
	 * @param aDESKey 密钥数据，必须使用三倍长的密钥，即24个字节
	 * @return 加密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] desEncrypt(byte[] srcData, byte[] key) throws Exception {
		byte[] result = new byte[srcData.length];
		try {
			SecretKey secretKey = new SecretKeySpec(key, "DESede");// 算法：DES,DESede(3DES),Blowfish
			Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");// 填充方式
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			result = cipher.doFinal(srcData);

		} catch (Exception e) {
			throw new Exception("DES加密失败", e);
		}
		return result;
	}

	/**
	 * <pre>
	 * 3-DES解密.
	 * 
	 * @param aSrc 已加密的数据
	 * @param aDESKey 密钥数据，必须使用三倍长的密钥，即24个字节
	 * @return 解密结果
	 * @throws KcSecException
	 * </pre>
	 */
	public static byte[] desDecrypt(byte[] encData, byte[] key) throws Exception {
		byte[] result = new byte[encData.length];
		SecretKey secretKey = new SecretKeySpec(key, "DESede");// 算法：DES,DESede(3DES),Blowfish
		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");// 填充方式
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		result = cipher.doFinal(encData);
		return result;
	}
	/**
	 * <pre>
	 * 3-DES解密.
	 * @param encData 密文
	 * @param key 密钥数据，必须使用三倍长的密钥，即24个字节,如果不够右填充0，超过就截取前24位
	 * @return 解密结果
	 * </pre>
	 */
	public static String desDecrypt(String encData, String key) throws Exception{
		key=adaptationKey(key);
		byte [] de=CodeUtil.hexString2Bytes(encData);
		byte [] src=desDecrypt(de,key.getBytes());
		return new String(src);
	}
	/**
	 * 将key适配为24位的长度
	 * @param key
	 * @return
	 */
	public static String adaptationKey(String key){
		int len=24;
		if (key.length()<len) {
			key=StringUtil.stuffString(key, len, false, "0");
		}else{
			key=key.substring(0, len);
		}
		return key;
	}
	
	public static void main(String[] args) throws Exception {
		String data="1234";
		String key="1234567890";
		key=StringUtil.stuffString(key,24,false,"0");
		System.out.println(key);
		byte [] en=desEncrypt(data.getBytes(),key.getBytes());
		String enString=CodeUtil.byte2HexString(en);
		System.out.println(enString);
		byte [] en2=CodeUtil.hexString2Bytes(enString);
		byte [] de=desDecrypt(en2,key.getBytes());
		System.out.println(new String(de));
	}
}
