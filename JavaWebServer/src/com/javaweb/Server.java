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

/**
 * @author jizz
 *
 */
public class Server {
	private static int port = 8080;
	
	public static class Close extends Thread {
		public static boolean state = true;

		
		public boolean isState() {
			return state;
		}

		public void setState(boolean state) {
			this.state = state;
		}

		public Close() {
		}

		public void run() {
			Scanner in = new Scanner(System.in);
			while(true){
				if ("exit".equals(in.nextLine())){
					System.out.println("haha");
					state = false;
					in.close();
					return;
				}
			}

		}
	}
	
	public static void main(String[] args) {
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
				System.out.println("shutdown");
				return;
			}
			
		}
	}
	
	
	
	public void open() {}
	public void shutdown() {}
	public void lietenning() {}
	public void parse() {}
	public void execute() {}
}
