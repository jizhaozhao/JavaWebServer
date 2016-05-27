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
import java.net.URLDecoder;
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
					System.out.println("这里异常");
					e.printStackTrace();
					i = -1;
				}
				if (-1 == i){//如果没有输入，则保持在接受状态，不加这一判定下面会出现空指针异常
					continue;
				}
				for (int j = 0; j < i; j++) {
					request.append((char) (buffer[j]));
				}
				System.out.println("request = " + request.toString());
				if ( !"GET".equals(request.substring(0, 3)) ){
					throw new RuntimeException("请求方法需为GET");
				}
				
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
//				System.out.println("uri = " + uri);
				String str = URLDecoder.decode(uri, "UTF-8");//转换字符编码，解决汉字和空格问题
//				System.out.println("str = " + str);
				
				//按照目标地址获取文件
				byte[] bytes = new byte[1024];
				FileInputStream fis = null;
				try {
					File file = new File("D:\\", str);
					if (file.exists()) {
//						System.out.println("file = " + file.getAbsolutePath());
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
								String eStr = f.getAbsolutePath().substring(3);
//								System.out.println("eStr = " + eStr);
								show.append("<br><a href=\"\\" + eStr + "\">" + f.getName() + "</\"" + "</br>");
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
//				Scanner in = new Scanner(System.in);
//				if (in.hasNext()){
//					if ("exit".equals(in.nextLine())){
//						input.close();
//						output.close();
//						socket.close();
//						return;
//					}
//				}else
//					continue;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
