/**
 * 
 */
package com.javaweb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jizz
 *
 */
public class Response {
	private static Logger log = LoggerFactory.getLogger(Response.class);
	private String filePath;// 请求的文件路径
	private OutputStream output;// 想浏览器发送的输出流

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

	public void showFileList(File file) {
		File[] filelist = file.listFiles();
		StringBuffer show = new StringBuffer();
		show.append(("<!doctype html><html lang=\"en\">"
				+ "<head><meta charset=\"UTF-8\">"
				+ "<meta name=\"Generator\" content=\"EditPlus®\">"
				+ "<meta name=\"Author\" content=\"\">"
				+ "<title>Document</title></head><body>"));
		if ("D:\\".equals(file.getPath())) {
			show.append("<h3>系统根目录</h3>");
		} else {
			show.append("<h3><a href=\"\\" + file.getParent().substring(3)
					+ "\">返回上级</h3>");
		}
		for (File f : filelist) {
			String eStr = f.getAbsolutePath().substring(3);
			show.append("<br><a href=\"\\" + eStr + "\">" + f.getName());
		}
		show.append("</body></html>");
		try {
			output.write(show.toString().getBytes());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	public void showFile(FileInputStream fis, File file) throws IOException {
		output.write("HTTP/1.1 200 OK\n".getBytes());
		output.write("MIME_version:1.0\n".getBytes());
		output.write(("Content-Length:" + file.length() + "\n").getBytes());
		output.write(("Content_Type:" + getContentType(file.getName()) + "\n").getBytes());
		output.write("\n".getBytes());
		output.flush();
		fis = new FileInputStream(file);
		int fileSize = (int) file.length();
		byte[] bytes = new byte[fileSize];
		fis.read(bytes, 0, fileSize);
		
		output.write(bytes);
		output.flush();
		fis.close();
		log.debug("input");
	}

	public String getContentType(String name){
		if (name.endsWith(".html") || name.endsWith(".htm")){
			return "text/html";
		} else if (name.endsWith(".txt") || name.endsWith(".java")){
			return "text/plain";
		} else if (name.endsWith(".gif")){
			return "image/gif";
		} else if (name.endsWith(".class")){
			return "application/octet-stream";
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")){
			return "image/jpeg";
		} else if (name.endsWith("pdf")){
			return "application/pfd";
		} else{
			return "text/plain";
		}
			
	}
	
	public void showFileNotFound() throws IOException {
		// file not found
		String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
				+ "Content-Type: text/html\r\n" + "Content-Length: 23\r\n"
				+ "\r\n" + "<h1>File Not Found</h1>";
		output.write(errorMessage.getBytes());
		output.flush();
		log.warn("404 not found");
	}

	public void sendStaticResource() {
		// 按照目标地址获取文件
		FileInputStream fis = null;
		try {
			File file = new File("D:\\", filePath);
			if (file.exists()) {
				if (file.isDirectory()) {
					showFileList(file);
				} else {
					showFile(fis, file);
				}
			} else {
				showFileNotFound();
			}
		} catch (Exception e) {
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
