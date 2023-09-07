package com.linzhi.datasource.app.utils.hsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmSession {
	private final static Logger log = LoggerFactory.getLogger(HsmSession.class);

	private static final int ERR_CONFIG_FILE = 0x00000008;

	private static final int ERR_CONNECT_HSM = 0x0000000a;

	private static final int ERR_SENDTO_HSM = 0x0000000c;

	private static final int ERR_RECVFORM_HSM = 0x0000000d;

	private static final int ERR_SESSION_END = 0x00000020;

	private static final int ERR_HANDLE_FAULT = 0x00000030;

	private static ShareHandle[] sHandles;

	private static String[] sIPs;

	private static int sHsmNumber;

	private static int sBalance;

	private static int sPort;

	private static int sTimeOut;

	private static String sProfileFile;

	private static int sPreIndex = -1;

	private int iCurrentIndex = -1;

	private int iLastErr = -1;

	/*
	 * private String aProfileFile;
	 * 
	 * public void setaProfileFile(String aProfileFile) {
	 * log.info("-------------------aProfileFile:"+aProfileFile);
	 * this.aProfileFile = aProfileFile; }
	 */

	// 创建或获取连接
	public HsmSession(int sHsmNumber, int sBalance, int sPort, int sTimeOut, String HSM01_1,
			String HSM01_2) {
		this.sHsmNumber = sHsmNumber;
		this.sBalance = sBalance;
		this.sPort = sPort;
		this.sTimeOut = sTimeOut;
		sIPs = new String[sHsmNumber];
		sIPs[0] = HSM01_1;
		sIPs[1] = HSM01_2;

		iLastErr = 0;
		iCurrentIndex = -1;
		log.info("-----------------HsmSession01 构造");
		// 1、初始化加密机连接
		try {
			initial();
		} catch (Exception e) {
			iLastErr = ERR_CONFIG_FILE;
			e.printStackTrace();
			return;
		}
		// 20131122 107 update ------iCurrentIndex------------begin
		/*
		 * // 2：获取可用连接 for (int loop = 0; loop < (sTimeOut / 20); loop++) {
		 * iCurrentIndex = getSession(); if (iCurrentIndex != -1) break; try {
		 * Thread.sleep(20); } catch (InterruptedException e) {
		 * e.printStackTrace(); break; } }
		 * 
		 * // 3：无法获取连接情况下，置错误号 if (iCurrentIndex == -1) iLastErr =
		 * ERR_HANDLE_FAULT;
		 */
		// 20131122 107 update ------------------end
	}

	public static synchronized void initial() throws Exception {
		log.info("-----------------initial_01 初始化");
		SessionMonitor sSessionMonitor = new SessionMonitor();
		int i, j, nError;
		String str, sDigit;
		nError = 0;

		// 1、判断是否已经初始化
		// if (sHandles != null)
		// return;

		// 2、获取配制文件
		try {
			// sHsmNumber =
			// Integer.parseInt(PlatformConfigInfo.encryptionMachine.get("HSM01_NUMBER").toString());
			// sBalance =
			// Integer.parseInt(PlatformConfigInfo.encryptionMachine.get("HSM01_BALANCE").toString());
			// sPort =
			// Integer.parseInt(PlatformConfigInfo.encryptionMachine.get("HSM01_PORT").toString());
			// sTimeOut =
			// Integer.parseInt(PlatformConfigInfo.encryptionMachine.get("HSM01_TIMEOUT").toString());
			// sIPs = new String[sHsmNumber];
			// sIPs[0] =
			// PlatformConfigInfo.encryptionMachine.get("HSM01_1").toString();
			// sIPs[1] =
			// PlatformConfigInfo.encryptionMachine.get("HSM01_2").toString();
			log.info("--sHsmNumber:" + sHsmNumber);
			log.info("--sBalance:" + sBalance);
			log.info("--sPort:" + sPort);
			log.info("--sTimeOut:" + sTimeOut);
			log.info("--sIPs0:" + sIPs[0]);
			log.info("--sIPs1:" + sIPs[1]);
		} catch (Exception e) {
			throw new Exception("初始化加密机出错！" + e.getMessage());
		}

		// 3、初始化连接池，每加密机依次建立一个连接，以实现负载均衡
		ShareHandle[] tHandle = new ShareHandle[sHsmNumber * sBalance];
		for (i = 0; i < sBalance; i++) {
			for (j = 0; j < sHsmNumber; j++) {
				tHandle[i * sHsmNumber + j] = new ShareHandle(sIPs[j], sPort, sTimeOut);
				if (tHandle[i * sHsmNumber + j].isFault())
					nError++;
			}
		}

		if (nError == sHsmNumber * sBalance) {
			throw new Exception("无法与加密机建立连接。");
		}

		// 4、启动连接恢复线程
		sHandles = tHandle;
		sSessionMonitor.addHandle(sHandles);
		sSessionMonitor.start();
	}

	public static String getHsmSessionInfo() {
		StringBuffer tInfo = new StringBuffer();
		tInfo.append("Initial File = [" + sProfileFile + "]<br>")
				.append("Balance = [" + sBalance + "]<br>")
				.append("Number = [" + sHsmNumber + "]<br>").append("IP = [" + sIPs[0] + "]<br>")
				.append("Port = [" + sPort + "]<br>").append("Timeout = [" + sTimeOut + "]<br>")
				.append("Handle = [" + sHandles + "]<br>").append("Connection Info:<br>\n");

		for (int i = 0; i < sBalance * sHsmNumber; i++) {
			ShareHandle tHandle = sHandles[i];
			tInfo.append("Connection[" + i + "] Status is " + tHandle.getStatus() + "\n");
		}

		return tInfo.toString();
	}

	private static synchronized int getSession() {
		int i = 0;
		boolean tStatus = false;
		int tNumOfSession = sHsmNumber * sBalance;

		for (i = sPreIndex + 1; i < tNumOfSession; i++) {
			ShareHandle tHandle = sHandles[i];
			if (tHandle.isUsable()) {
				tHandle.setUsed();
				sPreIndex = i;
				tStatus = true;
				break;
			}
		}

		if (!tStatus) {
			for (i = 0; i < sPreIndex; i++) {
				ShareHandle tHandle = sHandles[i];
				if (tHandle.isUsable()) {
					tHandle.setUsed();
					sPreIndex = i;
					tStatus = true;
					break;
				}
			}
		}

		if (!tStatus)
			i = -1;

		return i;
	}

	public int GetPortConf() {
		return sPort;
	}

	public int GetLastError() {
		return iLastErr;
	}

	public void SetErrCode(int nErrCode) {
		iLastErr = nErrCode;
		return;
	}

	public String ParseErrCode(int nErrCode) {
		String sMeaning;
		switch (nErrCode) {
		case 0:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":操作正确,状态正常";
			break;
		case ERR_CONFIG_FILE:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":配置文件异常";
			break;
		case ERR_CONNECT_HSM:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":连接密码机失败";
			break;
		case ERR_SENDTO_HSM:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":发送数据至密码机失败";
			break;
		case ERR_RECVFORM_HSM:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":接收密码机数据失败";
			break;
		case ERR_SESSION_END:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":连接已关闭";
			break;
		case ERR_HANDLE_FAULT:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":连接句柄状态异常";
			break;
		default:
			sMeaning = "0x" + Integer.toHexString(nErrCode) + ":异常操作,检查密码机日志";
			break;
		}
		return sMeaning;
	}

	// 发送数据到加密机
	// public int SendData(byte[] byteOut, int nLength) {//20131122 107 update
	// iCurrentIndex
	public int SendData(byte[] byteOut, int nLength, int iCurrentIndex) {
		ShareHandle tHandle = sHandles[iCurrentIndex];

		if (tHandle.isFault()) {
			iLastErr = ERR_HANDLE_FAULT;
			return iLastErr;
		}

		try {
			tHandle.write(byteOut, nLength);
			iLastErr = 0;
		} catch (Exception e) {
			tHandle.setFault();
			// sSessionMonitor.testHandle();
			iLastErr = ERR_SENDTO_HSM;
			log.error("HsmSession::SendData() - " + e.getMessage());
		} catch (Error err) {
			tHandle.setFault();
			// sSessionMonitor.testHandle();
			iLastErr = ERR_SENDTO_HSM;
			log.error("HsmSession::SendData() - " + err.getMessage());
		}

		return iLastErr;
	}

	// 从加密机接收数据
	// public int RecvData(byte[] byteIn) {//20131122 107 update iCurrentIndex
	public int RecvData(byte[] byteIn, int iCurrentIndex) {
		ShareHandle tHandle = sHandles[iCurrentIndex];
		if (tHandle.isFault()) {
			iLastErr = ERR_HANDLE_FAULT;
			return iLastErr;
		}

		try {
			tHandle.read(byteIn, 2048);
			// 成功应答码'A'
			if (byteIn[0] != 'A') {
				// iLastErr = byteIn[9] & 0xff;
				iLastErr = ERR_HANDLE_FAULT;
			} else {
				iLastErr = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tHandle.setFault();
			// sSessionMonitor.testHandle();
			iLastErr = ERR_RECVFORM_HSM;
			log.error("HsmSession::RecvData() - " + e.getMessage());
		} catch (Error err) {
			err.printStackTrace();
			tHandle.setFault();
			// sSessionMonitor.testHandle();
			iLastErr = ERR_RECVFORM_HSM;
			log.error("HsmSession::RecvData() - " + err.getMessage());
		}
		return iLastErr;
	}

	// public int HsmSessionEnd() {//20131122 107 update iCurrentIndex
	public int HsmSessionEnd(int iCurrentIndex) {
		if (iCurrentIndex != -1) {
			ShareHandle tHandle = sHandles[iCurrentIndex];
			if (tHandle.isUsed()) {
				tHandle.setNotused();
				iLastErr = 0;
			}
		}
		return iLastErr;
	}

	/**
	 * @Title: getAvailableIndex
	 * @Description: TODO(获取可用连接) iCurrentIndex
	 * @date 2013-11-22 下午04:23:16
	 * @author yang-lj
	 * @param @return 设定文件
	 * @return int 返回类型
	 * @throws
	 */
	public int getAvailableIndex() {
		int iCurrentIndex = -1;
		// 2：获取可用连接
		for (int loop = 0; loop < (sTimeOut / 20); loop++) {
			iCurrentIndex = getSession();
			if (iCurrentIndex != -1)
				break;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}

		// 3：无法获取连接情况下，置错误号
		if (iCurrentIndex == -1)
			iLastErr = ERR_HANDLE_FAULT;
		return iCurrentIndex;
	}

}
