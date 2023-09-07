package com.linzhi.datasource.app.utils.hsm;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hsm05App {
	private final static Logger log = LoggerFactory.getLogger(Hsm05App.class);

	public static final int SECBUF_MAX_SIZE = 6000;

	private byte[] iSecBufferIn = new byte[SECBUF_MAX_SIZE];

	private byte[] iSecBufferOut = new byte[SECBUF_MAX_SIZE];

	// 构造函数：
	public Hsm05App() {
	}

	/**
	 * 删除RSAKey[0xC006].
	 */
	public int DelRSAKey(HsmSession hSession, Hsm05Struct.DelRSAKey.DelRSAKeyIn hsmIn,
			Hsm05Struct.DelRSAKey.DelRSAKeyOut hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x06;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # RSA密钥索引号(2byte), 10进制转16进制
		String temp = new String(hsmIn.rsaIndex);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 访问口令(8byte)
		if (hsmIn.passwd.length < 8) {
			hsmOut.reply_code[0] = Hsm05Const.ERR_PASSWD;
			return Hsm05Const.T_FAIL;
		}
		System.arraycopy(hsmIn.passwd, 0, iSecBufferIn, iSndLen, Hsm05Const.PWD_LEN);
		iSndLen += Hsm05Const.PWD_LEN;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9个字节操作结果 0：成功；1：失败
			i = iSecBufferOut[9] & 0xff;
			return i;

		} catch (Exception e) {
			log.error("DelRSAKey Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * 产生RSA密钥对[0xC031].
	 */
	public int GenerateRsaKey(HsmSession hSession,
			Hsm05Struct.GenerateRsaKey.GenerateRsaKeyIn hsmIn,
			Hsm05Struct.GenerateRsaKey.GenerateRsaKeyOut hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x31;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # 模长(2byte), 10进制转16进制
		String temp = new String(hsmIn.modeLen);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # RSA密钥索引号(2byte), 10进制转16进制
		temp = new String(hsmIn.rsaIndex);
		i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 访问口令(8byte)
		if (hsmIn.passwd.length < 8) {
			hsmOut.reply_code[0] = Hsm05Const.ERR_PASSWD;
			return Hsm05Const.T_FAIL;
		}
		System.arraycopy(hsmIn.passwd, 0, iSecBufferIn, iSndLen, Hsm05Const.PWD_LEN);
		iSndLen += Hsm05Const.PWD_LEN;

		// # 密钥长度(1byte)
		iSecBufferIn[iSndLen++] = (byte) 0x08;

		// # 算法标志(1byte)
		iSecBufferIn[iSndLen++] = (byte) 0x01;

		// # 加密标志(1byte)
		iSecBufferIn[iSndLen++] = (byte) 0x00;

		// # 输出标志(1byte)
		iSecBufferIn[iSndLen++] = (byte) 0x00;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9,10字节表示公钥长度
			// publicLen = secbuf_out[1] * 256 + secbuf_out[2];
			int publicLen = ((iSecBufferOut[9] << 8) & 0xff00) | (iSecBufferOut[10] & 0xff);
			System.arraycopy(String.format("%04d", publicLen).getBytes(), 0, hsmOut.publicKeyLen,
					0, Hsm05Const.DATA_LEN);
			System.arraycopy(iSecBufferOut, 13, hsmOut.publicKey, 0, publicLen);

			// 第11,12字节表示公钥长度
			// privateLen = secbuf_out[3] * 256 + (secbuf_out[4] & 0xff);
			int privateLen = ((iSecBufferOut[11] << 11) & 0xff00) | (iSecBufferOut[12] & 0xff);
			System.arraycopy(String.format("%04d", privateLen).getBytes(), 0, hsmOut.privateKeyLen,
					0, Hsm05Const.DATA_LEN);
			System.arraycopy(iSecBufferOut, 13 + publicLen, hsmOut.privateKey, 0, privateLen);

			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("GenerateRsaKey Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * 私钥解密数据[0xC016].
	 */
	public int PrivateKeyDecrypt(HsmSession hSession,
			Hsm05Struct.PrivateKeyEncrypt.PrivateKeyEncryptIn hsmIn,
			Hsm05Struct.PrivateKeyEncrypt.PrivateKeyEncryptOut hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x16;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # RSA密钥索引号(2byte), 10进制转16进制
		String temp = new String(hsmIn.rsaIndex);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 访问口令(8byte)
		if (hsmIn.passwd.length < 8) {
			hsmOut.reply_code[0] = Hsm05Const.ERR_PASSWD;
			return Hsm05Const.T_FAIL;
		}
		System.arraycopy(hsmIn.passwd, 0, iSecBufferIn, iSndLen, Hsm05Const.PWD_LEN);
		iSndLen += Hsm05Const.PWD_LEN;

		// # 数据长度(2byte), 10进制转16进制
		temp = new String(hsmIn.dataLen);
		i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 数据
		System.arraycopy(hsmIn.encData, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9,10字节表示应答数据长度
			i = ((iSecBufferOut[9] << 8) & 0xff00) | (iSecBufferOut[10] & 0xff);
			if (i > 9999) {
				hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_DATA_LEN;
				return Hsm05Const.T_FAIL;
			}
			System.arraycopy(String.format("%04d", i).getBytes(), 0, hsmOut.dataLen, 0,
					Hsm05Const.DATA_LEN);
			System.arraycopy(iSecBufferOut, 11, hsmOut.data, 0, i);
			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("PrivateKeyDecrypt Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * 签名[0xC018].
	 */
	public int Sign(HsmSession hSession, Hsm05Struct.Sign.SignIn hsmIn,
			Hsm05Struct.Sign.SignOut hsmOut) {
		int iSndLen = 0, nResult = 0;
		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x18;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # RSA密钥索引号(2byte), 10进制转16进制
		String temp = new String(hsmIn.rsaIndex);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 访问口令(8byte)
		if (hsmIn.passwd.length < 8) {
			hsmOut.reply_code[0] = Hsm05Const.ERR_PASSWD;
			return Hsm05Const.T_FAIL;
		}
		System.arraycopy(hsmIn.passwd, 0, iSecBufferIn, iSndLen, Hsm05Const.PWD_LEN);
		iSndLen += Hsm05Const.PWD_LEN;

		// # 算法标志(1byte) 0:SHA－1 1:MD5
		// iSecBufferIn[iSndLen++] = (byte) 0x00;
		i = Integer.parseInt(new String(hsmIn.signFlag));
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 数据长度(2byte), 10进制转16进制
		temp = new String(hsmIn.dataLen);
		i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 数据
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9,10字节表示应答数据长度
			i = ((iSecBufferOut[9] << 8) & 0xff00) | (iSecBufferOut[10] & 0xff);
			if (i > 9999) {
				hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_DATA_LEN;
				return Hsm05Const.T_FAIL;
			}
			System.arraycopy(String.format("%04d", i).getBytes(), 0, hsmOut.dataLen, 0,
					Hsm05Const.DATA_LEN);
			System.arraycopy(iSecBufferOut, 11, hsmOut.data, 0, i);
			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * 私钥加密数据[0xC004].
	 */
	public int PrivateRSAKeyEncrypt(HsmSession hSession,
			Hsm05Struct.PrivateRSAKeyEncrypt.PrivateRSAKeyEncryptIn hsmIn,
			Hsm05Struct.PrivateRSAKeyEncrypt.PrivateRSAKeyEncryptOut hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x04;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # RSA密钥索引号(2byte), 10进制转16进制
		String temp = new String(hsmIn.rsaIndex);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 访问口令(8byte)
		if (hsmIn.passwd.length < 8) {
			hsmOut.reply_code[0] = Hsm05Const.ERR_PASSWD;
			return Hsm05Const.T_FAIL;
		}
		System.arraycopy(hsmIn.passwd, 0, iSecBufferIn, iSndLen, Hsm05Const.PWD_LEN);
		iSndLen += Hsm05Const.PWD_LEN;

		// # 数据长度(2byte), 10进制转16进制
		temp = new String(hsmIn.dataLen);
		i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 数据
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9,10字节表示应答数据长度
			i = ((iSecBufferOut[9] << 8) & 0xff00) | (iSecBufferOut[10] & 0xff);
			if (i > 9999) {
				hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_DATA_LEN;
				return Hsm05Const.T_FAIL;
			}
			System.arraycopy(String.format("%04d", i).getBytes(), 0, hsmOut.dataLen, 0,
					Hsm05Const.DATA_LEN);
			System.arraycopy(iSecBufferOut, 11, hsmOut.data, 0, i);
			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("PrivateRSAKeyEncrypt Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * 私钥加密数据[0xC019].
	 */
	public int verifySign(HsmSession hSession, Hsm05Struct.VerifySign.VerifySignIn hsmIn, Hsm05Struct.VerifySign.VerifySignOut hsmOut) {
		int iSndLen = 0, nResult = 0;
		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0xc0;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x18;
		iSndLen += 2;

		// # 用户保留字(8byte)
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;
		iSecBufferIn[iSndLen++] = (byte) 0x30;

		// # RSA模长(2byte), 10进制转16进制
		String temp = new String(hsmIn.publicKeyLen);
		int i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 数据长度(2byte), 10进制转16进制
		temp = new String(hsmIn.signCodeLen);
		i = Integer.parseInt(temp);
		iSecBufferIn[iSndLen++] = (byte) ((i >> 8) & 0xff);
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # 摘要算法标志(1byte) 0:SHA－1 1:MD5
		i = Integer.parseInt(new String(hsmIn.signFlag));
		iSecBufferIn[iSndLen++] = (byte) (i & 0xff);

		// # RSA公钥
		i = Integer.parseInt(new String(hsmIn.publicKeyLen));
		System.arraycopy(hsmIn.publicKey, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		// # 签名
		i = Integer.parseInt(new String(hsmIn.signDataLen));
		System.arraycopy(hsmIn.sign, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		// # 数据
		i = Integer.parseInt(new String(hsmIn.signCodeLen));
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[9]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第9字节表示应答数据长度
			System.arraycopy(iSecBufferOut, 9, hsmOut.data, 0, 1);
			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * <pre>
	 * 产生工作密钥[0x0404].
	 * 
	 * 输入：
	 * 		#命令类型 1H 0x04
	 * 		#命令 1H 0x04
	 * 		#区域主密钥索引号 2H
	 * 		#加密算法 1H 0：64BIT 3DES 1：128BIT 3DES 2：192BIT 3DES
	 * 		#数据密钥长度 1H 8/16/24
	 * 
	 * 输出：
	 * 		#应答码 1A “A”
	 * 		#数据密钥长度	1H 8/16/24
	 * 		#数据密钥	NH 用区域主密钥加密后数据密钥
	 * 		#数据密钥校验码 8H 数据密钥对全零加密的结果
	 * 		或			
	 * 		#应答码 1A “E”
	 * 		#错误码 1H
	 * </pre>
	 */
	public int GenerateWorkKey(HsmSession hSession,
			Hsm05Struct.GenerateWorkKey.GenerateWorkKeyIn hsmIn,
			Hsm05Struct.GenerateWorkKey.GenerateWorkKeyOut hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0x04;

		// # 命令(1byte)
		iSecBufferIn[1] = (byte) 0x04;
		iSndLen += 2;

		// # BMK索引(2byte)
		System.arraycopy(hsmIn.keyIndex, 0, iSecBufferIn, iSndLen, Hsm05Const.INDEX_LEN);
		iSndLen += Hsm05Const.INDEX_LEN;

		// # 加密算法(1byte). 根据密钥长度获取加密算法
		// String s = CodeUtil.byte2HexString(hsmIn.keyLen);
		int i = Hsm05Const.byte2Int(hsmIn.keyLen);
		byte keylen = (byte) (i & 0xff);
		switch (keylen) {
		case 8:
			iSecBufferIn[iSndLen++] = 0;
			break;
		case 16:
			iSecBufferIn[iSndLen++] = 1;
			break;
		case 24:
			iSecBufferIn[iSndLen++] = 2;
			break;
		default:
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_KEY_LEN;
			return Hsm05Const.T_FAIL;
		}

		// # 密钥长度(1byte)
		iSecBufferIn[iSndLen++] = keylen; // key len

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[1]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第2字节表示密钥长度
			int keyLen = iSecBufferOut[1] & 0xff;
			System.arraycopy(Hsm05Const.int2BytePadding(keyLen, Hsm05Const.KEY_LEN_LEN), 0,
					hsmOut.keyLen, 0, Hsm05Const.KEY_LEN_LEN);
			// 数据密钥
			System.arraycopy(iSecBufferOut, 2, hsmOut.workKey, 0, keyLen);
			// CheckValue 8个字节
			System.arraycopy(iSecBufferOut, 2 + keyLen, hsmOut.checkvalue, 0, 8);

			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * <pre>
	 * 加解密[0x59].
	 * 该指令仅支持密钥长度为16的加解密
	 * 
	 * 输入：
	 * 		#命令类型 1H 0x04
	 * 		#通信主密钥索引号 2H
	 * 		#标志 1H 0：加密   1：解密
	 * 		#密钥 16H 由通信主密钥加密的WK
	 * 		#数据长度 2H 8的倍长
	 * 		#数据 NH
	 * 
	 * 输出：
	 * 		#应答码 1A “A”
	 * 		#数据长度 2H
	 * 		#数据 NH 明文/密文
	 * 		或			
	 * 		#应答码	1A	“E”
	 * 		#错误码	1H
	 * </pre>
	 */
	public int EncDec_Data_3DES(HsmSession hSession, Hsm05Struct.EncDec_Data.EncDec_DataIN hsmIn,
			Hsm05Struct.EncDec_Data.EncDec_DataOUT hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0x59;
		iSndLen += 1;

		// # 通信主密钥索引号
		System.arraycopy(hsmIn.index, 0, iSecBufferIn, iSndLen, Hsm05Const.INDEX_LEN);
		iSndLen += Hsm05Const.INDEX_LEN;

		// # 标志
		iSecBufferIn[iSndLen++] = (byte) (hsmIn.flag[0] - '0');

		// # 密钥
		int keyLen = 16; // Hsm05Const.byte2Int(hsmIn.key_len);
		System.arraycopy(hsmIn.work_key, 0, iSecBufferIn, iSndLen, keyLen);
		iSndLen += keyLen;

		// # 数据长度
		System.arraycopy(hsmIn.data_len, 0, iSecBufferIn, iSndLen, Hsm05Const.DATA_LEN_LEN);
		iSndLen += Hsm05Const.DATA_LEN_LEN;

		// # 数据
		int i = Hsm05Const.byte2Int(hsmIn.data_len);
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[1]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第2,3字节表示数据长度
			int dataLen = ((iSecBufferOut[1] << 8) & 0xff00) | (iSecBufferOut[2] & 0xff);
			System.arraycopy(Hsm05Const.int2BytePadding(dataLen, Hsm05Const.DATA_LEN_LEN), 0,
					hsmOut.data_len, 0, Hsm05Const.DATA_LEN_LEN);
			// 数据
			System.arraycopy(iSecBufferOut, 3, hsmOut.data, 0, dataLen);

			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * <pre>
	 * 加解密[0xD180].
	 * 
	 * 输入：
	 * 		#命令类型 1H 0x04
	 * 		#标志 1H 0：加密   1：解密
	 * 		#数据段数 1H 1-9
	 * 		#通信主密钥索引号 2H
	 * 		#密钥 16H 由通信主密钥加密的WK
	 * 		#数据长度 2H 8的倍长
	 * 		#数据 NH
	 * 
	 * 输出：
	 * 		#应答码 1A “A”
	 * 		#数据长度 2H
	 * 		#数据 NH 明文/密文
	 * 		或			
	 * 		#应答码	1A	“E”
	 * 		#错误码	1H
	 * </pre>
	 */
	public int EncDec_Data_2Des(HsmSession hSession, Hsm05Struct.EncDec_Data.EncDec_DataIN hsmIn,
			Hsm05Struct.EncDec_Data.EncDec_DataOUT hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0x59;
		iSndLen += 1;

		// # 通信主密钥索引号
		System.arraycopy(hsmIn.index, 0, iSecBufferIn, iSndLen, Hsm05Const.INDEX_LEN);
		iSndLen += Hsm05Const.INDEX_LEN;

		// # 标志
		iSecBufferIn[iSndLen++] = (byte) (hsmIn.flag[0] - '0');

		// # 密钥
		int keyLen = Hsm05Const.byte2Int(hsmIn.key_len);
		System.arraycopy(hsmIn.work_key, 0, iSecBufferIn, iSndLen, keyLen);
		iSndLen += keyLen;

		// # 数据长度
		System.arraycopy(hsmIn.data_len, 0, iSecBufferIn, iSndLen, Hsm05Const.DATA_LEN_LEN);
		iSndLen += Hsm05Const.DATA_LEN_LEN;

		// # 数据
		int i = Hsm05Const.byte2Int(hsmIn.data_len);
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			// String s = CodeUtil.byte2HexString(iSecBufferOut);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[1]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第2,3字节表示数据长度
			int dataLen = ((iSecBufferOut[1] << 8) & 0xff00) | (iSecBufferOut[2] & 0xff);
			System.arraycopy(Hsm05Const.int2BytePadding(dataLen, Hsm05Const.DATA_LEN_LEN), 0,
					hsmOut.data_len, 0, Hsm05Const.DATA_LEN_LEN);
			// 数据
			System.arraycopy(iSecBufferOut, 3, hsmOut.data, 0, dataLen);

			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}

	/**
	 * <pre>
	 * 用区域主密钥对数据加/解密[0x72].
	 * 
	 * 输入：
	 * 		#命令类型 1H 0x72
	 * 		#区域主密钥索引 2H
	 * 		#初始向量 8H CBC加密的初始向量
	 * 		#标志 1H 1：加密  0：解密
	 * 		#算法标识 1H
	 * 		#数据 NH
	 * 
	 * 输出：
	 * 		#应答码 1A “A”
	 * 		#数据长度 2H
	 * 		#数据 NH 明文/密文
	 * 		或			
	 * 		#应答码	1A	“E”
	 * 		#错误码	1H
	 * </pre>
	 */
	public int EncDec_Data_By_Bmk(HsmSession hSession, Hsm05Struct.EncDec_Data.EncDec_DataIN hsmIn,
			Hsm05Struct.EncDec_Data.EncDec_DataOUT hsmOut) {
		int iSndLen = 0, nResult = 0;

		// # 命令类型(1byte)
		iSecBufferIn[0] = (byte) 0x72;
		iSndLen += 1;

		// # 区域主密钥索引号
		System.arraycopy(hsmIn.index, 0, iSecBufferIn, iSndLen, Hsm05Const.INDEX_LEN);
		iSndLen += Hsm05Const.INDEX_LEN;

		// # 初始向量 8H CBC加密的初始向量
		byte[] tBlock = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		System.arraycopy(tBlock, 0, iSecBufferIn, iSndLen, 8);
		iSndLen += 8;

		// # 标志
		iSecBufferIn[iSndLen++] = (byte) (hsmIn.flag[0] - '0');

		// # 算法标识 1H
		int i = Hsm05Const.byte2Int(hsmIn.key_len);
		byte keylen = (byte) (i & 0xff);
		switch (keylen) {
		case 8:
			iSecBufferIn[iSndLen++] = (byte) 0x10;
			break;
		case 16:
			iSecBufferIn[iSndLen++] = (byte) 0x00;
			break;
		case 24:
			iSecBufferIn[iSndLen++] = (byte) 0x30;
			break;
		default:
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_KEY_LEN;
			return Hsm05Const.T_FAIL;
		}

		// # 数据长度
		System.arraycopy(hsmIn.data_len, 0, iSecBufferIn, iSndLen, Hsm05Const.DATA_LEN_LEN);
		iSndLen += Hsm05Const.DATA_LEN_LEN;

		// # 数据
		i = Hsm05Const.byte2Int(hsmIn.data_len);
		System.arraycopy(hsmIn.data, 0, iSecBufferIn, iSndLen, i);
		iSndLen += i;

		int iCurrentIndex = hSession.getAvailableIndex();
		try {
			// 发往加密机
			nResult = hSession.SendData(iSecBufferIn, iSndLen, iCurrentIndex);
			if (nResult != 0) {
				hsmOut.reply_code[0] = (byte) nResult;
				return Hsm05Const.T_FAIL;
			}

			// 接收加密机返回数据
			nResult = hSession.RecvData(iSecBufferOut, iCurrentIndex);
			// String s = CodeUtil.byte2HexString(iSecBufferOut);
			if (nResult != 0) {
				// 加密机返回处理失败
				hsmOut.reply_code[0] = iSecBufferOut[1]; // 错误码
				return Hsm05Const.T_FAIL;
			}

			// 解析加密机成功响应数据
			hsmOut.reply_code[0] = 0x30;

			// 第2,3字节表示数据长度
			int dataLen = ((iSecBufferOut[1] << 8) & 0xff00) | (iSecBufferOut[2] & 0xff);
			System.arraycopy(Hsm05Const.int2BytePadding(dataLen, Hsm05Const.DATA_LEN_LEN), 0,
					hsmOut.data_len, 0, Hsm05Const.DATA_LEN_LEN);
			// 数据
			System.arraycopy(iSecBufferOut, 3, hsmOut.data, 0, dataLen);

			return Hsm05Const.T_SUCCESS;

		} catch (Exception e) {
			log.error("HSM Exception.", e);
			hsmOut.reply_code[0] = (byte) Hsm05Const.ERR_EXP;
			return Hsm05Const.T_FAIL;

		} finally {
			hSession.HsmSessionEnd(iCurrentIndex);
		}
	}
}
