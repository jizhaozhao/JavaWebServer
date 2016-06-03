/**
 * 
 */
package com.javaweb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author jizz
 *
 */
public class Request {
	private InputStream input = null;//浏览器发送的输入流
	private StringBuilder requestString = null;//获取的请求字符串
	private String uri = null;//解析得到的请求文件路径

	public Request() {
		super();
	}

	/**
	 * 参数为输入流的构造方法
	 * 给input、request和uri属性赋值
	 * @param input
	 */
	public Request(InputStream input) {
		super();
		this.input = input;
		parse();
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public StringBuilder getRequestString() {
		return requestString;
	}

	public void setRequest(StringBuilder requestString) {
		this.requestString = requestString;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * 从套接字中获取请求信息字符串
	 */
	private void parse(){
		requestString = new StringBuilder(2048);
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
			requestString.append((char) (buffer[j]));
		}
		System.out.println("request = " + requestString.toString());
		if (!"GET".equals(requestString.substring(0, 3))) {// 只处理GET请求，其他请求抛出异常
			throw new RuntimeException("请求方法需为GET");
		}
		uri = parseUri(requestString.toString());
	}
	
	/**
	 * 解析目标地址
	 * @param requestString
	 * @return
	 */
	private String parseUri(String requestString){
		String uri = null;
		int index1 = 0;
		int index2 = 0;
		index1 = requestString.indexOf(' ');
		if (index1 != -1) {
			index2 = requestString.indexOf(' ', index1 + 1);
			if (index2 > index1) {
				uri = requestString.substring(index1 + 2, index2);
			}
		}
		// System.out.println("uri = " + uri);
		String str = null;
		try {
			str = URLDecoder.decode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}// 转换字符编码，解决汉字和空格问题
		System.out.println("filepath = " + str);
		return str;
	}
}
