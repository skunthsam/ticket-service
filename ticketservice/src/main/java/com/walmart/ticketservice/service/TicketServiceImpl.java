package com.walmart.ticketservice.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import com.walmart.TicketserviceApplication;
import com.walmart.ticketservice.Constants;
import com.walmart.ticketservice.database.VenueSeatingRepository;
import com.walmart.ticketservice.database.model.SeatHold;
import com.walmart.ticketservice.database.model.Seats;
import com.walmart.ticketservice.exception.HoldExpiredException;
import com.walmart.ticketservice.exception.InvalidDataException;
import com.walmart.ticketservice.exception.NotEnoughSeatsException;

/**
 * @author Satya
 *
 */

@Service("ticketService")
@Transactional(value = "transactionManager", isolation = Isolation.SERIALIZABLE)
public class TicketServiceImpl implements TicketService {

	private static Logger log = Logger.getLogger(TicketserviceApplication.class);

	private VenueSeatingRepository venueSeatingRepository;

	@Autowired
	public void setVenueSeatingRepository(VenueSeatingRepository venueSeatingRepository) {
		this.venueSeatingRepository = venueSeatingRepository;
	}

	/**
	 * The number of seats in the Venue that are neither held nor reserved
	 *
	 * @return the number of tickets available in the Venue
	 */

	@Override
	public int numSeatsAvailable() {

		int size = venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS).size();
		log.info("numSeatsAvailable : " + size);
		return size;
	}

	/**
	 * Find and hold the best available seats for a customer If Not enough seats
	 * available, sends message "Not Enough Seats Available or hold the seats
	 * and send the information to customer.
	 *
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information
	 */
	@Override
	public SeatHold findAndHoldSeats(int numSeats, String customerEmail) {
		SeatHold seatHold = null;
		if (numSeats <= 0 || StringUtils.isEmpty(customerEmail)) {
			log.info("findAndHoldSeats :: Invalid Input Data recieved");
			throw new InvalidDataException("Invalid Input Data");
		}
		if (numSeats > numSeatsAvailable()) {
			log.info("findAndHoldSeats ::  Not Enought Seats available");
			throw new NotEnoughSeatsException("Not Enough Seats Available");
		}

		seatHold = holdSeats(numSeats, customerEmail);
		log.info("findAndHoldSeats :: Seats are held for the customer");
		return seatHold;
	}

	/**
	 * Commit seats held for a specific customer
	 *
	 * @param seatHoldId
	 *            the seat hold identifier
	 * @param customerEmail
	 *            the email address of the customer to which the seat hold is
	 *            assigned
	 * @return a reservation confirmation code
	 * 
	 */

	@Override
	public String reserveSeats(int seatHoldId, String customerEmail) {

		if (seatHoldId <= 0 || StringUtils.isEmpty(customerEmail)) {
			log.info("reserveSeats :: Invalid Input Data recieved");
			throw new InvalidDataException("Invalid Input Data");
		}

		List<Seats> heldSeats = venueSeatingRepository.findAllByHoldidAndEmail(seatHoldId, customerEmail);
		if (heldSeats.isEmpty()) {
			log.info("reserveSeats :: Hold Expired");
			throw new HoldExpiredException("Hold is expired, please try again");
		} else {
			List<Seats> reservedSeats = new ArrayList<>();
			String resConfirmCode = customerEmail.substring(0, 4) + seatHoldId;

			for (Seats seat : heldSeats) {
				seat.setStatus(Constants.RESERVED_STATUS);
				seat.setEmail(customerEmail);
				seat.setResconfirmcode(resConfirmCode);
				seat.setUpdated(new Timestamp(System.currentTimeMillis()));
				seat.setHolduntil(null);

				reservedSeats.add(seat);
			}
			venueSeatingRepository.save(heldSeats);
			log.info("reserveSeats :: " + resConfirmCode);
			return resConfirmCode;
		}

	}

	/**
	 * Finds the best available seats for a customer and holds them. Update the
	 * database to hold the seats and return Seats information.
	 * 
	 * @param numSeats
	 *            the number of seats to find the best seats
	 * @param customerEmail
	 *            customer email sent by user
	 * @return a List of Seats of best available seats
	 */

	private synchronized SeatHold holdSeats(int numSeats, String customerEmail) {

		// Find best available seats
		List<Seats> subList = findBestAvailableSeats(numSeats);

		List<String> list = new ArrayList<>();

		// Generate a unique random Hold Id
		int holdid = new Random().nextInt(100000);

		log.info("Hold Id Generated : " + holdid);
		Timestamp updatedtimestamp = new Timestamp(System.currentTimeMillis());
		Timestamp holdUnitltimestamp = new Timestamp(System.currentTimeMillis() + Constants.HOLD_TIME);

		List<Seats> selectedSeats = new ArrayList<>();

		// Update the Hold Seats in Database
		for (Seats seat : subList) {
			seat.setStatus(Constants.HOLD_STATUS);
			seat.setEmail(customerEmail);
			seat.setHoldid(holdid);
			// Set Hold Time
			seat.setUpdated(updatedtimestamp);
			seat.setHolduntil(holdUnitltimestamp);

			selectedSeats.add(seat);
			list.add(seat.getRowid());
		}
		venueSeatingRepository.save(selectedSeats);

		SeatHold seatHold = new SeatHold();
		seatHold.setSeatIds(list);
		seatHold.setHoldid(holdid);
		seatHold.setIsholdSuccess(true);
		// set current time
		seatHold.setReservedTime(new SimpleDateFormat("MM/DD/YYYY").format(updatedtimestamp));
		seatHold.setEmail(customerEmail);
		return seatHold;
	}

	/**
	 * Finds the best available seats for a customer
	 * 
	 * This is based on the ROW , Seats in First ROW is best preference and last
	 * row are the least preference (This can modified to set based on
	 * preference by ROW AND/SEATS - the logic need to be modified)
	 * 
	 * First (front) ROW is assigned with row preference =1 , second row with
	 * row preference =2 and so on., find the number of available seats in a
	 * single row starting from First Row and if not, find the best available
	 * seats in consecutive Rows. Otherwise, find all available seats starting
	 * from Row A, Row B and so on.,
	 * 
	 * List <Seats> of Available seats converted to Map <RowId, List<Seats>> -
	 * map to store Row id and Seats in each row. Iterate thru to the Map and
	 * find the best seats available.
	 * 
	 * 
	 * @param numSeats
	 *            the number of seats to find the best seats
	 * @return a List of Seats of best available seats
	 */
	private List<Seats> findBestAvailableSeats(int numSeats) {

		List<Seats> availableSeats = venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS);

		Map<String, List<Seats>> seatsMap = new HashMap<>();

		if (availableSeats.size() < numSeats) {
			log.info("findBestAvailableSeats ::  Not Enought Seats available");
			throw new NotEnoughSeatsException("Not Enough Seats Available");
		} else if (availableSeats.size() == numSeats) {
			return availableSeats;
		} else {
			// Converting the List <Seats> to Map <Rowid, List<Seats>>
			for (Seats seat : availableSeats) {
				String rowid = seat.getRowid();

				List<Seats> slist = seatsMap.get(rowid);
				if (slist == null) {
					slist = new ArrayList<>();
				}
				slist.add(seat);
				seatsMap.put(rowid, slist);
			}
		}

		// Find if the seats are in single row., if Yes return the list.
		Iterator<String> keys = seatsMap.keySet().iterator();

		List<Seats> listSeats = new ArrayList<>();
		for (; keys.hasNext();) {
			String rowId = keys.next();
			List<Seats> listValues = seatsMap.get(rowId);
			if (listValues.size() >= numSeats) {
				return listValues.subList(0, numSeats);
			}
		}
		keys = seatsMap.keySet().iterator();
		List<Seats> prevList = new ArrayList<>();
		// Find if the seats are not in single row., then find consecutive row.
		// or Find the select available seats starting from Row A
		for (; keys.hasNext();) {

			String rowId = keys.next();
			List<Seats> listValues = seatsMap.get(rowId);
			if (prevList.size() + listValues.size() >= numSeats) {

				listValues.addAll(prevList);
				return listValues.subList(0, numSeats);
			} else {
				prevList = listValues;
				listSeats.addAll(listValues);
			}
		}

		return listSeats.subList(0, numSeats);
	}

}
