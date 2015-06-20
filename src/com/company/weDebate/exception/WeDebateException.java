package com.company.weDebate.exception;

/**
 * 
 * 描述：公共异常类
 */
public class WeDebateException extends Exception {
	//网络不可用异常
	public static final int NETWORK_OFF_EXCEPTION = 0x01;
	
	//服务器错误异常
	public static final int HOST_ERROR_EXCEPTION = 0x01;
	
	//连接超时异常
	public static final int CONNECT_TIMEOUT_EXCEPTION = 0x02;
	
	//Socket超时异常
	public static final int SOCKET_TIMEOUT_EXCEPTION = 0x03;
	
	private int statusCode = -1;

	public WeDebateException(String msg) {
		super(msg);
	}

	public WeDebateException(Exception cause) {
		super(cause);
	}

	public WeDebateException(String msg, int statusCode) {
		super(msg);
		this.statusCode = statusCode;
	}

	public WeDebateException(String msg, Exception cause) {
		super(msg, cause);
	}

	public WeDebateException(String msg, Exception cause, int statusCode) {
		super(msg, cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return this.statusCode;
	}

	public WeDebateException() {
		super();
	}

	public WeDebateException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WeDebateException(Throwable throwable) {
		super(throwable);
	}

	public WeDebateException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
}
