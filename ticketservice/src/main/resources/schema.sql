DROP TABLE SEATS IF EXISTS;

CREATE TABLE if not exists SEATS (
	id INTEGER PRIMARY KEY,
	rowid VARCHAR (50),
	rowprefid INTEGER,
	seatid VARCHAR (50),
	status VARCHAR (50),
	holdid INTEGER,
	email VARCHAR (50),
	resconfirmcode VARCHAR (50),
	updated TIMESTAMP,
	holduntil TIMESTAMP
);

