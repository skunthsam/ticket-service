package com.walmart.ticketservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.context.request.async.DeferredResult;

import com.walmart.ticketservice.database.model.SeatHold;
import com.walmart.ticketservice.exception.HoldExpiredException;
import com.walmart.ticketservice.exception.InvalidDataException;
import com.walmart.ticketservice.exception.NotEnoughSeatsException;
import com.walmart.ticketservice.service.TicketService;

/**
 * @author Satya Service Controller process the customer requests
 */

@RestController
@RequestMapping(value = "/ticket-service")
public class ServiceController {

	@Autowired
	private TicketService ticketService;

	@RequestMapping(value = "/available-seats", method = RequestMethod.GET)
	public DeferredResult<Integer> getNumofAvailableSeats() {
		DeferredResult<Integer> result = new DeferredResult<>();

		Integer numOfAvailSeats = ticketService.numSeatsAvailable();
		result.setResult(numOfAvailSeats);
		return result;
	}

	@RequestMapping(value = "/hold-seats/{numSeats}/{customerEmail:.+}", method = RequestMethod.POST)
	public DeferredResult<SeatHold> holdSeats(@PathVariable Integer numSeats, @PathVariable String customerEmail) {

		DeferredResult<SeatHold> result = new DeferredResult<>();
		SeatHold seatHold = null;
		try {
			seatHold = ticketService.findAndHoldSeats(numSeats, customerEmail);
		} catch (InvalidDataException | NotEnoughSeatsException e ) {
			seatHold = new SeatHold();
			seatHold.setErrorMessage(e.getMessage());
		}
		result.setResult(seatHold);
		return result;
	}

	@RequestMapping(value = "/reserve-seats/{holdId}/{customerEmail:.+}", method = RequestMethod.POST)
	public DeferredResult<String> reserveSeats(@PathVariable Integer holdId, @PathVariable String customerEmail) {
		DeferredResult<String> result = new DeferredResult<>();

		String seatsReserved;
		try {
			seatsReserved = ticketService.reserveSeats(holdId, customerEmail);
			result.setResult(seatsReserved);
		} catch (InvalidDataException | HoldExpiredException e) {
			result.setResult(e.getMessage());
		}
		return result;
	}

}
