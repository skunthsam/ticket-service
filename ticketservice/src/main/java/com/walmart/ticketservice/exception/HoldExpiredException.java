package com.walmart.ticketservice.exception;

public class HoldExpiredException  extends RuntimeException  {
	
	private static final long serialVersionUID = 12335327840813620L;

	public HoldExpiredException() {	
	}
	
	public HoldExpiredException(String message) {
		super(message);
	}

}
