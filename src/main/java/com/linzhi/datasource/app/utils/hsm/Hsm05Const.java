package com.linzhi.datasource.app.utils.hsm;


import com.linzhi.datasource.app.utils.CodeUtil;

public class Hsm05Const {
	public static final int T_SUCCESS = 0;

	public static final int T_FAIL = 1;

	public static final int MAX_RSA_KEY = 50;

	// ************************************
	public static final int REPLY_CODE_LEN = 1;

	public static final int INDEX_LEN = 2;

	public static final int MODULE_LEN = 4;

	public static final int FLAG_LEN = 1;

	public static final int PWD_LEN = 8;

	public static final int DATA_LEN = 4;

	public static final int PUBLIC_LEN = 516;

	public static final int PRIVATE_LEN = 1416;

	/* 错误码 */
	// 0xC2：数据长度错误
	// 0xC1：非法的RSA索引号
	// 0xCB：无RSA密钥
	// 0xC4：RSA口令验证失败
	public static final int ERR_EXP = 0x31; // 处理异常
	public static final int ERR_PASSWD = 0x32; // 密码格式错
	public static final int ERR_DATA_LEN = 0x33; // 数据长度错
	public static final int ERR_KEY_LEN = 0x34; // 密钥长度错

	// *************************************

	/** 数据密钥长度的长度 */
	public static final int KEY_LEN_LEN = 1;
	/** 数据长度的长度 */
	public static final int DATA_LEN_LEN = 2;

	public static final int MAX_MAC_ELEMENT_LEN = 400;

	public static final int MAC_ELEMENT_LEN_LEN = 3;

	public static final int MAC_LEN = 8;

	public static final int PIN_LEN = 8;

	public static final int PAN_LEN = 19; // 8*2

	public static final int PIK_LEN = 24 * 2;

	public static final int MAK_LEN = 24 * 2;

	public static final int CHV_LEN = 8 * 2;

	public static final int PIN_M_LEN = 12;

	public static final int PIN_LEN_LEN = 3;

	public static final int PIN_BLK_LEN = 8 * 2;

	public static final int PWD_BLK_LEN = 24 * 2;

	public static final int EKEY_INVALID_BMK_INDEX = 0x0c;

	public static final int EKEY_LENGTH = 0x32;

	public static final int EPIN_LENGTH = 0x31;

	public static final int MAX_DATA_LEN = 5000;

	public Hsm05Const() {
	}

	/**
	 * 加密机，十进制转2进制，输出指定字节数的byte数组.
	 */
	public static byte[] int2BytePadding(int inNum, int outLen) {
		String s = Integer.toString(inNum, 16);
		int padNum = outLen * 2 - s.length();
		for (int i = 0; i < padNum; i++) {
			s = "0" + s;
		}
		return CodeUtil.hexString2Byte(s);
	}

	/**
	 * 加密机，byte转INT.
	 */
	public static int byte2Int(byte[] data) {
		String hexStr = CodeUtil.byte2HexString(data);
		return Integer.parseInt(hexStr, 16);
	}
}
