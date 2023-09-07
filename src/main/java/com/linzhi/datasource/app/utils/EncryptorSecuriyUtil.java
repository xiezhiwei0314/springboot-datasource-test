package com.linzhi.datasource.app.utils;

import com.linzhi.datasource.app.utils.exception.SecException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Properties;

/**
 * @author: xiezhiwei
 * @date: 2023/9/6 16:26
 **/
public class EncryptorSecuriyUtil {


    private static X509Certificate sCA = null;

    // 网关交易证书的索引位置,用于交易签名和私钥解密(OPS为03索引位, ETS为08索引位)
    private static String txHsmIndex = "03";

    private static final String HSSM_CONFIG = "/properties/hssm.properties"; // 加密机配置信息

    // ----敏感信息/文件加密解密--
    private static final byte[] SENSTIVE_AES_KEY = { 49, 56, 57, 66, 53, 48, 55, 65, 56, 66, 66, 54, 53, 52, 56, 50 };
    private static final String SENSTIVE_ALGORITHM = "AES";
    private static final String SENSTIVE_CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final SecretKeySpec secretKey = new SecretKeySpec(SENSTIVE_AES_KEY, SENSTIVE_ALGORITHM);
    private static final IvParameterSpec iv = new IvParameterSpec(
            new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });
    // ----敏感信息/文件加密解密--

    /**
     * 执行初始化操作，将 BouncyCastleProvider添加到java.security.Security中
     */
    static {

        Security.addProvider(new BouncyCastleProvider());
        Security.addProvider(new SmtSecurityProvider());

    }

    private EncryptorSecuriyUtil() {
    };

    /**
     * 初始化加密机
     */
    public static void initial() {
        if (!Hsm05Util.initialed()) {
            String ip = "";
            String aHsmPwd = "";
            Properties hssmProp = new Properties();
            try {
                hssmProp.load(EncryptorSecuriyUtil.class.getResourceAsStream(HSSM_CONFIG));
                ip = hssmProp.getProperty("hssm.ip");
                aHsmPwd = hssmProp.getProperty("hssm.pwd");
                txHsmIndex = hssmProp.getProperty("hssm.index");
                System.out.println("hssm.ip = " + ip + " ,hssm.pwd = " + aHsmPwd + ", hssm.index = " + txHsmIndex);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            Hsm05Util.initial(ip, aHsmPwd);
        }
    }

    public static String MD5(byte[] data) throws SecException {
        Digest digest = new MD5Digest();
        byte[] resBuf = new byte[digest.getDigestSize()];
        try {
            digest.update(data, 0, data.length);
            digest.doFinal(resBuf, 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MD5运算失败：" + e.getMessage());
            throw new SecException("MD5运算失败");
        }
        String resStr = new String(Base64.encode(resBuf));
        return resStr;
    }

    /**
     * 对数据做MD5摘要
     *
     * @param aData
     *            源数据
     * @return 摘要结果
     * @throws SecException
     * @author nilomiao
     * @since 2009-11-27
     */
    public static String MD5Encode(String aData) throws SecException {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = CodeUtil.bytes2HexString(md.digest(aData.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("MD5运算失败：" + e.getMessage());
            throw new SecException("MD5运算失败");
        }
        return resultString;
    }

    /**
     * 对数据做SHA1摘要
     *
     * @param aData
     *            源数据
     * @return 摘要结果，结果Base64编码
     * @throws SecException
     */
    public static String SHA1(String aData) throws SecException {
        Digest digest = new SHA1Digest();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = new byte[aData.getBytes().length];
        bytes = aData.getBytes();
        try {
            digest.update(bytes, 0, bytes.length);
            digest.doFinal(resBuf, 0);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("SHA1运算失败：" + e.getMessage());
            throw new SecException("SHA1运算失败");
        }
        String resStr = new String(Base64.encode(resBuf));
        return resStr;
    }

    /**
     * 用私钥对数据加密,签名算法: MD5withRSA
     *
     * @param src
     *            明文数据
     * @param privateKey
     *            私钥字符串，需要先转换PrivateKey对象
     * @return 加密后密文
     * @throws SecException
     */
    public static String signByRSA(String src, String privateKey) throws SecException {
        try {
            byte[] pribyte = CodeUtil.hexString2Bytes(privateKey.trim());
            PKCS8EncodedKeySpec priKeySpec = new PKCS8EncodedKeySpec(pribyte);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(priKeySpec);

            Security.addProvider(new BouncyCastleProvider());
            Signature sigEng = Signature.getInstance("MD5withRSA");

            sigEng.initSign(priKey);
            sigEng.update(src.getBytes());

            byte[] signature = sigEng.sign();
            String signMsg = CodeUtil.bytes2HexString(signature);

            return signMsg;
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            throw new SecException("No Such Algorithm");
        } catch (InvalidKeyException e) {
            System.out.println(e);
            throw new SecException("Invalid Key");
        } catch (SignatureException e) {
            System.out.println(e);
            throw new SecException("Signature Exception");
        } catch (InvalidKeySpecException e) {
            System.out.println(e);
            throw new SecException("Invalid Key Spec");
        }
    }

    /**
     * 用加密机对数据签名，签名算法为：RSA_SHA1
     *
     * @param aSrcData
     *            源数据
     * @return 签名结果，经过Base64编码
     * @throws SecException
     */
    public static String signByHsm(byte[] aSrcData) throws SecException {
        // byte[] tRes = Hsm05Util.sign(aSrcData,
        // SIGNATURE_ALGORITHM_RSA_SHA1,sSignHsmIndex);
        byte[] tRes = Hsm05Util.signWithEncode(aSrcData, "0", txHsmIndex);
        return new String(Base64.encode(tRes));
    }

    /**
     * 从证书内容文本字符串读取公钥，要求证书为BASE64格式
     *
     * @param certMsgStr
     *            证书文本
     * @return 公钥
     */
    public static Key getPubKeyFromStr(String certMsgStr) {

        InputStream tIn = null;
        Certificate tCer = null;
        Key key = null;
        try {
            CertificateFactory tCertFactory = CertificateFactory.getInstance("X.509");
            tIn = new ByteArrayInputStream(Base64.decode(certMsgStr));
            tCer = (X509Certificate) tCertFactory.generateCertificate(tIn);

            key = tCer.getPublicKey();
            System.out.println(CodeUtil.bytes2HexString(key.getEncoded()));
        } catch (CertificateException e) {
            System.out.println("x509证书读取异常：" + e);
        } finally {
            try {
                if (tIn != null)
                    tIn.close();
            } catch (IOException e) {
                System.out.println("关闭X509证书流对象异常：" + e);
            }
        }

        return key;
    }

    /**
     * 使用公钥验证签名
     *
     * @param pubKey
     *            公钥
     * @param srcBytes
     *            签名原串字节数组
     * @param signBytes
     *            签名串字节数组
     * @param signAlg
     *            签名算法
     * @return 验签结果 true = 成功 false = 不成功
     *
     * @see signByPriKey
     */
    public static boolean verifyByPubKey(Key pubKey, byte[] srcBytes, byte[] signBytes) {
        boolean result = false;
        try {
            Signature sign = Signature.getInstance("SHA1WithRSA", new BouncyCastleProvider());
            sign.initVerify((PublicKey) pubKey);
            sign.update(srcBytes);
            result = sign.verify(signBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("公钥验签 - 无效算法：");
        } catch (InvalidKeyException e) {
            System.out.println("公钥验签 - 无效的密钥：");
        } catch (SignatureException e) {
            System.out.println("公钥验签 - 签名异常：");
        }

        return result;
    }

    /**
     * 使用公钥验证签名 provider不使用bouncyCastle
     *
     * @param pubKey
     *            公钥
     * @param srcBytes
     *            签名原串字节数组
     * @param signBytes
     *            签名串字节数组
     * @param signAlg
     *            签名算法
     * @return 验签结果 true = 成功 false = 不成功
     *
     * @see signByPriKey
     */

    public static boolean verifyByPubKeyWithoutBC(Key pubKey, byte[] srcBytes, byte[] signBytes, String signAlg) {
        boolean result = false;
        try {
            Signature sign = Signature.getInstance(signAlg);

            sign.initVerify((PublicKey) pubKey);
            sign.update(srcBytes);
            result = sign.verify(signBytes);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("公钥验签 - 无效算法：");
        } catch (InvalidKeyException e) {
            System.out.println("公钥验签 - 无效的密钥：");
        } catch (SignatureException e) {
            System.out.println("公钥验签 - 签名异常：");
        }

        return result;
    }

    /**
     * 计算MAC，软件实现 (1)对MAB，按每8个字节做异或（不管信息中的字符格式），如果最后不满8个字节，则添加“0X00”
     * (2)将异或运算后的最后8个字节（RESULT BLOCK）转换成16 个HEXDECIMAL (3)取前8 个字节用MAK加密
     * (4)将加密后的结果与后8 个字节异或 (5)用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算
     * (6)将运算后的结果（ENCBLOCK2）转换成16 个HEXDECIMAL (7)取前8个字节作为MAC值
     *
     * @param aSrcData
     *            源数据(MAB)
     * @param MAK
     *            Mac密钥
     * @return
     * @throws SecException
     */
    public static byte[] macBySoft(byte[] aSrcData, byte[] MAK) throws SecException {
        /**
         * step 1
         */
        int len1 = (aSrcData.length % 8 == 0) ? 0 : (8 - aSrcData.length % 8);
        byte[] MAB = new byte[aSrcData.length + len1];
        System.arraycopy(aSrcData, 0, MAB, 0, aSrcData.length);
        for (int i = 0; i < len1; i++) {
            MAB[aSrcData.length + i] = 0x00;
        }
        int len2 = MAB.length / 8;
        byte[] result1 = new byte[8];
        System.arraycopy(MAB, 0, result1, 0, 8);
        for (int j = 1; j < len2; j++) {
            byte[] tmp1 = new byte[8];
            System.arraycopy(MAB, j * 8, tmp1, 0, 8);
            for (int jj = 0; jj < 8; jj++) {
                result1[jj] = (byte) (result1[jj] ^ tmp1[jj]);
            }
        }
        /**
         * step 2
         */
        String ss1 = CodeUtil.bytes2HexString(result1);
        byte[] result2_1 = ss1.substring(0, 8).getBytes();
        byte[] result2_2 = ss1.substring(8, 16).getBytes();
        /**
         * step 3
         */
        byte[] result3 = desEncryptBySoft(result2_1, MAK);
        /**
         * step 4
         */
        byte[] result4 = new byte[8];
        for (int m = 0; m < 8; m++) {
            result4[m] = (byte) (result3[m] ^ result2_2[m]);
        }
        /**
         * step 5
         */
        byte[] result5 = desEncryptBySoft(result4, MAK);
        /**
         * step 6
         */
        String result6 = CodeUtil.bytes2HexString(result5);
        /**
         * step 7
         */
        return result6.substring(0, 8).getBytes();
    }

    /**
     * 软件根据原数据生成MAC值
     *
     * @param aData
     *            原数据buffer ,Bytes数组格式,长度为8倍数，不足以0x00补足
     * @param aMac
     *            MAC值 ,Hex 格式
     * @param aMackey
     *            MAC密钥密文 ,Hex 格式（由机构主密钥（BMK）加密）
     * @param aBmkey
     *            机构主密钥明文 ,Hex 格式
     * @return
     * @throws SecException
     */
    public static byte[] genMacBySoft(byte[] aData, String aMackey, String aBmkey) throws SecException {
        byte[] tResult = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        byte[] tBlock = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        // byte[] aData = {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30};
        try {
            // step1.检查原数据并进行相应填充
            int len1 = (aData.length % 8 == 0) ? 0 : (8 - aData.length % 8);
            byte[] MAB = new byte[aData.length + len1];
            System.arraycopy(aData, 0, MAB, 0, aData.length);
            for (int i = 0; i < len1; i++) {
                MAB[aData.length + i] = 0x00;
            }

            // step2. 用BMK解密MacKey;
            byte[] tMacKey = DESCoder.decrypt(CodeUtil.hexString2Bytes(aMackey), CodeUtil.hexString2Bytes(aBmkey));

            // step3.计算MAC值
            int len2 = MAB.length / 8;
            for (int j = 0; j < len2; j++) {
                System.arraycopy(MAB, j * 8, tBlock, 0, 8);
                for (int jj = 0; jj < 8; jj++) {
                    tResult[jj] = (byte) (tResult[jj] ^ tBlock[jj]);
                }
                tResult = DESCoder.encrypt(tResult, tMacKey);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new SecException("验证Mac时发生异常:[" + e.getLocalizedMessage() + "]");
        }
        return tResult;

    }

    /**
     * 软件MAC验证
     *
     * @param aData
     *            原数据buffer ,Bytes数组格式,长度为8倍数，不足以0x00补足
     * @param aMac
     *            MAC值 ,Hex 格式
     * @param aMackey
     *            MAC密钥密文 ,Hex 格式（由机构主密钥（BMK）加密）
     * @param aBmkey
     *            机构主密钥明文 ,Hex 格式
     * @return
     * @throws SecException
     */
    public static boolean verfyMacBySoft(byte[] aData, String aMac, String aMackey, String aBmkey) throws SecException {
        byte[] tResult = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        byte[] tBlock = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
        try {
            // step1.检查原数据并进行相应填充
            int len1 = (aData.length % 8 == 0) ? 0 : (8 - aData.length % 8);
            byte[] MAB = new byte[aData.length + len1];
            System.arraycopy(aData, 0, MAB, 0, aData.length);
            for (int i = 0; i < len1; i++) {
                MAB[aData.length + i] = 0x00;
            }

            // step2. 用BMK解密MacKey;
            byte[] tMacKey = DESCoder.decrypt(CodeUtil.hexString2Bytes(aMackey), CodeUtil.hexString2Bytes(aBmkey));

            // step3.计算MAC值
            int len2 = MAB.length / 8;
            for (int j = 0; j < len2; j++) {
                System.arraycopy(MAB, j * 8, tBlock, 0, 8);
                for (int jj = 0; jj < 8; jj++) {
                    tResult[jj] = (byte) (tResult[jj] ^ tBlock[jj]);
                }
                tResult = DESCoder.encrypt(tResult, tMacKey);
            }

            // 验证MAC是否符合
            if (aMac.equals(CodeUtil.bytes2HexString(tResult)))
                return true;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new SecException("验证Mac时发生异常:[" + e.getLocalizedMessage() + "]");
        }
        return false;

    }

    /**
     * 由PEM格式证书内容构造X509Certificate证书对象
     *
     * @param aCer
     *            证书内容
     * @return 证书对象
     * @throws SecException
     */
    public static X509Certificate getX509Certificate(String aCer) throws SecException {
        if (aCer == null)
            throw new SecException("证书对象为空");

        if (aCer.indexOf("BEGIN CERTIFICATE") < 0) {
            aCer = "-----BEGIN CERTIFICATE-----\n" + aCer + "\n-----END CERTIFICATE-----\n";
        }
        X509Certificate tCer = null;
        CertificateFactory tCf;
        try {
            tCf = CertificateFactory.getInstance("X.509");
        } catch (Exception e1) {
            System.out.println("[创建X509证书对象异常：]" + e1);
            throw new SecException(e1.getMessage());
        }

        InputStream tIn = new ByteArrayInputStream(aCer.getBytes());
        try {
            tCer = (X509Certificate) tCf.generateCertificate(tIn);
        } catch (Exception e) {
            System.out.println("[x509验证异常：]" + e);
            throw new SecException(e.getMessage());
        } finally {
            try {
                tIn.close();
            } catch (IOException e) {
                System.out.println("[构造X509证书时流对象关闭异常：]" + e);
            }
        }
        return tCer;
    }

    /**
     * DES加密
     *
     * @param aSrc
     *            源数据
     * @param aDESKey
     *            密钥数据
     * @return 加密结果
     * @throws SecException
     */
    public static byte[] desEncryptBySoft(byte[] aSrc, byte[] aDESKey) throws SecException {

        String Algorithm = "DES/ECB/NoPadding";// "DESede"; //定义 加密算法,可用
        // DES,DESede(3DES),Blowfish
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(aDESKey, "DES");
        // 加密
        try {
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(aSrc);
        } catch (Exception e) {
            throw new SecException("DES加密失败" + e.getMessage());
        }
    }

    /**
     * DES解密
     *
     * @param aSrc
     *            源数据
     * @param aDESKey
     *            密钥数据
     * @return 解密结果
     * @throws SecException
     */
    public static byte[] desDecryptBySoft(byte[] aSrc, byte[] aDESKey) throws SecException {
        try {
            String Algorithm = "DES/ECB/NoPadding";// "DESede"; //定义 加密算法,可用
            // DES,DESede(3DES),Blowfish
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(aDESKey, "DES");
            // 解密
            Cipher cipher = Cipher.getInstance(Algorithm);
            // KeySpec keySpec = new DESedeKeySpec(aDESKey);
            // SecretKeyFactory keyFactory =
            // SecretKeyFactory.getInstance("DES");
            // Cipher cipher = Cipher.getInstance(Algorithm);
            // SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            return cipher.doFinal(aSrc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecException("des 解密失败" + e.getMessage());
        }

    }

    /**
     * 3-DES加密
     *
     * @param aSrc
     *            源数据
     * @param aDESKey
     *            密钥数据
     * @return 加密结果
     * @throws SecException
     */
    public static byte[] TripleDESEncrypt(byte[] aSrc, byte[] aDESKey) throws SecException {
        byte[] desKey = new byte[24];
        if (16 == aDESKey.length) {// 如果密钥长度为16位，则将密钥数据k1+k2转化为k1+k2+k1
            System.arraycopy(aDESKey, 0, desKey, 0, 16);
            System.arraycopy(aDESKey, 0, desKey, 16, 8);
        } else {
            System.arraycopy(aDESKey, 0, desKey, 0, aDESKey.length);
        }
        String Algorithm = "TripleDES/ECB/NoPadding";// "DESede"; //定义
        // 加密算法,可用
        // DES,DESede(3DES),Blowfish
        // 生成密钥
        SecretKey deskey = new SecretKeySpec(desKey, "DESede");
        // 加密
        try {
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(aSrc);
        } catch (Exception e) {
            throw new SecException("DES加密失败" + e.getMessage());
        }
    }

    /**
     * 3-DES解密
     *
     * @param aSrc
     *            源数据
     * @param aDESKey
     *            密钥数据
     * @return 解密结果
     * @throws SecException
     */
    public static byte[] TripleDESDecrypt(byte[] aSrc, byte[] aDESKey) throws SecException {
        try {
            byte[] desKey = new byte[24];
            if (16 == aDESKey.length) {// 如果密钥长度为16位，则将密钥数据k1+k2转化为k1+k2+k1
                System.arraycopy(aDESKey, 0, desKey, 0, 16);
                System.arraycopy(aDESKey, 0, desKey, 16, 8);
            } else {
                System.arraycopy(aDESKey, 0, desKey, 0, aDESKey.length);
            }
            String Algorithm = "TripleDES/ECB/NoPadding";// "DESede"; //定义
            // 加密算法,可用
            // DES,DESede(3DES),Blowfish
            // 生成密钥
            // SecretKey deskey = new SecretKeySpec(aDESKey, "DESede");
            // 解密
            // Cipher c1 = Cipher.getInstance(Algorithm);
            KeySpec keySpec = new DESedeKeySpec(desKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESEde");
            Cipher cipher = Cipher.getInstance(Algorithm);
            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(aSrc);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecException("3des 解密失败" + e.getMessage());
        }

    }

    /**
     * 对PIN进行加密处理 1）pin block 2）pin+pan block 3）3-DES加密
     *
     * @param aPin
     * @param aPan
     * @param aPIK
     * @return
     * @throws SecException
     */
    public static byte[] pinEncryptWithPan(String aPin, String aPan, byte[] aPIK) throws SecException {

        byte[] tBlock = CodeUtil.pin2PinBlockWithCardNO(aPin, aPan);
        byte[] tEnrypt = TripleDESEncrypt(tBlock, aPIK);
        return tEnrypt;
    }

    /**
     * 对主账号，第二磁道，第三磁道信息的加密处理（3-DES） 加密步骤： （1）将源数据长度的BCD与源数据BCD数据组合到一起；
     * （2）如步骤（1）得到的数据长度不是8的倍数，则后补0x00达到8的倍数； （3）用TRK对步骤(2)的数据进行3DES加密，形成加密后的数据；
     *
     * @throws SecException
     */
    public static byte[] trkEncrypt(String aSrc, byte[] aTRK, int len) throws SecException {
        /**
         * setp1-2
         */
        int data_len = aSrc.length();
        int len_data_len_bcd = len;
        int data_len_bcd = (data_len / 2) + (data_len % 2);
        byte[] lenBCD = new byte[len_data_len_bcd];
        byte[] dataBCD = new byte[data_len_bcd];
        CodeUtil.toBcd_left(String.valueOf(data_len), lenBCD);
        CodeUtil.toBcd_left(aSrc, dataBCD);
        /**
         * len8必须是8的倍数
         */
        int len8 = ((len_data_len_bcd + data_len_bcd) % 8 == 0) ? (len_data_len_bcd + data_len_bcd)
                : ((len_data_len_bcd + data_len_bcd) + (8 - (len_data_len_bcd + data_len_bcd) % 8));
        byte[] tmp = new byte[len8];
        for (int i = 0; i < len8; i++) {
            tmp[i] = 0x00;
        }
        System.arraycopy(lenBCD, 0, tmp, 0, len_data_len_bcd);
        System.arraycopy(dataBCD, 0, tmp, len_data_len_bcd, data_len_bcd);
        /**
         * step3
         */
        byte[] desEncrypt = TripleDESEncrypt(tmp, aTRK);

        return desEncrypt;
    }

    /**
     * bcd转化为数字字符串 右靠
     *
     * @param bytes
     * @return
     */
    public static String bcd2Str_right(byte[] bytes) {

        StringBuffer temp = new StringBuffer(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();

    }

    /**
     * bcd转化为数字字符串 左靠
     *
     * @param bytes
     * @return
     */
    public static String bcd2Str_left(byte[] bytes, int length) {
        /*
         * StringBuffer temp=new StringBuffer(length);
         *
         * for(int i=0;i<bytes.length;i++){ temp.append((byte)((bytes[i]&
         * 0xf0)>>>4)); temp.append((byte)(bytes[i]& 0x0f)); } return
         * temp.toString().substring(0, length);
         */
        String ret = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret.substring(0, length);
    }

    /**
     * 对主账号，第二磁道，第三磁道信息的解密处理（3-DES） 加密步骤： （1）将源数据长度的BCD与源数据BCD数据组合到一起；
     * （2）如步骤（1）得到的数据长度不是8的倍数，则后补0x00达到8的倍数； （3）用TRK对步骤(2)的数据进行3DES加密，形成加密后的数据；
     * 解密步骤反向进行
     *
     * @throws SecException
     */
    public static String trkDecrypt(int srcLen, byte[] des, byte[] aTRK) throws SecException {

        byte[] src = TripleDESDecrypt(des, aTRK);
        byte[] len = new byte[srcLen];
        System.arraycopy(src, 0, len, 0, srcLen);
        int lenInt = Integer.parseInt(bcd2Str_right(len));
        int bcd_len = lenInt / 2 + lenInt % 2;
        byte[] bcd_result = new byte[bcd_len];
        System.arraycopy(src, srcLen, bcd_result, 0, bcd_len);
        String result = bcd2Str_left(bcd_result, lenInt);
        return result;
    }

    /**
     * 用通联私钥解密加密报文的对称密钥
     *
     * @param String
     *            encryptedKey 加密报文
     * @return String 解密后的报文
     * @author nilomiao
     */
    public static String decryptByPrivateKey(byte[] aCipherDataInBase64) throws SecException {
        byte[] tData = Base64.decode(aCipherDataInBase64);
        return new String(Hsm05Util.rsaPriKeyDecrypt(tData, txHsmIndex)).trim();
    }

    /**
     * 用通联私钥解密加密报文的对称密钥
     *
     * @param String
     *            encryptedKey 加密报文
     * @return String 解密后的报文
     * @author nilomiao
     */
    public static String decryptByPrivateKey(String aCipherDataInBase64) throws SecException {
        byte[] tData = Base64.decode(aCipherDataInBase64);
        return new String(Hsm05Util.rsaPriKeyDecrypt(tData, txHsmIndex)).trim();
    }

    public static String encryptByPublicKey(String src, String certPath) {
        PublicKey key = null;
        try {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            FileInputStream fis = new FileInputStream(certPath);

            X509Certificate cert = (X509Certificate) factory.generateCertificate(fis);
            key = cert.getPublicKey();
        } catch (FileNotFoundException e) {
            System.out.println("从证书文件读取公钥 - 证书文件不存在：");
            System.out.println(e);
        } catch (CertificateException e) {
            System.out.println("从证书文件读取公钥 - 密钥读取异常：");
            System.out.println(e);
        }

        byte[] encBytes = null;
        try {

            Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encBytes = cipher.doFinal(src.getBytes());

        } catch (NoSuchAlgorithmException e) {
            System.out.println("公钥加密 - 无效算法：");
        } catch (InvalidKeyException e) {
            System.out.println("公钥加密 - 无效密钥：");
        } catch (IllegalBlockSizeException e) {
            System.out.println("公钥加密 - 非法的分块大小：");
        } catch (NoSuchPaddingException e) {
            System.out.println("公钥加密 - 错误的填充格式：");
        } catch (BadPaddingException e) {
            System.out.println("公钥加密 - 填充异常：");
        }

        return new String(Base64.encode(encBytes));
    }

    // from HsmApp
    static boolean Str2Hex(byte[] in, byte[] out, int len) {
        byte[] asciiCode = { 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

        if (len > in.length) {
            return false;
        }

        if (len % 2 != 0) {
            return false;
        }

        byte[] temp = new byte[len];

        for (int i = 0; i < len; i++) {
            if (in[i] >= 0x30 && in[i] <= 0x39) {
                temp[i] = (byte) (in[i] - 0x30);
            } else if (in[i] >= 0x41 && in[i] <= 0x46) {
                temp[i] = asciiCode[in[i] - 0x41];
            } else if (in[i] >= 0x61 && in[i] <= 0x66) {
                temp[i] = asciiCode[in[i] - 0x61];
            } else {
                return false;
            }
        }

        for (int i = 0; i < len / 2; i++) {
            out[i] = (byte) (temp[2 * i] * 16 + temp[2 * i + 1]);
        }

        return true;
    }

    // from HsmApp
    static boolean Hex2Str(byte[] in, byte[] out, int len) {
        byte[] asciiCode = { 0x41, 0x42, 0x43, 0x44, 0x45, 0x46 };

        if (len > in.length) {
            return false;
        }

        byte[] temp = new byte[2 * len];

        for (int i = 0; i < len; i++) {
            temp[2 * i] = (byte) ((in[i] & 0xf0) / 16);
            temp[2 * i + 1] = (byte) (in[i] & 0x0f);
        }

        for (int i = 0; i < 2 * len; i++) {
            if (temp[i] <= 9 && temp[i] >= 0) {
                out[i] = (byte) (temp[i] + 0x30);
            } else {
                out[i] = asciiCode[temp[i] - 0x0a];
            }
        }

        return true;
    }

    /**
     * 敏感数据加密
     *
     * @param src
     * @return
     * @throws SecException
     */
    public static String senstiveEncrypt(String src) throws SecException {
        try {
            Cipher cipher = Cipher.getInstance(SENSTIVE_CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] tgtBytes = cipher.doFinal(src.getBytes());
            return CodeUtil.bytes2HexString(tgtBytes);
        } catch (Exception e) {
            throw new SecException("敏感数据加密失败" + e.getMessage());
        }
    }

    /**
     * 敏感数据解密
     *
     * @param src
     * @return
     * @throws SecException
     */
    public static String senstiveDecrypt(String src) throws SecException {
        try {
            Cipher cipher = Cipher.getInstance(SENSTIVE_CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] tgtBytes = cipher.doFinal(CodeUtil.hexString2Byte(src));
            return new String(tgtBytes);
        } catch (Exception e) {
            throw new SecException("敏感数据解密失败" + e.getMessage());
        }

    }

    /**
     * 敏感文件加密
     *
     * @param in
     * @param out
     * @throws SecException
     */
    public static void encryptFile(InputStream in, OutputStream out) throws SecException {
        CipherInputStream cin = null;
        try {
            Cipher cipher = Cipher.getInstance(SENSTIVE_CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            cin = new CipherInputStream(in, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cin.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }

        } catch (Exception e) {
            throw new SecException("敏感文件加密失败" + e.getMessage());
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                }
            ;
            if (cin != null)
                try {
                    cin.close();
                } catch (Exception e) {
                }
            ;
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                }
            ;
        }
    }

    /**
     * 敏感文件解密
     *
     * @param in
     * @param out
     * @throws Exception
     */
    public static void decryptFile(InputStream in, OutputStream out) throws Exception {
        CipherOutputStream cout = null;
        try {
            Cipher cipher = Cipher.getInstance(SENSTIVE_CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            cout = new CipherOutputStream(out, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                cout.write(cache, 0, nRead);
                cout.flush();
            }

        } catch (Exception e) {
            throw new SecException("敏感文件解密失败" + e.getMessage());
        } finally {
            if (cout != null)
                try {
                    cout.close();
                } catch (Exception e) {
                }
            ;
            if (out != null)
                try {
                    out.close();
                } catch (Exception e) {
                }
            ;
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                }
            ;
        }
    }

    public static void main(String[] args) {
        try {

             String enstr = senstiveEncrypt("432501199808020028");
             System.out.println(enstr);
             System.out.println(senstiveDecrypt(enstr));
            // FileInputStream is = new FileInputStream(new
            // File("D:/test.xls"));
            // OutputStream out = new FileOutputStream(new
            // File("D:/testAES.xls"));
            // encryptFile(is,out);
            // FileInputStream dis = new FileInputStream(new
            // File("D:/testAES.xls"));
            // OutputStream dout = new FileOutputStream(new
            // File("D:/testAESDE.xls"));
            // decryptFile(dis,dout);
        } catch (Exception ce) {
            ce.printStackTrace();
        }

    }
}
