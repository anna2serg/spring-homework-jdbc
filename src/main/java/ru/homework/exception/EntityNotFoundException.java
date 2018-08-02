package ru.homework.exception;

public class EntityNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String msg;
	
	public EntityNotFoundException(String message) {
		msg = message;
	}
	
	public String toString() {
		return "EntityNotFoundException (" + msg + ")";
	}
	
	public String getMessage() {
		return msg;
	}

}
