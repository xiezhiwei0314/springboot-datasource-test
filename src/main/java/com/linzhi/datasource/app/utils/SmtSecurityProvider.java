package com.linzhi.datasource.app.utils;

import java.security.Provider;

/**
 * @author duyujun 本类是java.security.Provider的子类，负责向java运行环境中注册本地的签名实现类
 */
public class SmtSecurityProvider extends Provider {

	private static final long serialVersionUID = -8913813653863828832L;

	private static String info = "Chinasmartpay Security Provider v1.0";

	public static String PROVIDER_NAME = "caixincyjj";

	public SmtSecurityProvider() {

		super(PROVIDER_NAME, 1.0, info);
		/**
		 * 添加签名算法的本地实现类
		 */
		put("Signature.SHA1WithRSAEncryption",
				"com.ey.server.security.provider.KYSignature$SHA1WithRSAEncryption");

	/*	put("Signature.SHA1WithRSAEncryption",
				"com.linzhi.datasource.app.security.provider.KYSignature$SHA1WithRSAEncryption");*/

	}
}

