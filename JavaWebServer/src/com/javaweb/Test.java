package com.javaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.Iterator;

class ServerThreadCode extends Thread {
	private Socket clientSocket = null;
	private InputStream input = null;
	private OutputStream output = null;

	public ServerThreadCode() {
		super();
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

	public ServerThreadCode(Socket clientSocket) throws IOException {
		super();
		this.clientSocket = clientSocket;
		input = clientSocket.getInputStream();
		output = clientSocket.getOutputStream();

		start();

	}

	public void run() {
		// 获取请求字符串
		StringBuffer request = new StringBuffer(2048);
		int i = 0;
		byte[] buffer = new byte[2048];

		try {
			i = input.read(buffer);
		} catch (IOException e) {
			System.out.println("这里异常");
			e.printStackTrace();
			i = -1;
		}
		if (-1 == i) {// 如果没有输入，则保持在接受状态，不加这一判定下面会出现空指针异常
			return;
		}
		for (int j = 0; j < i; j++) {
			request.append((char) (buffer[j]));
		}
		System.out.println("request = " + request.toString());
		if (!"GET".equals(request.substring(0, 3))) {// 只处理GET请求，其他请求抛出异常
			throw new RuntimeException("请求方法需为GET");
		}

		// 解析目标地址
		String uri = null;
		int index1 = 0;
		int index2 = 0;
		index1 = request.toString().indexOf(' ');
		if (index1 != -1) {
			index2 = request.toString().indexOf(' ', index1 + 1);
			if (index2 > index1) {
				uri = request.toString().substring(index1 + 2, index2);
			}
		}
		// System.out.println("uri = " + uri);
		String str = null;
		try {
			str = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}// 转换字符编码，解决汉字和空格问题

		// 按照目标地址获取文件
		byte[] bytes = new byte[1024];
		FileInputStream fis = null;
		try {
			File file = new File("D:\\", str);
			if (file.exists()) {
				System.out.println("file = " + file.getAbsolutePath());
				if (file.isDirectory()) {
					File[] filelist = file.listFiles();
					StringBuffer show = new StringBuffer();
					show.append(("<!doctype html>" + "<html lang=\"en\">"
							+ "<head>" + "<meta charset=\"UTF-8\">"
							+ "<meta name=\"Generator\" content=\"EditPlus®\">"
							+ "<meta name=\"Author\" content=\"\">"
							+ "<title>Document</title>" + "</head>" + "<body>"));
					if ("D:\\".equals(file.getPath())) {
						show.append("<h3>" + "系统根目录" + "</h3>");
					} else {
						show.append("<h3>" + "<a href=\"\\"
								+ file.getParent().substring(3) + "\">"
								+ "返回上级" + "</h3>");
					}
					for (File f : filelist) {
						String eStr = f.getAbsolutePath().substring(3);
						// System.out.println("eStr = " + eStr);
						show.append("<br><a href=\"\\" + eStr + "\">"
								+ f.getName() + "</\"" + "</br>");
					}
					show.append("</body>");
					show.append("</html>");
					try {
						System.out.println(show.toString());
						output.write(show.toString().getBytes());
						output.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					fis = new FileInputStream(file);
					int ch = fis.read(bytes, 0, 1024);
					while (ch != -1) {
						output.write(bytes, 0, ch);
						ch = fis.read(bytes, 0, 1024);
					}
				}
			} else {
				// file not found
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
						+ "Content-Type: text/html\r\n"
						+ "Content-Length: 23\r\n" + "\r\n"
						+ "<h1>File Not Found</h1>";
				output.write(errorMessage.getBytes());
			}
		} catch (Exception e) {
			// thrown if cannot instantiate a File object
			System.out.println(e.toString());
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
}

class Close extends Thread {
	public static boolean state = true;

	public Close() {
		start();
	}

	public void run() {
		Scanner in = new Scanner(System.in);
		while(true){
			if ("exit".equals(in.nextLine())){
				System.out.println("haha");
				state = false;
				return;
			}
		}

	}
}

/**
 * @author jizz
 *
 */
public class Test {
	private static int port = 8080;

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 1,
					InetAddress.getByName("127.0.0.1"));
			ExecutorService fixedThreadPool = Executors.newFixedThreadPool(30);
			new Close();
			while (true) {
				if (Close.state == true) {
					Socket socket = serverSocket.accept();
					fixedThreadPool.execute(new ServerThreadCode(socket));
					Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
					System.out.println("============当前的线程有=========");
					Iterator<Thread> it = maps.keySet().iterator();
					while (it.hasNext()){
						Thread th = it.next();
						System.out.println("线程名 :" + th.getName());
						System.out.println("线程的 创建类 :" + th.getClass());
					}
					System.out.println("==============================");
				} else {
//					直接这样写会关闭正在运行的线程中的socket，导致异常
//					Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
//					Iterator<Thread> it = maps.keySet().iterator();
//					while (it.hasNext()){
//						Thread th = it.next();
//						if (th instanceof ServerThreadCode){
//							((ServerThreadCode) th).getClientSocket().close();
//							((ServerThreadCode) th).getInput().close();
//							((ServerThreadCode) th).getOutput().close();
//						}
//					}
					serverSocket.close();
					fixedThreadPool.shutdown();
					System.out.println("shutdown");
					return;
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
