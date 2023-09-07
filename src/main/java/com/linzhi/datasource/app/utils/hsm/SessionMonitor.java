package com.linzhi.datasource.app.utils.hsm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SessionMonitor extends Thread {
	private final static Logger log = LoggerFactory.getLogger(SessionMonitor.class);
	
	private static ShareHandle[] sHandle;

	public void addHandle(ShareHandle[] aHandle) {
		sHandle = aHandle;
	}

	/**
	 * @Title: testHandle
	 * @Description: TODO(这方法是用于长连接时的心跳？？？？？？107 )
	 * @date 2013-11-21 下午03:51:09
	 * @author yang-lj
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void testHandle() {
		byte[] tmpBuffout = new byte[1];
		byte[] tmpBuffin = new byte[128];
		for (int i = 0; i < sHandle.length; i++) {
			synchronized (sHandle) {
				if (sHandle[i].isUsable()) {
					try {
						sHandle[i].setUsed();
						sHandle[i].write(tmpBuffout, 1);
						sHandle[i].read(tmpBuffin, 128);
						sHandle[i].setNotused();
					} catch (Exception e) {
						log.error("____107____加密机连接出错 err:" + e.getMessage());
						sHandle[i].setFault();
					}
				}
			}
		}
	}

	public void run() {
		while (true) {
			try {
				testHandle();
				// System.out.println("SessionMonitor::run()");
				for (int i = 0; i < sHandle.length; i++) {
					if (sHandle[i].isFault()) {
						log.info("____107____加密机重连...........begin");
						sHandle[i].releaseSocketHandle();
						sHandle[i].connect();
						log.info("____107____加密机重连...........end");
					}
				}
				sleep(10000);
			} catch (Exception e) {
				System.out.println("SessionMonitor::run() - " + e.getMessage());
			} catch (Error err) {
				System.out.println("SessionMonitor::run() - " + err.getMessage());
			}
		}
	}
}
