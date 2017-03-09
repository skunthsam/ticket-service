package com.walmart.ticketservice.exception;

public class InvalidDataException extends RuntimeException {

	private static final long serialVersionUID = 89895327840813620L;

	public InvalidDataException() {

	}

	public InvalidDataException(String message) {
		super(message);

	}

}
