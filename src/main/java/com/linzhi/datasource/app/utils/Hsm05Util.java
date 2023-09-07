package com.linzhi.datasource.app.utils;

import com.linzhi.datasource.app.utils.exception.SecException;
import com.linzhi.datasource.app.utils.hsm.Hsm05App;
import com.linzhi.datasource.app.utils.hsm.Hsm05Const;
import com.linzhi.datasource.app.utils.hsm.Hsm05Struct;
import com.linzhi.datasource.app.utils.hsm.HsmSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.rsa.RSASignature;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AlgorithmId;

import java.security.MessageDigest;

public class Hsm05Util {

	private final static Logger log = LoggerFactory.getLogger(Hsm05Util.class);

	/* 签名算法（RSA_SHA1） */
	public static final String SIGNATURE_ALGORITHM_RSA_SHA1 = "0";
	/* 签名算法（RSA_MD5） */
	public static final String SIGNATURE_ALGORITHM_RSA_MD5 = "1";

	private static HsmSession hsmSession; // 加密机连接
	private static String hsmPassword = ""; // 加密机访问密码
	private static boolean initialed = false;

	public static boolean initialed() {
		return initialed;
	}

	public static boolean initial(String ip, String pwd) {
		if (!initialed) {
			hsmSession = new HsmSession(2, 1, 6666, 10000, ip, ip);
			int nRet = hsmSession.GetLastError();
			if (nRet != 0) {
				log.error("连接加密机失败:" + ip);
				return false;
			}
			log.info("已连接加密机:" + ip);
			hsmPassword = pwd;
			initialed = true;
		}
		return initialed;
	}

	/**
	 * <pre>
	 * DES加密.
	 * 
	 * &#64;param iData 明文数据
	 * &#64;param workKey 支持8单倍长/16双倍长/24三倍长的数据密钥
	 * &#64;param bmkIndex 密钥索引
	 * &#64;return 密文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] desEncrypt(byte[] iData, byte[] workKey, int bmkIndex) throws Exception {
		// 用BMK对工作密钥解密，得明文工作密钥
		byte[] keyData = desEncDecByBmk(workKey, bmkIndex, workKey.length, true);
		// System.out.println("P: " + CodeUtil.byte2HexString(keyData));

		// 数据长度不为8的倍数，后补空格
		byte[] data = paddingSpace(iData);

		// 用明文密钥加密数据
		switch (workKey.length) {
		case 8:
			return DESUtil.singleDesEncrypt(data, keyData);
		case 16:
		case 24:
			return DESUtil.tripleDesEncrypt(data, workKey);
		default:
			throw new Exception("invalid workkey.");
		}
	}

	/**
	 * <pre>
	 * DES解密.
	 * 
	 * &#64;param iData 密文数据
	 * &#64;param workKey 支持8单倍长/16双倍长/24三倍长的数据密钥
	 * &#64;param bmkIndex 密钥索引
	 * &#64;return 明文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] desDecrypt(byte[] data, byte[] workKey, int bmkIndex) throws Exception {
		// 用BMK对工作密钥解密，得明文工作密钥
		byte[] keyData = desEncDecByBmk(workKey, bmkIndex, workKey.length, true);
		// System.out.println("P: " + CodeUtil.byte2HexString(keyData));

		// 用明文密钥加密数据
		switch (workKey.length) {
		case 8:
			return DESUtil.singleDesDecrypt(data, keyData);
		case 16:
		case 24:
			return DESUtil.tripleDESDecrypt(data, workKey);
		default:
			throw new Exception("invalid workkey.");
		}
	}

	/**
	 * <pre>
	 * DES加密.
	 * 
	 * &#64;param data 明文数据
	 * &#64;param workKey 16字节双倍长的数据密钥 
	 * &#64;param bmkIndex 密钥索引
	 * &#64;return 密文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] des2Encrypt(byte[] data, byte[] workKey, int bmkIndex) throws Exception {
		return des2EncDec(data, workKey, bmkIndex, false);
	}

	/**
	 * <pre>
	 * DES解密.
	 * 
	 * &#64;param data 密文数据
	 * &#64;param workKey 16字节双倍长的数据密钥 
	 * &#64;param bmkIndex 密钥索引
	 * &#64;return 明文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] des2Decrypt(byte[] data, byte[] workKey, int bmkIndex) throws Exception {
		return des2EncDec(data, workKey, bmkIndex, true);
	}

	/**
	 * <pre>
	 * DES加解密.
	 * 
	 * &#64;param iData 数据
	 * &#64;param workKey 数据密钥
	 * &#64;param bmkIndex 密钥索引
	 * &#64;param isDecrypt 是否解密
	 * &#64;return 明文/密文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] des2EncDec(byte[] iData, byte[] workKey, int aBMKIndex, boolean isDecrypt) throws Exception {
		if (!initialed)
			throw new Exception("未初始化HsmUtil.");

		// 数据长度不为8的倍数，后补空格
		byte[] data = paddingSpace(iData);

		int nRet;
		Hsm05Struct hsmStruct = new Hsm05Struct();
		Hsm05Struct.EncDec_Data des = hsmStruct.new EncDec_Data();
		Hsm05Struct.EncDec_Data.EncDec_DataIN hsmIn = null;
		Hsm05Struct.EncDec_Data.EncDec_DataOUT hsmOut = null;
		Hsm05App hApp = new Hsm05App();
		try {
			byte[] flag = "0".getBytes(); // 加密
			if (isDecrypt) {
				flag = "1".getBytes(); // 解密
			}
			byte[] index = Hsm05Const.int2BytePadding(aBMKIndex, Hsm05Const.INDEX_LEN);
			byte[] keyLen = Hsm05Const.int2BytePadding(workKey.length, Hsm05Const.KEY_LEN_LEN);
			byte[] dataLen = Hsm05Const.int2BytePadding(data.length, Hsm05Const.DATA_LEN_LEN);
			hsmIn = des.new EncDec_DataIN(index, keyLen, workKey, flag, dataLen, data);
			hsmOut = des.new EncDec_DataOUT();
			nRet = hApp.EncDec_Data_2Des(hsmSession, hsmIn, hsmOut);
		} catch (Exception e) {
			throw new Exception("HSM Error.", e);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			int dataLen = Hsm05Const.byte2Int(hsmOut.data_len);
			byte[] tReturnData = new byte[dataLen];
			System.arraycopy(hsmOut.data, 0, tReturnData, 0, dataLen);
			return tReturnData;
		} else {
			String replyCode = CodeUtil.byte2HexString(hsmOut.reply_code);
			throw new Exception("desEncDec Error[" + replyCode + "].");
		}
	}

	/**
	 * <pre>
	 * DES加解密.
	 * 
	 * &#64;param iData 数据
	 * &#64;param bmkIndex 密钥索引
	 * &#64;param flag CBC算法标识(支持8单倍长/16双倍长/24三倍长)
	 * &#64;param isDecrypt 是否解密
	 * &#64;return 明文/密文
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] desEncDecByBmk(byte[] iData, int aBMKIndex, int augFlag, boolean isDecrypt)
			throws SecException {
		if (!initialed)
			// throw new Exception("未初始化HsmUtil.");
			throw new SecException("未初始化HsmUtil，请确定已经使用HsmUtil.initial(加密机连接配置文件名)方法初始化。");

		// 数据长度不为8的倍数，后补空格
		byte[] data = paddingSpace(iData);

		int nRet;
		Hsm05Struct hsmStruct = new Hsm05Struct();
		Hsm05Struct.EncDec_Data des = hsmStruct.new EncDec_Data();
		Hsm05Struct.EncDec_Data.EncDec_DataIN hsmIn = null;
		Hsm05Struct.EncDec_Data.EncDec_DataOUT hsmOut = null;
		Hsm05App hApp = new Hsm05App();
		try {
			byte[] flag = "0".getBytes(); // 加密
			if (isDecrypt) {
				flag = "1".getBytes(); // 解密
			}
			byte[] index = Hsm05Const.int2BytePadding(aBMKIndex, Hsm05Const.INDEX_LEN);
			byte[] keyLen = Hsm05Const.int2BytePadding(augFlag, Hsm05Const.KEY_LEN_LEN);
			byte[] dataLen = Hsm05Const.int2BytePadding(data.length, Hsm05Const.DATA_LEN_LEN);
			byte[] workKey = new byte[8];
			hsmIn = des.new EncDec_DataIN(index, keyLen, workKey, flag, dataLen, data);
			hsmOut = des.new EncDec_DataOUT();
			nRet = hApp.EncDec_Data_By_Bmk(hsmSession, hsmIn, hsmOut);
		} catch (Exception e) {
			throw new SecException("desEncDecByBmk运算失败 密钥索引：" + aBMKIndex);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			int dataLen = Hsm05Const.byte2Int(hsmOut.data_len);
			byte[] tReturnData = new byte[dataLen];
			System.arraycopy(hsmOut.data, 0, tReturnData, 0, dataLen);
			return tReturnData;
		} else {
			String replyCode = CodeUtil.byte2HexString(hsmOut.reply_code);
			throw new SecException("desEncDecByBmk Error[" + replyCode + "].");
		}
	}

	/**
	 * 数据长度不为8的倍数，后补空格
	 */
	private static byte[] paddingSpace(byte[] iData) {
		int iLen = (iData.length % 8 == 0) ? 0 : (8 - iData.length % 8);
		byte[] data = new byte[iData.length + iLen];
		System.arraycopy(iData, 0, data, 0, iData.length);
		for (int i = 0; i < iLen; i++) {
			data[iData.length + i] = 0x20;
		}
		return data;
	}

	/**
	 * <pre>
	 * 产生工作密钥.
	 * 
	 * &#64;param aBMKIndex 区域主密钥
	 * &#64;param workKeyLen 工作密钥长度 (支持8/16/24)
	 * &#64;return 被区域主密钥加密工作密钥
	 * &#64;throws Exception
	 * </pre>
	 */
	public static byte[] genWorkKey(int aBMKIndex, int workKeyLen) throws Exception {
		if (!initialed)
			throw new Exception("未初始化HsmUtil.");

		int nRet;
		Hsm05Struct hsmStruct = new Hsm05Struct();
		Hsm05Struct.GenerateWorkKey workKey = hsmStruct.new GenerateWorkKey();
		Hsm05Struct.GenerateWorkKey.GenerateWorkKeyIn hsmIn = null;
		Hsm05Struct.GenerateWorkKey.GenerateWorkKeyOut hsmOut = null;
		Hsm05App hApp = new Hsm05App();
		try {
			byte[] index = Hsm05Const.int2BytePadding(aBMKIndex, Hsm05Const.INDEX_LEN);
			byte[] keyLen = Hsm05Const.int2BytePadding(workKeyLen, Hsm05Const.KEY_LEN_LEN);
			hsmIn = workKey.new GenerateWorkKeyIn(keyLen, index);
			hsmOut = workKey.new GenerateWorkKeyOut();
			nRet = hApp.GenerateWorkKey(hsmSession, hsmIn, hsmOut);
		} catch (Exception e) {
			throw new Exception("HSM Error.", e);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			int keyLen = Hsm05Const.byte2Int(hsmOut.keyLen);
			byte[] tReturnData = new byte[keyLen];
			System.arraycopy(hsmOut.workKey, 0, tReturnData, 0, keyLen);
			log.info("CKV:", CodeUtil.byte2HexString(hsmOut.checkvalue));
			return tReturnData;
		} else {
			String replyCode = CodeUtil.byte2HexString(hsmOut.reply_code);
			throw new Exception("genWorkKey Error[" + replyCode + "].");
		}
	}

	/**
	 * 私钥解密
	 * 
	 * @param decData
	 *            待解密数据
	 * @param rsaKeyIndex
	 *            RSA密钥索引位(2位)
	 */
	public static byte[] rsaPriKeyDecrypt(byte[] decData, String rsaKeyIndex) throws SecException {
		if (!initialed)
			throw new SecException("未初始化HsmUtil.");

		int nRet;

		Hsm05Struct.PrivateKeyEncrypt tprivateKeyEncrypt = null;
		Hsm05Struct.PrivateKeyEncrypt.PrivateKeyEncryptIn tPrivateKeyEncryptIn = null;
		Hsm05Struct.PrivateKeyEncrypt.PrivateKeyEncryptOut tPrivateKeyEncryptOut = null;
		Hsm05App hApp = new Hsm05App();
		try {
			Hsm05Struct hsmStruct = new Hsm05Struct();
			tprivateKeyEncrypt = hsmStruct.new PrivateKeyEncrypt();
			tPrivateKeyEncryptIn = tprivateKeyEncrypt.new PrivateKeyEncryptIn(rsaKeyIndex.getBytes(), // 1.
																										// rsa
																										// ID+
					"0000".getBytes(), // 2. pri key len
					CodeUtil.getNNumberString(4, decData.length), // 3. data len
					"01".getBytes(), // 4. padFlag
					"1".getBytes(), // 5. athFlag
					hsmPassword.getBytes(), // 6. password
					"0".getBytes(), // 7. pri key
					decData // 8. dec data
			);
			tPrivateKeyEncryptOut = tprivateKeyEncrypt.new PrivateKeyEncryptOut();
			nRet = hApp.PrivateKeyDecrypt(hsmSession, tPrivateKeyEncryptIn, tPrivateKeyEncryptOut);

		} catch (Exception e) {
			throw new SecException("HSM Error." + e);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			int tDataLen = Integer.parseInt(new String(tPrivateKeyEncryptOut.dataLen));
			byte[] tReturnData = new byte[tDataLen];
			System.arraycopy(tPrivateKeyEncryptOut.data, 0, tReturnData, 0, tDataLen);
			return tReturnData;

		} else {
			String replyCode = CodeUtil.byte2HexString(tPrivateKeyEncryptOut.reply_code);
			throw new SecException("decByRSAPrivateKey Error[" + replyCode + "].");
		}
	}

	/**
	 * 私钥加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param rsaKeyIndex
	 *            RSA密钥索引位(2位)
	 */
	public static byte[] rsaPriKeyEncrypt(byte[] data, String rsaKeyIndex) throws Exception {
		if (!initialed)
			throw new Exception("未初始化HsmUtil.");

		int nRet;

		Hsm05Struct.PrivateRSAKeyEncrypt tprivateKeyEncrypt = null;
		Hsm05Struct.PrivateRSAKeyEncrypt.PrivateRSAKeyEncryptIn tPrivateKeyEncryptIn = null;
		Hsm05Struct.PrivateRSAKeyEncrypt.PrivateRSAKeyEncryptOut tPrivateKeyEncryptOut = null;
		Hsm05App hApp = new Hsm05App();
		try {
			Hsm05Struct hsmStruct = new Hsm05Struct();
			tprivateKeyEncrypt = hsmStruct.new PrivateRSAKeyEncrypt();
			tPrivateKeyEncryptIn = tprivateKeyEncrypt.new PrivateRSAKeyEncryptIn(rsaKeyIndex.getBytes(), // 1.
																											// rsa
																											// ID+
					CodeUtil.getNNumberString(4, data.length), // 3. data len
					hsmPassword.getBytes(), // 6. password
					data // 8. dec data
			);
			tPrivateKeyEncryptOut = tprivateKeyEncrypt.new PrivateRSAKeyEncryptOut();
			nRet = hApp.PrivateRSAKeyEncrypt(hsmSession, tPrivateKeyEncryptIn, tPrivateKeyEncryptOut);

		} catch (Exception e) {
			throw new Exception("HSM Error.", e);
		}

		if (nRet == Hsm05Const.T_SUCCESS) {
			int tDataLen = Integer.parseInt(new String(tPrivateKeyEncryptOut.dataLen));
			byte[] tReturnData = new byte[tDataLen];
			System.arraycopy(tPrivateKeyEncryptOut.data, 0, tReturnData, 0, tDataLen);
			return tReturnData;

		} else {
			String replyCode = CodeUtil.byte2HexString(tPrivateKeyEncryptOut.reply_code);
			throw new Exception("encByRSAPrivateKey Error[" + replyCode + "].");
		}
	}

	/**
	 * 签名运算，传入index
	 * 
	 * @param aByte
	 *            数据
	 * @param signData
	 *            签名数据
	 * @param aSignFlag
	 *            签名类型（0 SHA-1RSA 1 MD5RSA）
	 * @param aKeyIndex
	 *            私钥索引 2位数字，格式如："01"
	 * @return 签名数据
	 * @throws Exception
	 */
	public static boolean verify(byte[] aByte, byte[] signData, String aSignFlag, int modeLen, byte[] publicKey)
			throws Exception {
		if (!initialed)
			throw new Exception("未初始化HsmUtil.");
		int nRet;
		Hsm05Struct.VerifySign.VerifySignOut verifySignOut;
		Hsm05Struct hsmStruct = new Hsm05Struct();
		Hsm05Struct.VerifySign verifySign = hsmStruct.new VerifySign();

		Hsm05App hApp = new Hsm05App();
		verifySignOut = verifySign.new VerifySignOut();
		try {
			Hsm05Struct.VerifySign.VerifySignIn verifySignIn = verifySign.new VerifySignIn(
					CodeUtil.getNNumberString(4, modeLen), CodeUtil.getNNumberString(4, publicKey.length),
					CodeUtil.getNNumberString(4, signData.length), CodeUtil.getNNumberString(4, aByte.length),
					aSignFlag.getBytes(), publicKey, aByte, signData);

			nRet = hApp.verifySign(hsmSession, verifySignIn, verifySignOut);

		} catch (Exception e) {
			throw new Exception("HSM Error.", e);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			if (verifySignOut.data[0] == (byte) '1') {
				return true;
			} else {
				return false;
			}
		} else {
			String replyCode = CodeUtil.byte2HexString(verifySignOut.reply_code);
			log.error("加密机返回错误代码[" + replyCode + "]");
			throw new Exception("RSA验签失败, 返回码[" + replyCode + "].");
		}
	}

	/**
	 * 签名验证，传入index
	 * 
	 * @param aByte
	 *            待签名数据
	 * @param aSignFlag
	 *            签名类型（0 SHA-1RSA 1 MD5RSA）
	 * @param aKeyIndex
	 *            私钥索引 2位数字，格式如："01"
	 * @return 签名数据
	 * @throws Exception
	 */
	public static byte[] sign(byte[] aByte, String aSignFlag, String aKeyIndex) throws Exception {
		if (!initialed)
			throw new Exception("未初始化HsmUtil.");
		int nRet;
		Hsm05Struct.Sign.SignOut signOut;
		Hsm05Struct hsmStruct = new Hsm05Struct();
		Hsm05Struct.Sign sign = hsmStruct.new Sign();

		Hsm05App hApp = new Hsm05App();
		signOut = sign.new SignOut();
		try {
			Hsm05Struct.Sign.SignIn signIn = sign.new SignIn(aKeyIndex.getBytes(), "0000".getBytes(),
					CodeUtil.getNNumberString(4, aByte.length), aSignFlag.getBytes(), "1".getBytes(),
					hsmPassword.getBytes(), "0000".getBytes(), aByte);
			nRet = hApp.Sign(hsmSession, signIn, signOut);

		} catch (Exception e) {
			throw new Exception("HSM Error.", e);
		}
		if (nRet == Hsm05Const.T_SUCCESS) {
			int tLen = Integer.parseInt(new String(signOut.dataLen));
			byte[] tByte = new byte[tLen];
			System.arraycopy(signOut.data, 0, tByte, 0, tLen);
			return tByte;

		} else {
			String replyCode = CodeUtil.byte2HexString(signOut.reply_code);
			log.error("加密机返回错误代码[" + replyCode + "]");
			throw new Exception("RSA签名失败, 返回码[" + replyCode + "].");
		}
	}

	/**
	 * 签名运算，传入index
	 * 
	 * @param aByte
	 *            待签名数据
	 * @param aSignFlag
	 *            签名类型（0 SHA-1RSA 1 MD5RSA）
	 * @param aKeyIndex
	 *            私钥索引 2位数字，格式如："01"
	 * @return 签名数据
	 * @throws Exception
	 */
	public static byte[] signWithEncode(byte[] aByte, String aSignFlag, String aKeyIndex) throws SecException {
		if (!initialed)
			// throw new Exception("未初始化HsmUtil.");
			throw new SecException("未初始化HsmUtil.");
		int nRet;
		byte[] digest = null;
		MessageDigest md = null;
		ObjectIdentifier digestOID = null;
		try {
			if ("0".equals(aSignFlag)) {
				md = MessageDigest.getInstance("SHA1");
				digestOID = AlgorithmId.SHA_oid;
			} else {
				md = MessageDigest.getInstance("MD5");
				digestOID = AlgorithmId.MD5_oid;
			}
			md.update(aByte);
			digest = md.digest();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecException("signWithEncode Could not digest data" + e);
		}
		try {
			byte[] encoded = RSASignature.encodeSignature(digestOID, digest);
			byte[] encrypted = rsaPriKeyEncrypt(encoded, aKeyIndex);
			return encrypted;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecException("signWithEncode Could not sign data" + e);
		}
	}

	/**
	 * <pre>
	 * 生成RSA公私密钥的头部数据
	 * publicKeyHeader
	 * 512: {48} +{length} +{48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3} +{0}
	 * 1024: {48,-127} +{length} +{48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3} +{-127} +{length} +{0}
	 * 2048: {48,-126} +{length,length} +{48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3} +{-126}  +{length,length} +{0}
	 * privateKeyHeader
	 * {48,-126} +{length,length} { 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126} +{length,length} 
	 * &#64;param aKeyType  1:publickey  2:privatekey
	 * &#64;param aKeyLen   密钥长度（512，1024，2048）
	 * &#64;param aHsmKeyLen 加密机生成的密钥数据的长度
	 * &#64;throws Exception
	 * </pre>
	 */
	private static byte[] getX509KeyHeader(int aKeyType, int aKeyLen, int aHsmKeyLen) throws Exception {
		if (1 == aKeyType) {// publickey

			if (512 == aKeyLen) {
				byte[] bX509PubKeyHeader1 = { 48 };
				byte[] bX509PubKeyHeader2 = new byte[1];
				byte[] bX509PubKeyHeader3 = { 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3 };
				byte[] bX509PubKeyHeader4 = new byte[1];
				byte[] bX509PubKeyHeader5 = { 0 };
				bX509PubKeyHeader4[0] = (byte) (aHsmKeyLen + 1);
				bX509PubKeyHeader2[0] = (byte) (aHsmKeyLen + 1 + 1 + 16);
				byte[] bX509PubKeyHeader = new byte[1 + 1 + 16 + 1 + 1];
				System.arraycopy(bX509PubKeyHeader1, 0, bX509PubKeyHeader, 0, 1);
				System.arraycopy(bX509PubKeyHeader2, 0, bX509PubKeyHeader, 1, 1);
				System.arraycopy(bX509PubKeyHeader3, 0, bX509PubKeyHeader, 2, 16);
				System.arraycopy(bX509PubKeyHeader4, 0, bX509PubKeyHeader, 2 + 16, 1);
				System.arraycopy(bX509PubKeyHeader5, 0, bX509PubKeyHeader, 2 + 16 + 1, 1);
				return bX509PubKeyHeader;
			} else if (1024 == aKeyLen) {
				byte[] bX509PubKeyHeader1 = { 48, -127 };
				byte[] bX509PubKeyHeader2 = new byte[1];
				byte[] bX509PubKeyHeader3 = { 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -127 };
				byte[] bX509PubKeyHeader4 = new byte[1];
				byte[] bX509PubKeyHeader5 = { 0 };
				bX509PubKeyHeader4[0] = (byte) (aHsmKeyLen + 1);

				bX509PubKeyHeader2[0] = (byte) (aHsmKeyLen + 1 + 1 + 17);

				byte[] bX509PubKeyHeader = new byte[2 + 1 + 17 + 1 + 1];
				System.arraycopy(bX509PubKeyHeader1, 0, bX509PubKeyHeader, 0, 2);
				System.arraycopy(bX509PubKeyHeader2, 0, bX509PubKeyHeader, 2, 1);
				System.arraycopy(bX509PubKeyHeader3, 0, bX509PubKeyHeader, 3, 17);
				System.arraycopy(bX509PubKeyHeader4, 0, bX509PubKeyHeader, 3 + 17, 1);
				System.arraycopy(bX509PubKeyHeader5, 0, bX509PubKeyHeader, 3 + 17 + 1, 1);
				return bX509PubKeyHeader;
			} else if (2048 == aKeyLen) {
				byte[] bX509PubKeyHeader1 = { 48, -126 };
				byte[] bX509PubKeyHeader2 = new byte[2];
				byte[] bX509PubKeyHeader3 = { 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 3, -126 };
				byte[] bX509PubKeyHeader4 = new byte[2];
				byte[] bX509PubKeyHeader5 = { 0 };
				bX509PubKeyHeader4[0] = (byte) ((aHsmKeyLen + 1) / 256);
				bX509PubKeyHeader4[1] = (byte) ((aHsmKeyLen + 1) % 256);

				bX509PubKeyHeader2[0] = (byte) ((aHsmKeyLen + 1 + 2 + 17) / 256);
				bX509PubKeyHeader2[1] = (byte) ((aHsmKeyLen + 1 + 2 + 17) % 256);

				byte[] bX509PubKeyHeader = new byte[2 + 2 + 17 + 2 + 1];
				System.arraycopy(bX509PubKeyHeader1, 0, bX509PubKeyHeader, 0, 2);
				System.arraycopy(bX509PubKeyHeader2, 0, bX509PubKeyHeader, 2, 2);
				System.arraycopy(bX509PubKeyHeader3, 0, bX509PubKeyHeader, 4, 17);
				System.arraycopy(bX509PubKeyHeader4, 0, bX509PubKeyHeader, 4 + 17, 2);
				System.arraycopy(bX509PubKeyHeader5, 0, bX509PubKeyHeader, 4 + 17 + 2, 1);
				return bX509PubKeyHeader;
			} else {
				throw new Exception("密钥长度错误，请输入512或1024或2048）");
			}
		} else if (2 == aKeyType) {// privatekey
			byte[] bX509PriKeyHeader1 = { 48, -126 };
			byte[] bX509PriKeyHeader2 = new byte[2];
			byte[] bX509PriKeyHeader3 = { 2, 1, 0, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1, 1, 5, 0, 4, -126 };
			byte[] bX509PriKeyHeader4 = new byte[2];
			bX509PriKeyHeader4[0] = (byte) ((aHsmKeyLen) / 256);
			bX509PriKeyHeader4[1] = (byte) ((aHsmKeyLen) % 256);

			bX509PriKeyHeader2[0] = (byte) ((aHsmKeyLen + 2 + 20) / 256);
			bX509PriKeyHeader2[1] = (byte) ((aHsmKeyLen + 2 + 20) % 256);

			byte[] bX509PriKeyHeader = new byte[2 + 2 + 20 + 2];
			System.arraycopy(bX509PriKeyHeader1, 0, bX509PriKeyHeader, 0, 2);
			System.arraycopy(bX509PriKeyHeader2, 0, bX509PriKeyHeader, 2, 2);
			System.arraycopy(bX509PriKeyHeader3, 0, bX509PriKeyHeader, 4, 20);
			System.arraycopy(bX509PriKeyHeader4, 0, bX509PriKeyHeader, 4 + 20, 2);
			return bX509PriKeyHeader;
		} else {
			throw new Exception("未知的密钥类型，请输入(1或2）");
		}
	}

}
