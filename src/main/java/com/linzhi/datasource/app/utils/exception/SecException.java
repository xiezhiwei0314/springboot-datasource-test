package com.linzhi.datasource.app.utils.exception;

/**
 * Allinpay Security系统例外
 */
public class SecException extends Exception {   

	private static final long serialVersionUID = -4095275974552064550L;


    public static final String CS_EXC_CODE_8000 = "8000";
    public static final String CS_EXC_CODE_8001 = "8001";
    public static final String CS_EXC_CODE_8002 = "8002";
    public static final String CS_EXC_CODE_8003 = "8003";
    public static final String CS_EXC_CODE_8004 = "8004";
    public static final String CS_EXC_CODE_8005 = "8005";
    

    //加密机异常
    public static final String CS_EXC_MSG_8000 = "PIN格式错误";
    public static final String CS_EXC_MSG_8001 = "连接加密机失败";
    public static final String CS_EXC_MSG_8002 = "公钥加密失败";
    public static final String CS_EXC_MSG_8003 = "PIN转PINBLOCK失败";
    public static final String CS_EXC_MSG_8004 = "转PIN失败";
    public static final String CS_EXC_MSG_8005 = "找不到密钥";	

	/**
     * 例外代码
     */
    protected String iErrCode = "";

    /**
     * 详细信息
     */
    protected String iErrMessage = "";

	/**
     * 构造SecException对象，并给定对象[异常信息]属性。
     * @param aMessage 异常信息
	 */
	public SecException(String aMessage) {
		super(aMessage);
	}

    /**
     * 构造SecException对象，并给定对象[异常代码]及[异常信息]属性。
     * @param aCode 异常代码
     * @param aMessage 异常信息
     */
    public SecException(String aCode, String aMessage) {
        super(aMessage);
        iErrCode = aCode.trim();
    }
    /**
     * 取得异常代码
     * @return 异常代码
     */
    public String getCode() {
        return iErrCode;
    }

    /**
     * 取得异常信息
     * @return 异常信息
     */
    public String getMessage() {
        return super.getMessage();
    }
    /**
     * 构造SecException对象，并给定对象[例外代码]、[例外信息]及[详细信息]属性。
     * @param aCode 例外代码
     * @param aMessage 例外信息
     * @param aDetailMessage 详细信息
     */
    public SecException(String aCode, String aMessage, String aDetailMessage) {
        super(aMessage.trim());
        iErrCode = aCode.trim();
        iErrMessage= aDetailMessage.trim();
    }
}

