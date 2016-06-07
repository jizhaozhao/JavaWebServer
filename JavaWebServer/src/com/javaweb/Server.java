/**
 * 
 */
package com.javaweb;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jizz
 *
 */
public class Server {
	private static Logger log = LoggerFactory.getLogger(Server.class);
	private static int port = 8080;
	
	/**
	 * @author jizz
	 * 内部类Colse为守护线程
	 * 用来判断控制台输入
	 * 控制台输入为exit时程序退出
	 */
	public static class Close extends Thread {
		public static boolean state = true;//服务器当前状态，默认为开

		
		public boolean isState() {
			return state;
		}

		public void setState(boolean state) {
			Close.state = state;
		}

		public Close() {
		}

		public void run() {
			Scanner in = new Scanner(System.in);
			while(true){
				if ("exit".equals(in.nextLine())){
					log.info("server will be closed");
					state = false;
					in.close();
					return;
				}
			}

		}
	}
	
	public static void main(String[] args) {
		log.info(" server start");
		ServerSocket serverSocket = null;
		
		try {
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);
		
		Close close = new Close();
		close.setDaemon(true);
		close.start();
		
		
		while(true) {
			if (close.isState() == true) {
				Socket socket;
				try {
					socket = serverSocket.accept();
					fixedThreadPool.execute(new ServerThread(socket));
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				fixedThreadPool.shutdown();
				log.info("server shutdown");
				return;
			}
			
		}
	}
	
	
	
//	public void open() {}
//	public void shutdown() {}
//	public void lietenning() {}
//	public void parse() {}
//	public void execute() {}
}
