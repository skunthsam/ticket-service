/**
 * 
 */
package com.walmart.ticketservice.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.walmart.ticketservice.Constants;
import com.walmart.ticketservice.database.VenueSeatingRepository;
import com.walmart.ticketservice.database.model.Seats;
import com.walmart.ticketservice.exception.HoldExpiredException;
import com.walmart.ticketservice.exception.InvalidDataException;
import com.walmart.ticketservice.exception.NotEnoughSeatsException;


/**
 * @author satya
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class TicketServiceImplTest {

	private TicketServiceImpl ticketServiceImpl;

	@Mock
	private VenueSeatingRepository venueSeatingRepository;

	List<Seats> seats;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		ticketServiceImpl = new TicketServiceImpl();
		ticketServiceImpl.setVenueSeatingRepository(venueSeatingRepository);

		seats = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			Seats seat = new Seats();

			if (i < 3) {
				seat.setRowid("A");
				seat.setRowprefid(1);
				seats.add(seat);

			} else if (i < 6) {
				seat.setRowid("B");
				seat.setRowprefid(2);
				seats.add(seat);
			} else if (i < 8) {
				seat.setRowid("C");
				seat.setRowprefid(3);
				seats.add(seat);
			} else {
				seat.setRowid("D");
				seat.setRowprefid(4);
				seats.add(seat);
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#numSeatsAvailable()}.
	 */
	@Test
	public void testNumSeatsAvailable() {

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);
		ticketServiceImpl.numSeatsAvailable();

		assertEquals(seats.size(), 10);

	}

	/**
	 * zero seats requested Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test(expected = InvalidDataException.class)
	public void testFindAndHoldSeatsForZeroSeats() {

		int numSeats = 0;
		String customerEmail = "abc@cbz.com";
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);
	}

	/**
	 * invalid email sent Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test(expected = InvalidDataException.class)
	public void testFindAndHoldSeatsForNoEmail() {

		int numSeats = 3;
		String customerEmail = null;

		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * not enought seats Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test(expected = NotEnoughSeatsException.class)
	public void testFindAndHoldSeats() {

		int numSeats = 2;
		String customerEmail = "abc@abc.com";

		List<Seats> seats1 = seats.subList(0, 1);

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats1);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * Exception in the execution Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test (expected = NotEnoughSeatsException.class)
	public void testFindAndHoldSeatsException() {

		int numSeats = 2;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(new ArrayList<Seats>());
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * Seats Requested is equal to seats available Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeatsAllSeatsAvaiable() {

		int numSeats = 3;
		String customerEmail = "abc@abc.com";

		List<Seats> seats = new ArrayList<>();

		Seats seat = new Seats();
		seat.setRowid("A");
		seat.setRowprefid(1);
		seats.add(seat);

		Seats seat1 = new Seats();
		seat1.setRowid("A");
		seat1.setRowprefid(1);
		seats.add(seat1);

		Seats seat2 = new Seats();
		seat2.setRowid("A");
		seat2.setRowprefid(1);
		seats.add(seat2);

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * Seats available from the single row Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeatsSeatsAviable() {

		int numSeats = 3;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

		// assertEquals(seatHold.isIsholdSuccess(), false);
	}

	/**
	 * 
	 * Seats reserved by another concurrent user Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test(expected = NotEnoughSeatsException.class)
	public void testFindAndHoldSeatsWhenAnthorUserHold() {

		int numSeats = 3;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		List<Seats> seats1 = seats.subList(0, 1);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats1);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * Seats available from Single Row Book the seats in the single row if
	 * available Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeatsAvailableInSingleRow() {

		int numSeats = 3;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * Seats not available from single row but available from two consecutive
	 * seats Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeatsFromTwoConsercutiveRows() {

		int numSeats = 6;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * 
	 * Seats not available in single, not in two consecutive rows Book all the
	 * seats Available starting from Row A, then Row B and then Row C and so
	 * on., Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#findAndHoldSeats(int, java.lang.String)}.
	 */
	@Test
	public void testFindAndHoldSeatsFromBestSeats() {

		int numSeats = 8;
		String customerEmail = "abc@abc.com";

		Mockito.when(venueSeatingRepository.findAllByStatus(Constants.AVAILABLE_STATUS)).thenReturn(seats);

		Mockito.when(venueSeatingRepository.findAllByStatusOrderByRowprefid(Constants.AVAILABLE_STATUS))
				.thenReturn(seats);
		ticketServiceImpl.findAndHoldSeats(numSeats, customerEmail);

	}

	/**
	 * heldId = 0 Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 * 
	 * @throws InvalidDataException
	 * @throws HoldExpiredException
	 */
	@Test(expected = InvalidDataException.class)
	public void testReserveSeatsInvalidHoldId()  {
		int heldId = 0;
		String customerEmail = "abc@abc.com";

		ticketServiceImpl.reserveSeats(heldId, customerEmail);

	}

	/**
	 * null as Email Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 * 
	 * @throws InvalidDataException
	 * @throws HoldExpiredException
	 */

	@Test(expected = InvalidDataException.class)
	public void testReserveSeatsInvalidEmail() {
		int heldId = 123;
		String customerEmail = null;

		ticketServiceImpl.reserveSeats(heldId, customerEmail);

	}

	/**
	 * Hold Expired Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 * 
	 * @throws InvalidDataException
	 * @throws HoldExpiredException
	 */
	@Test(expected = HoldExpiredException.class)
	public void testReserveSeatsHoldExipred()  {

		int seatHoldId = 123;
		String customerEmail = "abc@abc.com";
		Mockito.when(venueSeatingRepository.findAllByHoldidAndEmail(seatHoldId, customerEmail))
				.thenReturn(new ArrayList<Seats>());

		ticketServiceImpl.reserveSeats(seatHoldId, customerEmail);

	}

	/**
	 * Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 * 
	 * @throws InvalidDataException
	 * @throws HoldExpiredException
	 */
	@Test
	public void testReserveSeats()  {

		int seatHoldId = 123;
		String customerEmail = "abc@abc.com";
		Mockito.when(venueSeatingRepository.findAllByHoldidAndEmail(seatHoldId, customerEmail)).thenReturn(seats);

		ticketServiceImpl.reserveSeats(seatHoldId, customerEmail);
	}

	/**
	 * Test method for
	 * {@link com.walmart.ticketservice.service.TicketServiceImpl#reserveSeats(int, java.lang.String)}.
	 * 
	 */
	@Test(expected = Exception.class)
	public void testReserveSeatsUnknownException()  {

		int seatHoldId = 123;
		String customerEmail = "abc@abc.com";
		Mockito.when(venueSeatingRepository.findAllByHoldidAndEmail(seatHoldId, customerEmail)).thenReturn(null);

		ticketServiceImpl.reserveSeats(seatHoldId, customerEmail);
	}

}
