package com.linzhi.datasource.app.utils.hsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ShareHandle {
	private final static Logger log = LoggerFactory.getLogger(ShareHandle.class);

	public final int FLAG_NOTUSE = 0;// 未使用

	public final int FLAG_USED = 1;// 已使用

	public final int FLAG_FAULT = 2;// 错误

	private Socket iSocketHandle = null;

	private int iStatus;

	private InputStream iInputStream = null;

	private OutputStream iOutputStream = null;

	private String iIP = null;

	private int iPort = -1;

	private int iTimeout = -1;

	/**
	 * @param aString
	 * @param aPort
	 * @param aTimeOut
	 */
	public ShareHandle(String aIP, int aPort, int aTimeout) {
		iIP = aIP;
		iPort = aPort;
		iTimeout = aTimeout;
		connect();
	}

	public void connect() {
		try {
			iSocketHandle = new Socket();
			InetSocketAddress hsmAddr = new InetSocketAddress(iIP, iPort);
			iSocketHandle.connect(hsmAddr, iTimeout);
			iSocketHandle.setSoTimeout(iTimeout);
			iSocketHandle.setKeepAlive(true);
			iSocketHandle.setReceiveBufferSize(2048);
			iSocketHandle.setTcpNoDelay(true);
			iInputStream = iSocketHandle.getInputStream();
			iOutputStream = iSocketHandle.getOutputStream();
			setNotused();
		} catch (IOException e) {
			releaseSocketHandle();
		}
	}

	public void releaseSocketHandle() {
		log.info("释放资源........begin");
		setFault();

		if (iInputStream != null) {
			try {
				iInputStream.close();
			} catch (Exception e) {
			}
			iInputStream = null;
		}

		if (iOutputStream != null) {
			try {
				iOutputStream.close();
			} catch (Exception e) {
			}
			iOutputStream = null;
		}

		if (iSocketHandle != null) {
			try {
				iSocketHandle.close();
			} catch (Exception e) {
			}
			iSocketHandle = null;
		}
		log.info("释放资源........end");
	}

	public void setUsed() {
		iStatus = FLAG_USED;
	}

	public void setNotused() {
		iStatus = FLAG_NOTUSE;
	}

	public void setFault() {
		// （如果发生在重连过程当中，怱略该输出）
		log.error("加密机连接发生错误！设置状态iStatus=2");
		iStatus = FLAG_FAULT;
	}

	public int getStatus() {
		return iStatus;
	}

	public boolean isUsed() {
		return (iStatus == FLAG_USED);
	}

	public boolean isUsable() {
		return (iStatus == FLAG_NOTUSE);
	}

	public boolean isFault() {
		return (iStatus == FLAG_FAULT);
	}

	/**
	 * @param aByteOut
	 * @param aI
	 * @param aLength
	 * @throws IOException
	 */
	public void write(byte[] aByteOut, int aLength) throws IOException {
		iOutputStream.write(aByteOut, 0, aLength);
	}

	/**
	 * @param aByteIn
	 * @param aI
	 * @throws IOException
	 */
	public int read(byte[] aByteIn, int aBufferSize) throws IOException {
		int tReadLen = iInputStream.read(aByteIn, 0, aBufferSize);

		return tReadLen;
	}
}
