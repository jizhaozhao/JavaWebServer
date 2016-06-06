/**
 * 
 */
package com.javaweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jizz
 *
 */
public class ServerThread extends Thread {
	private static Logger log = LoggerFactory.getLogger(ServerThread.class);
	
	private Socket clientSocket = null;
	private InputStream input = null;
	private OutputStream output = null;
	public ServerThread() {
		super();
	}
	/**
	 * @param clientSocket
	 * @throws IOException
	 * 在ServerThread的构造方法中，获取客户端socket
	 * 并且根据客户端socket获取到输入输出流
	 * 开启执行线程
	 */
	public ServerThread(Socket clientSocket) throws IOException {
		super();
		this.clientSocket = clientSocket;
		this.input = clientSocket.getInputStream();
		this.output = clientSocket.getOutputStream();
		start();
	}
	public Socket getClientSocket() {
		return clientSocket;
	}
	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	public InputStream getInput() {
		return input;
	}
	public void setInput(InputStream input) {
		this.input = input;
	}
	public OutputStream getOutput() {
		return output;
	}
	public void setOutput(OutputStream output) {
		this.output = output;
	}
	
	public void run() {
		Request request = new Request(input);
		if (null != request.getUri()){
			log.info("socket start");
			Response response = new Response(output);
			response.setFilePath(request.getUri());
			response.sendStaticResource();
			try {
				//半关闭Socket，关闭Socket的输入输出流，但是不关闭Socket，该Socket继续保持连接
				clientSocket.shutdownInput();
				clientSocket.shutdownOutput();
				log.info("socket shutdown");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

}
