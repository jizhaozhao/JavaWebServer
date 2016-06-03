/**
 * 
 */
package com.javaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author jizz
 *
 */
public class Response {
	private static final int BUFFER_SIZE = 1024;//字节数组的大小
	private String filePath;//请求的文件路径
	private OutputStream output;//想浏览器发送的输出流

	public Response() {
		
	}
	
	public Response(OutputStream output) {
		this.output = output;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	public static int getBufferSize() {
		return BUFFER_SIZE;
	}

	public void sendStaticResource() {
		// 按照目标地址获取文件
		FileInputStream fis = null;
		try {
			File file = new File("D:\\", filePath);
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
					//存在bug，若文件大小太小，则无法读取
					fis = new FileInputStream(file);
					byte[] bytes = new byte[BUFFER_SIZE];
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
