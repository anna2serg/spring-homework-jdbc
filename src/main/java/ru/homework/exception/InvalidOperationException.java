package ru.homework.exception;

public class InvalidOperationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public InvalidOperationException(String message) {
		msg = message;
	}
	
	public String toString() {
		return "InvalidOperationException (" + msg + ")";
	}
	
	public String getMessage() {
		return msg;
	}
}
