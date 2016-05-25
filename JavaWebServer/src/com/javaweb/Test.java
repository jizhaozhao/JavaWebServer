/**
 * 
 */
package com.javaweb;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * @author jizz
 *
 */
public class Test {
	private static int port = 8080;
	
	public static void main(String[] args){
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
			while(true){
				Socket socket = null;
				InputStream input = null;
				OutputStream output = null;
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				
				//获取请求字符串
				StringBuffer request = new StringBuffer(2048);
				int i = 0;
				byte[] buffer = new byte[2048];

				try {
					i = input.read(buffer);
				} catch (IOException e) {
					e.printStackTrace();
					i = -1;
				}
				for (int j = 0; j < i; j++) {
					request.append((char) (buffer[j]));
				}
				System.out.println("request = " + request.toString());
				
				//解析目标地址
				String uri = null;
				int index1 = 0;
				int index2 = 0;
				index1 = request.toString().indexOf(' ');
				if (index1 != -1) {
					index2 = request.toString().indexOf(' ', index1 + 1);
					if (index2 > index1) {
						uri =  request.toString().substring(index1 + 2, index2);
					}
				}
				System.out.println("uri = " + uri);
				
				//按照目标地址获取文件
				byte[] bytes = new byte[1024];
				FileInputStream fis = null;
				try {
					File file = new File("D:\\", uri);
					if (file.exists()) {
						System.out.println("file = " + file.getAbsolutePath());
						if (file.isDirectory()){
							File[] filelist = file.listFiles();
							StringBuilder show = new StringBuilder();
							show.append(("<!doctype html>" +
									"<html lang=\"en\">" +
									"<head>" +
									"<meta charset=\"UTF-8\">" +
									"<meta name=\"Generator\" content=\"EditPlus®\">" +
									"<meta name=\"Author\" content=\"\">" +
									"<title>Document</title>" +
									"</head>" +
									"<body>"));
							if ("D:\\".equals(file.getPath())){
								show.append("<h3>" + "系统根目录" +"</h3>");
							}else {
								show.append("<h3>" + "<a href=\"\\" + file.getParent().substring(3) + "\">" + "返回上级" +"</h3>");
							}
							for (File f :filelist){
								show.append("<br><a href=\"\\" + f.getAbsolutePath().substring(3) + "\">" + f.getName() + "</\"" + "</br>");
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
						}else{
							fis = new FileInputStream(file);
							int ch = fis.read(bytes, 0, 1024);
							while (ch!=-1) {
							output.write(bytes, 0, ch);
					        ch = fis.read(bytes, 0, 1024);
					        }
						}
					}
					else {
						// file not found
						String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
						"Content-Type: text/html\r\n" +
						"Content-Length: 23\r\n" +
						"\r\n" +
						"<h1>File Not Found</h1>";
						output.write(errorMessage.getBytes());
			 		}
				}
				catch (Exception e) {
					// thrown if cannot instantiate a File object
					System.out.println(e.toString() );
				}
				finally {
					if (fis!=null)
						fis.close();
				}
				socket.close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
