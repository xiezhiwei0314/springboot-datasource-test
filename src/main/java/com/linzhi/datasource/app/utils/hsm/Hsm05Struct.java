package com.linzhi.datasource.app.utils.hsm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class Hsm05Struct {
	public Hsm05Struct() {

	}

	/**
	 * @Package com.allinpay.mobile.util.hsm
	 * @Title: HsmStruct.java
	 * @ClassName: TerminalInitialization
	 * @Description: TODO(移动支付终端初始化)
	 * @author yang-lj
	 * @date 2013-2-21 上午09:11:18
	 * @version V1.0
	 */
	public class TerminalInit {
		public class TerminalInitIn {
			public byte[] allByte = new byte[57];

			public TerminalInitIn(byte[] cmd, byte[] flag, byte[] bdk0Index, byte[] bdk1Index,
					byte[] mkiIndex, byte[] ksn, byte[] cks, byte[] mks, byte[] iv)
					throws Exception {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bout);
				dos.write(cmd);
				dos.write(flag);
				dos.write(bdk0Index);
				dos.write(bdk1Index);
				dos.write(mkiIndex);
				dos.write(ksn);
				dos.write(cks);
				dos.write(mks);
				dos.write(iv);
				allByte = bout.toByteArray();// 获取内存缓冲区中的数据
				dos.close();
				bout.close();
			}
		}

		public class TerminalInitOut {
			public byte[] allByte = new byte[5000];
			public byte[] reply_code = new byte[1];
			public byte[] dataLen = new byte[2];
			public byte[] data = new byte[40];
		}
	}

	/**
	 * @Package com.allinpay.mobile.apmp.util.hsm
	 * @Title: HsmStruct.java
	 * @ClassName: DecryptTrack
	 * @Description: TODO(磁道解密对象)
	 * @author yang-lj
	 * @date 2013-2-27 下午02:43:36
	 * @version V1.0
	 */
	public class DecryptTrack {
		public class DecryptTrackIn {
			public byte[] allByte = new byte[57];

			public DecryptTrackIn(byte[] cmd, byte[] mkiIndex, byte[] mks, byte[] ksn, byte[] wks,
					byte[] flag, byte[] type, byte[] iv, byte[] dataLen, byte[] data)
					throws Exception {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bout);
				dos.write(cmd);
				dos.write(mkiIndex);
				dos.write(mks);
				dos.write(ksn);
				dos.write(wks);
				dos.write(flag);
				dos.write(type);
				dos.write(iv);
				dos.write(dataLen);
				dos.write(data);
				allByte = bout.toByteArray();// 获取内存缓冲区中的数据
				dos.close();
				bout.close();
			}
		}

		public class DecryptTrackOut {
			public byte[] allByte = new byte[5000];
			public byte[] reply_code = new byte[1];
			public byte[] dataLen = new byte[2];
			public byte[] data = new byte[300];
		}
	}

	/**
	 * @Package com.allinpay.mobile.apmp.util.hsm
	 * @Title: HsmStruct.java
	 * @ClassName: dataMsgEncrypt
	 * @Description: TODO(使用TTK 加密报文，终端初始化)
	 * @author yang-lj
	 * @date 2013-2-28 下午05:33:25
	 * @version V1.0
	 */
	public class dataMsgEncrypt {
		public class dataMsgEncryptIn {
			public byte[] allByte = new byte[2000];

			public dataMsgEncryptIn(byte[] cmd, byte[] TTKIndex, byte[] iv, byte[] EDFlag,
					byte[] AlgorithmFlag, byte[] dataLen, byte[] data) throws Exception {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bout);
				dos.write(cmd);
				dos.write(TTKIndex);
				dos.write(iv);
				dos.write(EDFlag);
				dos.write(AlgorithmFlag);
				dos.write(dataLen);
				dos.write(data);
				allByte = bout.toByteArray();// 获取内存缓冲区中的数据
				dos.close();
				bout.close();
			}
		}

		public class dataMsgEncryptOut {
			public byte[] allByte = new byte[5000];
			public byte[] reply_code = new byte[1];
			public byte[] dataLen = new byte[2];
			public byte[] data = new byte[500];
		}
	}

	/**
	 * @Package com.allinpay.mobile.util.hsm
	 * @Title: HsmStruct.java
	 * @ClassName: EncryptToken
	 * @Description: TODO(生成Token)
	 * @author yang-lj
	 * @date 2013-3-26 下午04:19:44
	 * @version V1.0
	 */
	public class EncryptToken {
		public class EncryptTokenIn {
			public byte[] allByte = new byte[57];

			public EncryptTokenIn(byte[] cmd, byte[] TokenIndex, byte[] iv, byte[] EDFlag,
					byte[] AlgorithmFlag, byte[] dataLen, byte[] data) throws Exception {
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bout);
				dos.write(cmd);
				dos.write(TokenIndex);
				dos.write(iv);
				dos.write(EDFlag);
				dos.write(AlgorithmFlag);
				dos.write(dataLen);
				dos.write(data);
				allByte = bout.toByteArray();// 获取内存缓冲区中的数据
				dos.close();
				bout.close();
			}
		}

		public class EncryptTokenOut {
			public byte[] allByte = new byte[5000];
			public byte[] reply_code = new byte[1];
			public byte[] dataLen = new byte[2];
			public byte[] data = new byte[300];
		}
	}

	public class GenerateRsaKey {
		public class GenerateRsaKeyIn {
			public byte[] modeLen = new byte[Hsm05Const.MODULE_LEN];

			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] flag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public GenerateRsaKeyIn(byte[] modeLen, byte[] rsaIndex, byte[] flag, byte[] passwd) {
				System.arraycopy(modeLen, 0, this.modeLen, 0, Hsm05Const.MODULE_LEN);
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(flag, 0, this.flag, 0, flag.length);
				System.arraycopy(passwd, 0, this.passwd, 0, passwd.length);
			}
		}

		public class GenerateRsaKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] publicKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] privateKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] publicKey = new byte[Hsm05Const.PUBLIC_LEN];

			public byte[] privateKey = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class PublicKeyEncrypt {
		public class PublicKeyEncryptIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] publicKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] padFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] athFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] publicKey = new byte[Hsm05Const.PUBLIC_LEN];

			public byte[] encData = new byte[Hsm05Const.PRIVATE_LEN];

			public PublicKeyEncryptIn(byte[] rsaIndex, byte[] publicKeyLen, byte[] dataLen,
					byte[] padFlag, byte[] athFlag, byte[] passwd, byte[] publicKey, byte[] encData) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(publicKeyLen, 0, this.publicKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(padFlag, 0, this.padFlag, 0, padFlag.length);
				System.arraycopy(athFlag, 0, this.athFlag, 0, athFlag.length);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(publicKey, 0, this.publicKey, 0, publicKey.length);
				System.arraycopy(encData, 0, this.encData, 0, encData.length);
			}
		}

		public class PublicKeyEncryptOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}

	}

	public class PrivateKeyEncrypt {
		public class PrivateKeyEncryptIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] privateKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] padFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] athFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] privateKey = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] encData = new byte[Hsm05Const.PRIVATE_LEN];

			public PrivateKeyEncryptIn(byte[] rsaIndex, byte[] privateKeyLen, byte[] dataLen,
					byte[] padFlag, byte[] athFlag, byte[] passwd, byte[] privateKey, byte[] decData) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				// System.arraycopy(privateKeyLen, 0, this.privateKey, 0,
				// HsmConst.DATA_LEN)
				System.arraycopy(privateKeyLen, 0, this.privateKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(padFlag, 0, this.padFlag, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(athFlag, 0, this.athFlag, 0, athFlag.length);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(privateKey, 0, this.privateKey, 0, privateKey.length);
				System.arraycopy(decData, 0, this.encData, 0, decData.length);
			}
		}

		public class PrivateKeyEncryptOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class PrivateRSAKeyEncrypt {
		public class PrivateRSAKeyEncryptIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];

			public PrivateRSAKeyEncryptIn(byte[] rsaIndex, byte[] dataLen,
					byte[] passwd, byte[] data) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(data, 0, this.data, 0, data.length);
			}
		}

		public class PrivateRSAKeyEncryptOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class Sign {
		public class SignIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] privateKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] signFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] athFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] privateKey = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];

			public SignIn(byte[] rsaIndex, byte[] privateKeyLen, byte[] dataLen, byte[] signFlag,
					byte[] athFlag, byte[] passwd, byte[] privateKey, byte[] data) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(privateKeyLen, 0, this.privateKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(signFlag, 0, this.signFlag, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(athFlag, 0, this.athFlag, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(privateKey, 0, this.privateKey, 0, privateKey.length);
				System.arraycopy(data, 0, this.data, 0, data.length);
			}
		}

		public class SignOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class VerifySign {
		public class VerifySignIn {
			public byte[] modeLen = new byte[Hsm05Const.MODULE_LEN];

			public byte[] publicKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] signDataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] signCodeLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] signFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] publicKey = new byte[Hsm05Const.PUBLIC_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] sign = new byte[Hsm05Const.PUBLIC_LEN];

			public VerifySignIn(byte[] modeLen, byte[] publicKeyLen,
					byte[] signDataLen, byte[] signCodeLen, byte[] signFlag,
					byte[] publicKey, byte[] data, byte[] sign) {
				System.arraycopy(modeLen, 0, this.modeLen, 0, Hsm05Const.MODULE_LEN);
				System.arraycopy(publicKeyLen, 0, this.publicKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(signDataLen, 0, this.signDataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(signCodeLen, 0, this.signCodeLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(signFlag, 0, this.signFlag, 0, signFlag.length);
				System.arraycopy(publicKey, 0, this.publicKey, 0, publicKey.length);
				System.arraycopy(data, 0, this.data, 0, data.length);
				// System.arraycopy(sign, 0, this.sign, 0, sign.length);
				int iSignLen = Integer.parseInt(new String(this.signCodeLen));
				System.arraycopy(sign, 0, this.sign, 0, iSignLen);
			}

		}

		public class VerifySignOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] data = new byte[Hsm05Const.FLAG_LEN];
		}

	}

	public class QueryRSAKey {
		public class QueryRSAKeyIn {
		}

		public class QueryRSAKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] keyNum = new byte[Hsm05Const.DATA_LEN];

			public byte[] indexList = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class DelRSAKey {
		public class DelRSAKeyIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public DelRSAKeyIn(byte[] rsaIndex, byte[] passwd) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
			}
		}

		public class DelRSAKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];
		}
	}

	public class ImportPrivateKey {
		public class ImportPrivateKeyIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] privateKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] athFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] privateKey = new byte[Hsm05Const.PRIVATE_LEN];

			public ImportPrivateKeyIn(byte[] rsaIndex, byte[] privateKeyLen, byte[] athFlag,
					byte[] passwd, byte[] privateKey) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(privateKeyLen, 0, this.privateKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(athFlag, 0, this.athFlag, 0, athFlag.length);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(privateKey, 0, this.privateKey, 0, privateKey.length);
			}
		}

		public class ImportPrivateKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];
		}
	}

	public class HashInit {
		public class HashInitIn {
			public byte[] hashFlag = new byte[Hsm05Const.REPLY_CODE_LEN];

			public HashInitIn(byte[] hashFlag) {
				System.arraycopy(hashFlag, 0, this.hashFlag, 0, hashFlag.length);
			}
		}

		public class HashInitOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class HashUpdate {
		public class HashUpdateIn {
			public byte[] hashFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] ivLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] ivData = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];

			// public HashUpdateIn(byte[] hashFlag, byte[] ivLen, byte[]
			// dateLen, byte[] ivdata,
			public HashUpdateIn(byte[] hashFlag, byte[] ivLen, byte[] dataLen, byte[] ivdata,
					byte[] data) {
				System.arraycopy(hashFlag, 0, this.hashFlag, 0, hashFlag.length);
				System.arraycopy(ivLen, 0, this.ivLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				// System.arraycopy(ivData, 0, this.data, 0, ivData.length);
				System.arraycopy(ivdata, 0, this.ivData, 0, ivData.length);
				System.arraycopy(data, 0, this.data, 0, data.length);

			}
		}

		public class HashUpdateOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class HashFinal {
		public class HashFinalIn {
			public byte[] hashFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] ivLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] ivData = new byte[Hsm05Const.PRIVATE_LEN];

			public HashFinalIn(byte[] hashFlag, byte[] ivLen, byte[] ivData) {
				System.arraycopy(hashFlag, 0, this.hashFlag, 0, hashFlag.length);
				System.arraycopy(ivLen, 0, this.ivLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(ivData, 0, this.ivData, 0, ivData.length);
			}
		}

		public class HashFinalOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] data = new byte[Hsm05Const.PRIVATE_LEN];
		}
	}

	public class QueryBMKey {
		public class QueryBMKeyIn {
			public byte[] keyIndex = new byte[Hsm05Const.INDEX_LEN];

			public QueryBMKeyIn(byte[] keyIndex) {
				System.arraycopy(keyIndex, 0, this.keyIndex, 0, Hsm05Const.INDEX_LEN);
			}
		}

		public class QueryBMKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];
		}
	}

	public class GenerateWorkKey {
		public class GenerateWorkKeyIn {
			public byte[] keyLen = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] keyIndex = new byte[Hsm05Const.INDEX_LEN];

			public GenerateWorkKeyIn(byte[] keyLen, byte[] keyIndex) {
				System.arraycopy(keyLen, 0, this.keyLen, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(keyIndex, 0, this.keyIndex, 0, Hsm05Const.INDEX_LEN);
			}
		}

		public class GenerateWorkKeyOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] keyLen = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] workKey = new byte[Hsm05Const.PIK_LEN];

			public byte[] checkvalue = new byte[Hsm05Const.CHV_LEN];
		}
	}

	public class ConvertPINBlock {
		public class ConvertPINBlockIn {
			public byte[] indexIn = new byte[Hsm05Const.INDEX_LEN];

			public byte[] indexOut = new byte[Hsm05Const.INDEX_LEN];

			public byte[] algIn = new byte[Hsm05Const.FLAG_LEN];

			public byte[] algOut = new byte[Hsm05Const.FLAG_LEN];

			public byte[] pin = new byte[Hsm05Const.PIN_BLK_LEN];

			public byte[] pikLenIn = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] pikIn = new byte[Hsm05Const.PIK_LEN];

			public byte[] accoIn = new byte[Hsm05Const.PAN_LEN];

			public byte[] pikLenOut = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] pikOut = new byte[Hsm05Const.PIK_LEN];

			public byte[] accoOut = new byte[Hsm05Const.PAN_LEN];

			public ConvertPINBlockIn(byte[] indexIn, byte[] indexOut, byte[] algIn, byte[] algOut,
					byte[] pin, byte[] pikLenIn, byte[] pikIn, byte[] accoIn, byte[] pikLenOut,
					byte[] pikOut, byte[] accoOut) {
				System.arraycopy(indexIn, 0, this.indexIn, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(indexOut, 0, this.indexOut, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(algIn, 0, this.algIn, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(algOut, 0, this.algOut, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(pin, 0, this.pin, 0, Hsm05Const.PIN_BLK_LEN);
				System.arraycopy(pikLenIn, 0, this.pikLenIn, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(pikIn, 0, this.pikIn, 0, pikIn.length);
				System.arraycopy(accoIn, 0, this.accoIn, 0, accoIn.length);
				System.arraycopy(pikLenOut, 0, this.pikLenOut, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(pikOut, 0, this.pikOut, 0, pikOut.length);
				System.arraycopy(accoOut, 0, this.accoOut, 0, accoOut.length);
			}

		}

		public class ConvertPINBlockOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] pin = new byte[Hsm05Const.PIN_BLK_LEN];
		}
	}

	public class ConvertPWD {
		public class ConvertPWDIn {
			public byte[] indexIn = new byte[Hsm05Const.INDEX_LEN];

			public byte[] indexOut = new byte[Hsm05Const.INDEX_LEN];

			public byte[] pin = new byte[Hsm05Const.PWD_BLK_LEN];

			public byte[] pikLenIn = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] pikIn = new byte[Hsm05Const.PIK_LEN];

			public byte[] pikLenOut = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] pikOut = new byte[Hsm05Const.PIK_LEN];

			public ConvertPWDIn(byte[] indexIn, byte[] indexOut, byte[] pin, byte[] pikLenIn,
					byte[] pikIn, byte[] pikLenOut, byte[] pikOut) {
				System.arraycopy(indexIn, 0, this.indexIn, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(indexOut, 0, this.indexOut, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(pin, 0, this.pin, 0, Hsm05Const.PWD_BLK_LEN);
				System.arraycopy(pikLenIn, 0, this.pikLenIn, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(pikIn, 0, this.pikIn, 0, pikIn.length);
				System.arraycopy(pikLenOut, 0, this.pikLenOut, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(pikOut, 0, this.pikOut, 0, pikOut.length);
			}

		}

		public class ConvertPWDOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] pin = new byte[Hsm05Const.PWD_BLK_LEN];
		}
	}

	public class Bank_GenMac {
		public class Bank_GenMacIN {
			public byte[] bk_inx = new byte[Hsm05Const.INDEX_LEN]; /* 分行BMK索引号 */

			public byte[] mak_len = new byte[Hsm05Const.KEY_LEN_LEN]; /*
																	 * MAC密钥长度(值为8
																	 * /16/24)
																	 */

			public byte[] mak = new byte[Hsm05Const.MAK_LEN]; /* MAC密钥 */

			public byte[] mac_element_len = new byte[Hsm05Const.MAC_ELEMENT_LEN_LEN]; /*
																					 * MAC
																					 * BLOCK长度
																					 */

			public byte[] mac_element = new byte[Hsm05Const.MAX_MAC_ELEMENT_LEN]; /* 计算MAC的数据 */

			public Bank_GenMacIN(byte[] index, byte[] mak_len, byte[] mak, byte[] mac_data_len,
					byte[] mac_data) {
				System.arraycopy(index, 0, this.bk_inx, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(mak_len, 0, this.mak_len, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(mak, 0, this.mak, 0, mak.length);
				System.arraycopy(mac_data_len, 0, this.mac_element_len, 0,
						Hsm05Const.MAC_ELEMENT_LEN_LEN);
				System.arraycopy(mac_data, 0, this.mac_element, 0, mac_data.length);
			}
		}

		public class Bank_GenMacOUT {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN]; /* 返回码 */

			public byte[] mac = new byte[Hsm05Const.MAC_LEN]; /*
															 * MAC 十六进制
															 *//* zhangje */
		}
	}

	public class Bank_VerifyMac {
		public class Bank_VerifyMacIN {
			public byte[] bk_inx = new byte[Hsm05Const.INDEX_LEN]; /* 分行BMK索引号 */

			public byte[] mak_len = new byte[Hsm05Const.KEY_LEN_LEN]; /*
																	 * MAC密钥长度(值为8
																	 * /16/24)
																	 */

			public byte[] mak = new byte[Hsm05Const.MAK_LEN]; /* MAC密钥(ASCII码字符串) */

			public byte[] mac = new byte[Hsm05Const.MAC_LEN]; /*
															 * 待校验的MAC 十六进制
															 *//* zhangje */

			public byte[] mac_element_len = new byte[Hsm05Const.MAC_ELEMENT_LEN_LEN]; /*
																					 * MAC
																					 * BLOCK长度
																					 */

			public byte[] mac_element = new byte[Hsm05Const.MAX_MAC_ELEMENT_LEN]; /* 计算MAC的数据 */

			public Bank_VerifyMacIN(byte[] index, byte[] mak_len, byte[] mak, byte[] mac,
					byte[] mac_data_len, byte[] mac_data) {
				System.arraycopy(index, 0, this.bk_inx, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(mak_len, 0, this.mak_len, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(mak, 0, this.mak, 0, mak.length);
				System.arraycopy(mac, 0, this.mac, 0, mac.length);
				System.arraycopy(mac_data_len, 0, this.mac_element_len, 0,
						Hsm05Const.MAC_ELEMENT_LEN_LEN);
				System.arraycopy(mac_data, 0, this.mac_element, 0, mac_data.length);
			}
		}

		public class Bank_VerifyMacOUT {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];
		}
	}

	public class EncDec_Data {
		public class EncDec_DataIN {
			public byte[] index = new byte[Hsm05Const.INDEX_LEN]; /* 分行BMK索引号 */

			public byte[] key_len = new byte[Hsm05Const.KEY_LEN_LEN]; /*
																	 * 数据密钥长度(值16
																	 * )
																	 */

			public byte[] work_key = new byte[Hsm05Const.MAK_LEN]; /* 密钥数据 */

			public byte[] flag = new byte[Hsm05Const.FLAG_LEN]; /*
																 * 加解密标志，0--加密，1--
																 * 解密；
																 */

			public byte[] data_len = new byte[Hsm05Const.DATA_LEN_LEN]; /* 数据长度 */

			public byte[] data = new byte[Hsm05Const.MAX_DATA_LEN]; /* 参与加解密的数据 */

			public EncDec_DataIN(byte[] index, byte[] key_len, byte[] work_key, byte[] flag,
					byte[] data_len, byte[] data) {
				System.arraycopy(index, 0, this.index, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(key_len, 0, this.key_len, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(work_key, 0, this.work_key, 0, work_key.length);
				System.arraycopy(flag, 0, this.flag, 0, flag.length);
				System.arraycopy(data_len, 0, this.data_len, 0, data_len.length);
				System.arraycopy(data, 0, this.data, 0, data.length);
			}
		}

		public class EncDec_DataOUT {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN]; /* 返回码 */

			public byte[] data_len = new byte[Hsm05Const.DATA_LEN_LEN]; /* 返回数据长度 */

			public byte[] data = new byte[Hsm05Const.MAX_DATA_LEN]; /* 加解密后的数据 */
		}
	}

	public class UnsymConvertPin {
		public class UnsymConvertPinIn {
			public byte[] rsaIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] privateKeyLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] dataLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] padFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] athFlag = new byte[Hsm05Const.FLAG_LEN];

			public byte[] passwd = new byte[Hsm05Const.PWD_LEN];

			public byte[] privateKey = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] decData = new byte[Hsm05Const.PRIVATE_LEN];

			public byte[] flagIn = new byte[Hsm05Const.FLAG_LEN];

			public byte[] accoIn = new byte[Hsm05Const.PAN_LEN];

			public byte[] bmkIndex = new byte[Hsm05Const.INDEX_LEN];

			public byte[] pikLen = new byte[Hsm05Const.KEY_LEN_LEN];

			public byte[] pik = new byte[Hsm05Const.PIK_LEN];

			public byte[] flagOut = new byte[Hsm05Const.FLAG_LEN];

			public byte[] accoOut = new byte[Hsm05Const.PAN_LEN];

			public UnsymConvertPinIn(byte[] rsaIndex, byte[] privateKeyLen, byte[] dataLen,
					byte[] padFlag, byte[] athFlag, byte[] passwd, byte[] privateKey,
					byte[] decData, byte[] flagIn, byte[] accoIn, byte[] bmkIndex, byte[] pikLen,
					byte[] pik, byte[] flagOut, byte[] accoOut) {
				System.arraycopy(rsaIndex, 0, this.rsaIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(privateKeyLen, 0, this.privateKeyLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(dataLen, 0, this.dataLen, 0, Hsm05Const.DATA_LEN);
				System.arraycopy(padFlag, 0, this.padFlag, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(athFlag, 0, this.athFlag, 0, athFlag.length);
				System.arraycopy(passwd, 0, this.passwd, 0, Hsm05Const.PWD_LEN);
				System.arraycopy(privateKey, 0, this.privateKey, 0, privateKey.length);
				System.arraycopy(decData, 0, this.decData, 0, decData.length);
				System.arraycopy(flagIn, 0, this.flagIn, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(accoIn, 0, this.accoIn, 0, accoIn.length);
				System.arraycopy(bmkIndex, 0, this.bmkIndex, 0, Hsm05Const.INDEX_LEN);
				System.arraycopy(pikLen, 0, this.pikLen, 0, Hsm05Const.KEY_LEN_LEN);
				System.arraycopy(pik, 0, this.pik, 0, pik.length);
				System.arraycopy(flagOut, 0, this.flagOut, 0, Hsm05Const.FLAG_LEN);
				System.arraycopy(accoOut, 0, this.accoOut, 0, accoOut.length);
			}
		}

		public class UnsymConvertPinOut {
			public byte[] reply_code = new byte[Hsm05Const.REPLY_CODE_LEN];

			public byte[] pinLen = new byte[Hsm05Const.DATA_LEN];

			public byte[] pinblock = new byte[Hsm05Const.PWD_BLK_LEN];
		}
	}

}
