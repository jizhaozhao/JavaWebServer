/**
 * 
 */
package com.javaweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author jizz
 *
 */
public class ServerThread extends Thread {
	private Socket clientSocket = null;
	private InputStream input = null;
	private OutputStream output = null;
	public ServerThread() {
		super();
	}
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
			Response response = new Response(output);
			response.setFilePath(request.getUri());
			response.sendStaticResource();
		} else {
			return;
		}
	}

}
