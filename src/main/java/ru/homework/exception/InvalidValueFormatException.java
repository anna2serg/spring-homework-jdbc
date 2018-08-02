package ru.homework.exception;

public class InvalidValueFormatException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public InvalidValueFormatException(String message) {
		msg = message;
	}
	
	public String toString() {
		return "InvalidValueFormatException (" + msg + ")";
	}
	
	public String getMessage() {
		return msg;
	}
}
