# About
	TicketService is Project to provide services to Hold, Reserve the tickets for particual Venue

# Installation and Execution
	
	Instaltion
		Set JAVA_HOME
		
		Set Java and Maven class path

		Download the Project, Copy to a location and run the below commands 
			
		mvn clean install
		
		java -cp target\ticketservice-0.0.1.jar org.springframework.boot.loader.JarLauncher

	Sample Execcution of Services

		curl -X GET http://localhost:8080/ticket-service/available-seats
			--> returns available seats
		curl -X POST http://localhost:8080/ticket-service/hold-seats/2/abc@abc.com
			--> holds 2 tickets if availabe , if not available Display message "Not Enough seats available"

		curl -X POST http://localhost:8080/ticket-service/reserve-seats/89009/abc@abc.com
			--> Reserves the held tickets and update the database and returns the reservation confirmation number


# Project Dependeices

	Java 1.8
	Apache Maven 3.3.9


	Spring Boot 1.5.2
	JPA including spring-data-jpa, spring-orm and Hibernate
	H2 Embeded Database
	Embeded Tomcat
	JUnit 4.12 and Mockito 1.10.19

# Functional Overview, Functionality and Assumptions

     Assumptions 
	
	1. Venue Seating is as follows
	
	Venue Seating 
	___________________________________________

	A1	A2	A3	A4	A5	A6
	B1	B2	B3	B4	B5	B6
	C1	C2	C3	C4	C5	C6
	D1	D2	D3	D4	D5	D6
	E1	E2	E3	E4	E5	E6
	F1	F2	F3	F4	F5	F6
	G1	G2	G3	G4	G5	G6
	H1	H2	H3	H4	H5	H6

	2. Row A is best viewing Row , Row B is second best and so on.,
	3. If avialable, all seats to should reserved in same row
	4. If no seats available in single row, should be reserved from consecutive rows 
	5. If not in single row availableand not available in concecutive rows, seats will be reserved from availabe rows from starting Row A. 
		For example, if there is a request for 10 seats, if Row A has 2 , Row B has 4 , Row E has 5 and Row F has 4 
			then  2 seats from Row A, 4 seats Seats from RowB, 4 Seats from Row E will be reserved
	6. If not enough seats avaiable, then return Message "Not Enought Seats Available"


      Project provides three services
	1. /available-seats - number of seats available in Venue 
			Number of seats that are neither held nor reserved

	2. /hold-seats - find and holds the seats available
		Seats automatically expires after 5 mins (300 secs)

	3. /reserve-seats - Reserve the Hold Seats
		Reserve the seats which were on hold, if expired sends a Exired message.

# Design and Build 
	DB Design table design with Initial setup of the table

		Seq#	Rowid	rowprefid	seatid	status	holdid	email	updated	holduntil
		1	A	1	A1	A				
		2	A	1	A2	A				
		3	A	1	A3	A				
		4	A	1	A4	A				
		5	A	1	A5	A				
		6	B	2	A6	A				
		7	B	2	A7	A				
		8	B	2	A8	A				
		9	B	2	A9	A				
		10	B	2	A10	A				
	
	Java
		Service Reqeust --> Service Controller --> Ticket Service --> JPA - H2 Database

		TicketServiceImpl --> implements all three services.
		SeatHold --> stores the list of seats ids, HoldId, ErrorMessage if any.
		ReleaseExpiredHoldScheduledTasks - Scheduler runs every 15 secs and release exired hold seats

# Test Results 


		-------------------------------------------------------
		 T E S T S
		-------------------------------------------------------
		Running com.walmart.ticketservice.service.TicketServiceImplTest

		Tests run: 16, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.367 sec - in com.walmart.ticketservice.service.TicketServiceImplTest

		Results :

		Tests run: 16, Failures: 0, Errors: 0, Skipped: 0