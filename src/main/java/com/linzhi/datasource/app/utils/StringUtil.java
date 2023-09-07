package com.linzhi.datasource.app.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public final class StringUtil {

	private static final String algorithm = "MD5";

	// 整数金额正规表达式
	public static Pattern orderAmountPattern = Pattern.compile("\\d{1,16}");

	// 订单号正规表达式
	public static Pattern orderNoPattern = Pattern.compile("[\\w,_,-]{1,50}");

	// 手机号码备用正规表达式，备用：^(13[0-9]|15[0|3|6|7|8|9]|18[8|9])\\d{8}$
	public static Pattern mobileNoPattern = Pattern
			.compile("^(13[0-9]|15[0-9]|18[0-9])\\d{8}$");
	
	public static Pattern ratePattern = Pattern.compile("^((([1-9]{1}\\d{0,1}))|([0]{1}))((\\.(\\d){1,2}))?$|(100|100.0|100.00)");


	/**
	 * 判断object是否为空
	 * 
	 * @param object
	 *            Object对象
	 * @return 布尔值
	 */
	public static boolean isNull(Object object) {
		if (object instanceof String) {
			return StringUtil.isEmpty(String.valueOf(object));
		}
		return object == null;
	}

	/**
	 * 判断字符串是否为空.
	 * 
	 * @param src
	 * @return
	 */
	public static boolean isEmpty(final String src) {
		if (null == src || "".equals(src)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断字符数组是否为空
	 * @param src
	 * @return
	 */
	public static boolean isEmpty(final String[] src) {
		if (null == src || src.length == 0) {
			return true;
		}
		return false;
	}

	public static String toString(final Object obj) {
		if (null == obj) {
			return null;
		} else {
			return String.valueOf(obj);
		}
	}
	
	/**
	 * trim字符串
	 * @param obj
	 * @return
	 */
	public static String toStringWithTrim(final String obj){
		if (null == obj) {
			return "";
		} else {
			return obj.trim();
		}
	}
	
	
	/**
	 * trim字符串
	 * @param obj
	 * @return
	 */
	public static String objectToStringWithTrim(final Object obj){
		String back=toString(obj);
		return toStringWithTrim(back);
	}
	
	/**
	 * 把首字母变为大写.
	 * 
	 * @param src
	 * @return
	 */
	public static String capFirst(final String src) {
		if (isEmpty(src)) {
			return src;
		}
		if (src.length() == 1) {
			return src.toUpperCase();
		}
		String first = src.substring(0, 1);
		first = first.toUpperCase();
		return first + src.substring(1);
	}

	/**
	 * 把首字母变为小写.
	 * 
	 * @param src
	 * @return
	 */
	public static String uncapFirst(final String src) {
		if (isEmpty(src)) {
			return src;
		}
		if (src.length() == 1) {
			return src.toLowerCase();
		}
		String first = src.substring(0, 1);
		first = first.toLowerCase();
		return first + src.substring(1);
	}
	
	/**
	 * 替换掉strXML中标签名为tagName的标签的value 
	 * @param strXML
	 * @param tagName
	 * @param TagValue
	 * @return
	 */
	public static String replaceTagValue(final String strXML,final String tagName,final String TagValue){
		
		if (null == tagName || "".equals(tagName)) {
			return "";
		}
		if (null == strXML || strXML.indexOf("<" + tagName + ">") == -1
				|| strXML.indexOf("</" + tagName + ">") == -1) {
			return "";
		}
		return strXML.substring(0, strXML.indexOf("<" + tagName + ">")+("<" + tagName + ">").length()) 
			                       +TagValue
			                       +strXML.substring(strXML.indexOf("</" + tagName + ">"), strXML.length());
	}
	
	
	
	/**
	 * 判断字符串src字符串中是否存在des字符串，存在返回true，不存在返回false add by lee
	 * @param des
	 * @param src
	 * @return
	 */
	public static boolean isExist(String src,String des){
		if(isEmpty(des) || isEmpty(src)){
			return false;
		}
		return toStringWithTrim(src).indexOf(toStringWithTrim(des))>-1;
	}
	

	/**
	 * Replace all occurances of a string within another string.
	 * 
	 * @param text
	 *            text to search and replace in
	 * @param repl
	 *            String to search for
	 * @param with
	 *            String to replace with
	 * @return the text with any replacements processed
	 * @see #replace(String text, String repl, String with, int max)
	 */
	public static String replace(String text, String repl, String with) {
		return replace(text, repl, with, -1);
	}

	/**
	 * Replace a string with another string inside a larger string, for the
	 * first <code>max</code> values of the search string. A <code>null</code>
	 * reference is passed to this method is a no-op.
	 * 
	 * @param text
	 *            text to search and replace in
	 * @param repl
	 *            String to search for
	 * @param with
	 *            String to replace with
	 * @param max
	 *            maximum number of values to replace, or <code>-1</code> if no
	 *            maximum
	 * @return the text with any replacements processed
	 * @throws NullPointerException
	 *             if repl is null
	 */
	public static String replace(String text, String repl, String with, int max) {
		if (text == null) {
			return null;
		}

		StringBuffer buf = new StringBuffer(text.length());
		int start = 0;
		int end = text.indexOf(repl, start);
		while (end != -1) {
			buf.append(text.substring(start, end)).append(with);
			start = end + repl.length();

			if (--max == 0) {
				break;
			}
			end = text.indexOf(repl, start);
		}
		buf.append(text.substring(start));
		return buf.toString();
	}

	/**
	 * 用Map中的变量名-变量值替换源字符串中的变量名. 不支持$a${name}${value{}类似的替换.
	 * 这个方法只用于严格遵循${v}的格式,并且$,{,}三个符号被定义为保留字. 如果要替换的字符串中包含这三个保留字的任一个,则此方法不能正确处理.
	 * 
	 * @param src
	 *            字符串
	 * @param value
	 *            变量名-变量值
	 * @return String <br>
	 * <br>
	 *         Example: <br>
	 *         String src = "Hello ${username}, this is ${target} speaking."; <br>
	 *         Map map = new HashMap(); <br>
	 *         map.put("username", "petter"); <br>
	 *         map.put("target", "tom"); <br>
	 *         src = StringUtil.replaceVariable(str, map); <br>
	 *         #The src equals: <br>
	 *         "Hello petter, this is tom speaking."
	 */
	public static String replaceVariable(final String src, final Map value) {
		int len = src.length();
		StringBuffer buf = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			char c = src.charAt(i);
			if (c == '$') {
				i++;
				StringBuffer key = new StringBuffer();
				char temp = src.charAt(i);
				while (temp != '}') {
					if (temp != '{') {
						key.append(temp);
					}
					i++;
					temp = src.charAt(i);
				}
				String variable = (String) value.get(key.toString());
				if (null == variable) {
					buf.append("");
				} else {
					buf.append(variable);
				}
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * 把参数src字符串中的大写替换为_小写. 例如: NotificationEvent-->_notification_event.
	 * 
	 * @param src
	 *            String
	 * @return String
	 */
	public static String relaceCapitalWith_AndLowercase(String src) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (Character.isLowerCase(c)) {// 小写字母.
				buf.append(c);
			} else if (Character.isUpperCase(c)) {// 大写字母
				buf.append('_').append(Character.toLowerCase(c));
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * 把参数src字符串中的_小写替换为大写. 例如: _notification_event-->NotificationEvent
	 * 
	 * @param src
	 *            String
	 * @return String
	 */
	public static String replace_AndLowercaseWithCapital(String src) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if ('_' == c) {
				i++;
				if (i < src.length() - 1) {
					c = src.charAt(i);
					buf.append(Character.toUpperCase(c));
				}
			} else {
				buf.append(c);
			}
		}
		return buf.toString();
	}

	/**
	 * 把key=value追加到加密/签名字符串最后.
	 * 
	 * @param buf
	 * @param key
	 * @param value
	 */
	public static void appendSignPara(StringBuffer buf, String key, String value) {
		if (!StringUtil.isEmpty(value)&&!value.equals("null")) {
			buf.append(key).append('=').append(value).append('&');
		}
	}

	/**
	 * 把key=value追加到加密/签名字符串的末尾.字符串不再继续增加新的key=value.
	 * 
	 * @param buf
	 * @param key
	 * @param value
	 */
	public static void appendLastSignPara(StringBuffer buf, String key,
			String value) {
		if (!StringUtil.isEmpty(value)) {
			buf.append(key).append('=').append(value);
		}
	}

	/**
	 * 
	 * @param buf
	 * @param key
	 * @param value
	 */
	public static void appendUrlPara(StringBuffer buf, String key, String value) {
		if (!StringUtil.isEmpty(value)) {
			try {
				buf.append(key).append('=')
						.append(URLEncoder.encode(value, "utf-8")).append('&');
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param buf
	 * @param key
	 * @param value
	 */
	public static void appendLastUrlPara(StringBuffer buf, String key,
			String value) {
		if (!StringUtil.isEmpty(value)) {
			try {
				buf.append(key).append('=')
						.append(URLEncoder.encode(value, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将所有KEY值对组到地址串中
	 * 
	 * @param buf
	 * @param key
	 * @param value
	 * @author nilomiao
	 * @since 2010-5-24
	 */
	public static void appendUrlParaForGetting(StringBuffer buf, String key,
			String value) {
		if (!StringUtil.isEmpty(value)) {
			try {
				buf.append(key).append('=')
						.append(URLEncoder.encode(value, "utf-8")).append('&');
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			buf.append(key).append('=').append('&');
		}
	}

	/**
	 * 把字符串src填充到len长度，填充的字符串为padding，填充方向为：当stuffHead为true时 填充到src头部，否则填充到尾部.
	 * 
	 * @param src
	 * @param len
	 * @param stuffHead
	 * @param padding
	 */
	public static String stuffString(String src, int len, boolean stuffHead,
			char padding) {
		if (len <= 0) {
			return src;
		}
		if (isEmpty(src)) {
			src = "";
		}
		int srcLen = src.length();
		StringBuffer buf = new StringBuffer(len);
		int paddingLen = len - srcLen;
		for (int i = 0; i < paddingLen; i++) {
			buf.append(padding);
		}
		if (stuffHead) {
			buf.append(src);
		} else {
			buf.insert(0, src);
		}
		return buf.toString();
	}
	
	/**
	 * 把字符串src填充到len长度(字节长度)，
	 * 填充的字符串为padding，填充值取字母数字空格等,一个字符串一个字节
	 * 填充方向为：当stuffHead为true时 填充到src头部，否则填充到尾部.
	 * 
	 * @param src
	 * @param len 字节长度
	 * @param stuffHead
	 * @param padding
	 */
	public static String stuffString(String src, int len, boolean stuffHead, String padding) {
		if (len <= 0) {
			return src;
		}
		if (isEmpty(src)) {
			src = "";
		}
		int srcLen = src.getBytes().length;
		StringBuffer buf = new StringBuffer(len);
		int paddingLen = len - srcLen;
		for (int i = 0; i < paddingLen; i++) {
			buf.append(padding);
		}
		if (stuffHead) {
			buf.append(src);
		} else {
			buf.insert(0, src);
		}
		return buf.toString();
	}

	/**
	 * 在数据data的前面填充上数据data的长度 长度格式为：两位长，不足两位的补0:14->14,8->08
	 * 
	 * @param data
	 * @return
	 */
	public static String padDataWithLen(String data) {
		int len = data.length();
		String lenStr = String.valueOf(len);
		if (len < 10) {
			lenStr = "0" + lenStr;
		}
		return lenStr + data;
	}

	/**
	 * 判断字符串是否全数字
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isNumber(String data) {

		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(data);
		return isNum.matches();
	}

	/**
	 * 判断字符串是否可以转换为Integer
	 * 
	 * @author Angi Wang
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断字符串是否可以转换为Long
	 * 
	 * @author Angi Wang
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isLong(String str) {
		try {
			Long.parseLong(str);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断金额格式（两位小数）
	 * 
	 */
	public static boolean isMoney(String money) {

		String[] tmpMoney = money.split("[.]");
		String tmp = "";
		for (int i = 0; i < tmpMoney.length; i++) {
			tmp += tmpMoney[i];
		}

		if (!isNumber(tmp)) {
			return false;
		}
		if (!money.contains(".")) {
			return false;
		} else {
			for (int i = 0; i < tmpMoney.length; i++) {
				if (i == 1) {
					tmp = "";
					tmp += tmpMoney[i];
				}

			}
			if (tmp.length() != 2) {
				return false;
			}
		}
		return true;
	}

	public static String parseNotifyResponse(String notifyMerchantResponse) {
		if (StringUtil.isEmpty(notifyMerchantResponse)) {
			return null;
		}
		int index1 = notifyMerchantResponse.indexOf("<pickupUrl>");
		if (index1 < 0) {
			return null;
		}
		int index2 = notifyMerchantResponse.indexOf("</pickupUrl>", index1);
		if (index2 < 0) {
			return null;
		}
		index1 += "<pickupUrl>".length();
		return notifyMerchantResponse.substring(index1, index2);
	}

	public static String stringTrim(String value) {
		if (value == null || "null".equals(value)) {
			return null;
		} else {
			return value.trim();
		}
	}

	public static String fillLLVAR(String str) {
		if (str == null || "null".equals(str))
			return null;

		String lenTemp = "";
		int length = str.length();
		if (length == 0)
			return "00";
		if (length < 10)
			lenTemp = "0";
		if (length >= 10 && length < 100)
			lenTemp = "";

		StringBuffer sb = new StringBuffer("").append(lenTemp).append(length)
				.append(str);
		return sb.toString();

	}

	public static String fillLLLVAR(String str) {
		if (str == null || "null".equals(str))
			return null;

		String lenTemp = "";
		int length = str.length();
		if (length == 0)
			return "000";
		if (length < 10)
			lenTemp = "00";
		if (length >= 10 && length < 100)
			lenTemp = "0";
		if (length >= 100 && length < 1000)
			lenTemp = "";

		StringBuffer sb = new StringBuffer("").append(lenTemp).append(length)
				.append(str);
		return sb.toString();

	}

	/**
	 * 填充字符串
	 * 
	 * @param string
	 *            被填充字符串
	 * @param filler
	 *            填充字符
	 * @param totalLength
	 *            填充后总长度
	 * @param atEnd
	 *            字符串前、后填充标志位
	 * @return
	 */
	public static String fillString(String string, char filler,
			int totalLength, boolean atEnd) {
		byte[] tempbyte = null;
		try {
			tempbyte = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		int currentLength = tempbyte.length;
		int delta = totalLength - currentLength;
		for (int i = 0; i < delta; i++) {
			if (atEnd) {
				string += filler;
			} else {
				string = filler + string;
			}
		}
		return string;
	}

	/**
	 * 取得XML文本的标签内容
	 * 
	 * @param String
	 *            strXML
	 * @param String
	 *            tagName 标签名称
	 * @return String 标签值
	 * @author nilomiao
	 * @since 2009-12-22
	 */
	public static String getTagValue(String strXML, String tagName) {
		if (null == tagName || "".equals(tagName)) {
			return "";
		}
		if (null == strXML || strXML.indexOf("<" + tagName + ">") == -1
				|| strXML.indexOf("</" + tagName + ">") == -1) {
			return "";
		}

		return strXML.substring(strXML.indexOf("<" + tagName + ">")
				+ ("<" + tagName + ">").length(),
				strXML.indexOf("</" + tagName + ">"));
	}

	/**
	 * 返回字符串字节长度（GBK编码）
	 * 
	 * @param data
	 * @return
	 */
	public static int getBytes(String data) {
		byte[] tempbyte = null;
		try {
			tempbyte = data.getBytes("gbk");
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return tempbyte.length;
	}

	/**
	 * 判断字符串是否超过最大字节数（GBK编码）
	 * 
	 * @param data
	 * @param maxLength
	 * @return false 超过 true 未超过
	 */
	public static boolean checkLength(String data, int maxLength) {
		if (getBytes(data) > maxLength)
			return false;
		return true;
	}

	/**
	 * 密码加密
	 * 
	 * @param password
	 * @return
	 */
	public static String encodePassword(String password) {
		byte[] unencodedPassword = password.getBytes();

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		md.reset();
		// call the update method one or more times
		// (useful when you don't know the size of your data, eg. stream)
		md.update(unencodedPassword);
		// now calculate the hash
		byte[] encodedPassword = md.digest();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < encodedPassword.length; i++) {
			if ((encodedPassword[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
		}
		return buf.toString();

	}

	/**
	 * 判断一个字符串是否与制定的模式匹配
	 * 
	 * @param p
	 * @param matherStr
	 * @return
	 */
	public static boolean isPatternMatcher(final Pattern p,
			final String matherStr) {
		Matcher m = p.matcher(matherStr);
		return m.matches();
	}

	
	/**
	 * 手机号掩码 1387****678
	 * @param phoneNum
	 * @return
	 */
	public static String markPhoneNum(String phoneNum){
		if(StringUtil.isEmpty(phoneNum)){
			return "";
		}
		String trimPhoneNum = phoneNum.trim();
		if(trimPhoneNum.length()>=11){
			return trimPhoneNum.substring(0, 4)+"****"+trimPhoneNum.substring(8);
		}else{
			return trimPhoneNum;
		}
	}
	
	/**
	 * 银行卡号掩码 ****0987
	 * @param bankCardNo
	 * @return
	 */
	public static String markBankCardNo(String bankCardNo){
		if(StringUtil.isEmpty(bankCardNo)){
			return "";
		}
		String trimBankCardNo = bankCardNo.trim();
		if(trimBankCardNo.length()>=4){
			return trimBankCardNo.subSequence(0, 4)+"****"+trimBankCardNo.substring(trimBankCardNo.length()-4);
		}else{
			return trimBankCardNo;
		}
	}
	
	/**
	 * 身份证号掩码 ****2111
	 * @param idNo
	 * @return
	 */
	public static String markIdNo(String idNo){
		if(StringUtil.isEmpty(idNo)){
			return "";
		}
		String trimIdNo = idNo.trim();
		if(trimIdNo.length()>=4){
			return "****"+trimIdNo.substring(trimIdNo.length()-4);
		}else{
			return trimIdNo;
		}
	}

	/**
	 * 姓名:李*
	 * @param str
	 * @return
	 */
	public static String markName(String str){
		if(str==null){
			return null;
		}
		int strLen=str.length();
		if(strLen<2){
			return str;
		}
		
		StringBuffer buff=new StringBuffer();
		
		buff.append(str.substring(0, 1));
		int lenHidden=strLen-1;
		for (int i = 0; i < lenHidden; i++) {
			buff.append("*");
		}
		return buff.toString();
	}
	
	/**
	 * 用*号替换卡号，其中字符串前面pre位、后面suf位为明文,中间部分为*号
	 * @param str
	 * @param pre
	 * @param suffix
	 * @return
	 */
	public static String replaceCardNoByStar(String str, int pre, int suf){
		if(null==str||str.length()<pre+suf){
			return "";
		}
		String temp = str.substring(pre, str.length()-suf);
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<temp.length();i++){
			sb.append("*");
		}
		return str.substring(0,pre)+sb.toString()+str.substring(str.length()-suf);
	}
	
	
	/**
	 * 银行协议号掩码6224****1234 add by lee 2014-11-11
	 * @param contranctNo
	 * @return
	 */
	public static String markContractNo(String contranctNo){
		if(StringUtil.isEmpty(contranctNo)){
			return "";
		}
		String trimConstractNo = contranctNo.trim();
		if(trimConstractNo.length()>=4){
			return trimConstractNo.subSequence(0,4)+"****"+trimConstractNo.substring(trimConstractNo.length()-4);
		}else{
			return trimConstractNo;
		}
	}
	
	
	

	
	/**
	 * 解析报文，原报文类似[N1=a][N2=b]...的格式
	 * 返回值 类似 <key=N1, value=a>..<key=N2, value=b>的map
	 * @param source 原报文 
	 * @param leftSep 即'['或'{'等左括号字符
	 * @param rightSep 即']'或'}'等右括号字符
	 * @param middleSep 即'='等示值字符 
	 * @return
	 */
	public static Map messageSeperator(String source, char leftSep,char rightSep, char middleSep){
		
		java.util.HashMap<String, String> map = new java.util.HashMap<String, String>();
		if(source == null || source.length() == 0)
			return map;
		int leftIndex = -1, rightIndex = -1;
		for(int i=0;i<source.length();i++){
			char c = source.charAt(i);
			if(c == leftSep) {
				leftIndex = i;
				continue;
			}
			if(c == rightSep) {
				if(leftIndex == -1) continue;
				rightIndex = i;
				String tmp = source.substring(leftIndex+1, rightIndex);
				String[] kv = tmp.split(String.valueOf(middleSep));
				map.put(kv[0],kv.length==1?"":kv[1]);
				leftIndex = -1;
				rightIndex = -1;
			}
		}
		
		return map;
	}
	
	/**
	 * 正则表达式数字验证 
	 * add by shijx 2013-12-23
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
          Pattern pattern = Pattern.compile("[0-9]*");
          Matcher isNum = pattern.matcher(str);
          if( !isNum.matches() )
          {
                return false;
          }
          return true;
    }
	
	public static String getCharIndexStr(String param){
		String source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer returnValue = new StringBuffer();
		for(int i=0;i<param.length();i++){
			int itemp = source.indexOf(param.charAt(i));
			String stemp = null;
			if(itemp > 25) {
				stemp = itemp - 26 > 9 ?(""+(itemp-26)):("0"+(itemp-26));
			}else{
				stemp = itemp > 9 ?(""+itemp):("0"+itemp);
			}
			returnValue.append(stemp);
		}
		return returnValue.toString();
	}

	
	public static void main(String []args){
	
	}
}
