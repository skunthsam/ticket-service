package com.walmart.ticketservice.tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.walmart.ticketservice.Constants;
import com.walmart.ticketservice.database.VenueSeatingRepository;
import com.walmart.ticketservice.database.model.Seats;

/**
 * @author Satya
 * 
 *         This is a Scheduler Bean to release the expired Sessions
 * 
 *         This job runs every 30 secs and release or update the records if the
 *         HoldUntil time is in past.
 * 
 */

@Component
public class ReleaseExpiredHoldScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ReleaseExpiredHoldScheduledTasks.class);

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private VenueSeatingRepository venueSeatingRepository;

	@Scheduled(fixedRate = 15000)
	public void reportCurrentTime() {
		log.info("The time is now {}", dateFormat.format(new Date()));
		List<Seats> expiredHolds = new ArrayList<>();

		List<Seats> seats = venueSeatingRepository.findAllByStatusAndHolduntilIsNotNull(Constants.HOLD_STATUS);
		for (Seats seat : seats) {
			System.out.println(seat.toString());
			if (seat.getHolduntil().getTime() <= System.currentTimeMillis()) {
				seat.setStatus(Constants.AVAILABLE_STATUS);
				seat.setEmail("");
				seat.setHoldid(null);
				seat.setUpdated(null);
				seat.setHolduntil(null);
				expiredHolds.add(seat);
			}
		}
		venueSeatingRepository.save(expiredHolds);

	}
}