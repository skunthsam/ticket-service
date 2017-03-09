package com.walmart.ticketservice.exception;

public class NotEnoughSeatsException extends RuntimeException {

	private static final long serialVersionUID = 87136973384382L;

	public NotEnoughSeatsException() {

	}

	public NotEnoughSeatsException(String message) {
		super(message);

	}

}
