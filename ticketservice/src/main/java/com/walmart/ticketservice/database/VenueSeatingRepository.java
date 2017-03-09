package com.walmart.ticketservice.database;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.walmart.ticketservice.database.model.Seats;


@Repository
public interface VenueSeatingRepository  extends JpaRepository<Seats, Long>{
	
	List<Seats> findAllByStatus(String status);
	List<Seats> findAllByStatusOrderByRowprefid(String status);
	List<Seats> findAllByHoldidAndEmail(Integer holdid, String email);
	
	//For Timer Task
	List<Seats> findAllByStatusAndHolduntilIsNotNull(String status);
	
	
}
