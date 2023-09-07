package com.linzhi.datasource.app.utils;



import com.linzhi.datasource.app.utils.exception.SecException;

import java.math.BigInteger;
import java.util.Random;

public class CodeUtil {
	private CodeUtil() {
	}

	/**
	 * 数字字符串转换为bcd,左靠，右补0
	 * 
	 * @param value
	 * @param buf
	 */
	public static void toBcd_left(String value, byte[] buf) {

		if (value.length() % 2 != 0) {
			value = value + "0";
		}

		byte[] tmp = value.getBytes();
		for (int i = 0; i < (value.length() / 2); i++) {
			buf[i] = CodeUtil.uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return;
	}

	public static BigInteger hex2BigInteger(String s) {
		String digits = "0123456789ABCDEF";
		s = s.toUpperCase();
		BigInteger val = BigInteger.valueOf(0);
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			int d = digits.indexOf(c);
			val = val.multiply(BigInteger.valueOf(16)).add(BigInteger.valueOf((long) d));
		}
		return val;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] hexString2Bytes(String src) {
		if (src.length() % 2 != 0) {
			src = src + "0";
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < (src.length() / 2); i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * Byte数组转十六进制字符串字节间不用空格分隔
	 * 
	 * @param b
	 * @return
	 */
	public static String bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * 互联网密码十六进制密文转为byte[]数组
	 * 
	 * @param aByte
	 * @return
	 */
	public static byte[] byte2Byte(byte[] aByte) {
		String tStr = "";
		byte[] tByte = new byte[24];
		int tIndex = 0;
		for (int i = 0; i < aByte.length; i++) {
			tStr = tStr + Character.toString((char) aByte[i]);
		}
		for (int m = 0; m < (tStr.length() - 1);) {
			String temp = tStr.substring(m, m + 2);
			tByte[tIndex] = (byte) Integer.parseInt(temp, 16);
			m = m + 2;
			tIndex = tIndex + 1;
		}
		return tByte;
	}

	/**
	 * 将十六进制表示的字符串转换为字符串代表的byte数组
	 * 
	 * @param aHex
	 *            十六进制字符串
	 * @return byte数组
	 */
	public static byte[] hex2Bytes(byte[] in) {
		byte[] out = new byte[in.length / 2];
		byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		byte[] temp = new byte[in.length];

		for (int i = 0; i < in.length; i++) {
			if (in[i] >= 0x30 && in[i] <= 0x39) {
				temp[i] = (byte) (in[i] - 0x30);
			} else if (in[i] >= 0x41 && in[i] <= 0x46) {
				temp[i] = asciiCode[in[i] - 0x41];
			} else if (in[i] >= 0x61 && in[i] <= 0x66) {
				temp[i] = asciiCode[in[i] - 0x61];
			}
		}

		for (int i = 0; i < in.length / 2; i++) {
			out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
		}

		return out;
	}

	/**
	 * 将十六进制表示的字符串转换为字符串代表的byte数组
	 * 
	 * @param aHex
	 *            十六进制字符串
	 * @return byte数组
	 */
	public static byte[] hex2Byte(byte[] in) {
		byte[] out = new byte[in.length / 2];
		byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };
		byte[] temp = new byte[in.length];

		for (int i = 0; i < in.length; i++) {
			if (in[i] >= 0x30 && in[i] <= 0x39) {
				temp[i] = (byte) (in[i] - 0x30);
			} else if (in[i] >= 0x41 && in[i] <= 0x46) {
				temp[i] = asciiCode[in[i] - 0x41];
			} else if (in[i] >= 0x61 && in[i] <= 0x66) {
				temp[i] = asciiCode[in[i] - 0x61];
			}
		}

		for (int i = 0; i < in.length / 2; i++) {
			out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
		}

		return out;
	}

	/**
	 * Byte数组转十六进制字符串，字节间用空格分隔
	 * 
	 * @param aBytes
	 * @return
	 */
	public static String byte2Hex(byte[] aBytes) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < aBytes.length; n++) {
			stmp = (Integer.toHexString(aBytes[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
			if (n < aBytes.length - 1) {
				hs = hs + " ";
			}
		}

		return hs.toUpperCase();
	}

	/**
	 * Byte数组转十六进制字符串字节间不用空格分隔
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
	 * 0xD9}
	 * 
	 * @param src
	 *            String
	 * @return byte[]
	 */
	public static byte[] hexString2Byte(String src) {
		if (src.length() % 2 != 0) {
			src = src + "0";
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < (src.length() / 2); i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/**
	 * hex转byte.
	 * 
	 * @param bytes
	 * @return
	 */
	public static byte[] hex2Byte(byte[] b, int offset, int len) {
		byte[] d = new byte[len];
		for (int i = 0; i < len * 2; i++) {
			int shift = i % 2 == 1 ? 0 : 4;
			d[i >> 1] |= Character.digit((char) b[offset + i], 16) << shift;
		}
		return d;
	}

	/**
	 * ?.
	 */
	public static int byte2Int(byte[] bRefArr) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = bRefArr.length - 1, x = 0; i >= 0; i--, x++) {
			bLoop = bRefArr[x];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}

	/**
	 * 生成随机的HEX字符串.
	 * 
	 * @param HEX字符串的字节数
	 * @return
	 */
	public static String randomHexStr(int len) {
		Random ran = new Random();
		int num = 0;
		String stmp = "";
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			while (true) {
				num = ran.nextInt(256);
				stmp = Integer.toHexString(num & 0xFF);
				if (stmp.length() == 1) {
					stmp = "0" + stmp;
				}
				strb.append(stmp);
				break;
			}
		}
		return strb.toString();
	}

	/**
	 * 两字节异或
	 * 
	 * @param byte1
	 * @param byte2
	 * @return
	 */
	public static byte byteXOR(byte byte1, byte byte2) {
		return (byte) (byte1 ^ byte2);
	}

	/**
	 * 两字节数组异或
	 * 
	 * @param byte1
	 * @param byte2
	 * @return
	 */
	public static byte[] btyeArrayXOR(byte[] byte1, byte[] byte2) {
		byte[] reBype = new byte[byte1.length];
		for (int i = 0, j = byte1.length; i < j; i++) {
			reBype[i] = byteXOR(byte1[i], byte2[i]);
		}
		return reBype;
	}

	/**
	 * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
	 * 
	 * @param src0
	 *            byte
	 * @param src1
	 *            byte
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 })).byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 })).byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * <pre>
	 * 10进制串转为BCD码.
	 * 
	 * &#64;param asc 10进制字符串
	 * &#64;param appendLeft 是否左补0(即右靠BCD)
	 * &#64;return BCD码
	 * </pre>
	 */
	public static byte[] bcdFromAscii(String asc, boolean appendLeft) {
		int len = asc.length();
		int mod = len % 2;
		if (mod != 0) {
			if (appendLeft) {
				asc = "0" + asc;
			} else {
				asc = asc + "0";
			}
			len = asc.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;
		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}
			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	/**
	 * <pre>
	 * BCD码转为10进制串(阿拉伯数据).
	 * 
	 * &#64;param bytes BCD码
	 * &#64;return 10进制串
	 * </pre>
	 */
	public static String bcd2Ascii(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString();
	}

	/**
	 * pin--->pinBlock，不加主帐号
	 * 
	 * @param aPin
	 * @return
	 * @throws KcSecException
	 */
	public static byte[] pin2PinBlock(String aPin) throws SecException {
		int tTemp = 1;

		int tPinLen = aPin.length();
		if (tPinLen > 12 || tPinLen < 4) {
			throw new SecException(SecException.CS_EXC_CODE_8000 + SecException.CS_EXC_MSG_8000);
		}
		byte[] tByte = new byte[8];
		try {
			if (tPinLen > 9) {
				tByte[0] = (byte) Integer.parseInt(new Integer(tPinLen).toString(), 10);
			} else {
				tByte[0] = (byte) Integer.parseInt(new Integer(tPinLen).toString(), 16);
			}
			if (tPinLen % 2 == 0) {
				for (int i = 0; i < tPinLen;) {
					String a = aPin.substring(i, i + 2);
					tByte[tTemp] = (byte) Integer.parseInt(a, 16);
					if (i == (tPinLen - 2)) {
						if (tTemp < 7) {
							for (int x = (tTemp + 1); x < 8; x++) {
								tByte[x] = (byte) 0xff;
							}
						}
					}
					tTemp++;
					i = i + 2;
				}
			} else {
				for (int i = 0; i < tPinLen - 1;) {
					String a;
					a = aPin.substring(i, i + 2);
					tByte[tTemp] = (byte) Integer.parseInt(a, 16);
					if (i == (tPinLen - 3)) {
						String b = aPin.substring(tPinLen - 1) + "F";
						tByte[tTemp + 1] = (byte) Integer.parseInt(b, 16);
						if ((tTemp + 1) < 7) {
							for (int x = (tTemp + 2); x < 8; x++) {
								tByte[x] = (byte) 0xff;
							}
						}
					}
					tTemp++;
					i = i + 2;
				}
			}
		} catch (Exception e) {
			System.out.println("PIN转PINBLOCK失败" + e);
			throw new SecException(SecException.CS_EXC_CODE_8003 + SecException.CS_EXC_MSG_8003);
		}
		return tByte;
	}

	/**
	 * Pin--->PinBlock,加主帐号
	 * 
	 * @param pin
	 * @param aCardNO
	 * @return
	 * @throws KcSecException
	 */
	public static byte[] pin2PinBlock(String aPin, String aCardNO) throws Exception {
		byte[] tPinByte = pin2PinBlock(aPin);
		if (aCardNO.length() == 11) {
			aCardNO = "00" + aCardNO;
		} else if (aCardNO.length() == 12) {
			aCardNO = "0" + aCardNO;
		}
		byte[] tPanByte = formatPan(aCardNO);
		byte[] tByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			tByte[i] = (byte) (tPinByte[i] ^ tPanByte[i]);
		}
		return tByte;
	}

	/**
	 * Pin--->PinBlock,加主帐号
	 * 
	 * @param pin
	 * @param aCardNO
	 * @return
	 * @throws SecException
	 */
	public static byte[] pin2PinBlockWithCardNO(String aPin, String aCardNO) throws SecException {
		byte[] tPinByte = CodeUtil.pin2PinBlock(aPin);
		if (aCardNO.length() == 11) {
			aCardNO = "00" + aCardNO;
		} else if (aCardNO.length() == 12) {
			aCardNO = "0" + aCardNO;
		}
		byte[] tPanByte = CodeUtil.formatPan(aCardNO);
		byte[] tByte = new byte[8];
		for (int i = 0; i < 8; i++) {
			tByte[i] = (byte) (tPinByte[i] ^ tPanByte[i]);
		}
		return tByte;
	}

	/**
	 * 
	 * @param aPan
	 * @return
	 * @throws Exception
	 */
	private static byte[] formatPan(String aPan) throws SecException {
		int tPanLen = aPan.length();
		byte[] tByte = new byte[8];
		if (tPanLen < 13 || tPanLen > 19) {
			throw new SecException(SecException.CS_EXC_CODE_8000 + SecException.CS_EXC_MSG_8000);
		}
		int temp = tPanLen - 13;
		try {
			tByte[0] = (byte) 0x00;
			tByte[1] = (byte) 0x00;
			for (int i = 2; i < 8; i++) {
				String a = aPan.substring(temp, temp + 2);
				tByte[i] = (byte) Integer.parseInt(a, 16);
				temp = temp + 2;
			}
		} catch (Exception e) {
			System.out.println("PIN转PINBLOCK失败" + e);
			throw new SecException(SecException.CS_EXC_CODE_8003 + SecException.CS_EXC_MSG_8003);
		}
		return tByte;
	}

	/**
	 * 返回CUPS标准的固定长度数字格式（右对齐前补零）
	 * 
	 * @param aLen
	 *            长度
	 * @param aString
	 * @return
	 */
	public static byte[] getNNumberString(int aLen, int aValue) {
		String tString = String.valueOf(aValue);
		int tStringLen = tString.length();
		StringBuffer tStringBuffer = new StringBuffer();

		for (int i = 0; i < aLen - tStringLen; i++)
			tStringBuffer.append("0");

		tStringBuffer.append(tString);

		return tStringBuffer.toString().getBytes();
	}

	// /**
	// * 对传入的BYTE数组进行PKCS#1填充
	// *
	// * @PARAM ABYTESTEXT
	// * 欲进行PKCS#1填充的BYTE数组
	// * @PARAM ABLOCKSIZE
	// * 区块大小
	// * @RETURN 经过PKCS#1填充后的BYTE数组，大小等于传入的区块大小。<BR>
	// * 若传入的BYTE数组长度超过(填充区块大小-3)时无法进行填充作业，将回传NULL。
	// */
	// PUBLIC STATIC BYTE[] ADDPKCS1PADDING(BYTE[] ABYTESTEXT, INT ABLOCKSIZE) {
	// IF (ABYTESTEXT.LENGTH > (ABLOCKSIZE - 3)) {
	// // 传入的BYTE数组长度超过(填充区块大小-3)
	// RETURN NULL;
	// }
	// SECURERANDOM TRANDOM = NEW SECURERANDOM();
	// BYTE[] TAFTERPADDINGBYTES = NEW BYTE[ABLOCKSIZE];
	// TRANDOM.NEXTBYTES(TAFTERPADDINGBYTES);
	// TAFTERPADDINGBYTES[0] = 0X00;
	// TAFTERPADDINGBYTES[1] = 0X02;
	// INT I = 2;
	// FOR (; I < ABLOCKSIZE - 1 - ABYTESTEXT.LENGTH; I++) {
	// IF (TAFTERPADDINGBYTES[I] == 0X00) {
	// TAFTERPADDINGBYTES[I] = (BYTE) TRANDOM.NEXTINT();
	// }
	// }
	// TAFTERPADDINGBYTES[I] = 0X00;
	// SYSTEM.ARRAYCOPY(ABYTESTEXT, 0, TAFTERPADDINGBYTES, (I + 1),
	// ABYTESTEXT.LENGTH);
	//
	// RETURN TAFTERPADDINGBYTES;
	// }

	// /**
	// * 数字字符串转换为bcd,右靠，左补0
	// *
	// * @param value
	// * @param buf
	// */
	// public static void toBcdRight(String value, byte[] buf) {
	// int charpos = 0; // char where we start
	// int bufpos = 0;
	// int tmp = buf.length - (value.length() / 2 + value.length() % 2);
	// for (int i = 0; i < tmp; i++) {
	// value = "00" + value;
	// }
	// if (value.length() % 2 == 1) {
	// // for odd lengths we encode just the first digit in the first byte
	// buf[0] = (byte) (value.charAt(0) - 48);
	// charpos = 1;
	// bufpos = 1;
	// }
	// // encode the rest of the string
	// while (charpos < value.length()) {
	// buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) |
	// (value.charAt(charpos + 1) - 48));
	// charpos += 2;
	// bufpos++;
	// }
	//
	// }
	//
	// /**
	// * 数字字符串转换为bcd,左靠，右补0
	// *
	// * @param value
	// * @param buf
	// */
	// public static void toBcdLeft(String value, byte[] buf) {
	// if (value.length() % 2 != 0) {
	// value = value + "0";
	// }
	// byte[] tmp = value.getBytes();
	// for (int i = 0; i < (value.length() / 2); i++) {
	// buf[i] = CodeUtil.uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
	// }
	// return;
	// }
}