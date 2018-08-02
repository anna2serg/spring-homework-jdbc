package ru.homework.exception;

public class NotUniqueEntityFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public NotUniqueEntityFoundException(String message) {
		msg = message;
	}
	
	public String toString() {
		return "NotUniqueEntityFoundException (" + msg + ")";
	}
	
	public String getMessage() {
		return msg;
	}
}
