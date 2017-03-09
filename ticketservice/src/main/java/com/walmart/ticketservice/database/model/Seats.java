package com.walmart.ticketservice.database.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SEATS")
public class Seats implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String rowid;
	private Integer rowprefid;
	private String seatid;
	private String status;
	private Integer holdid;
	private String email;
	private String resconfirmcode;

	private Timestamp updated;
	private Timestamp holduntil;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRowid() {
		return rowid;
	}

	public void setRowid(String rowid) {
		this.rowid = rowid;
	}

	public Integer getRowprefid() {
		return rowprefid;
	}

	public void setRowprefid(Integer rowprefid) {
		this.rowprefid = rowprefid;
	}

	public String getSeatid() {
		return seatid;
	}

	public void setSeatid(String seatid) {
		this.seatid = seatid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getHoldid() {
		return holdid;
	}

	public void setHoldid(Integer holdid) {
		this.holdid = holdid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getResconfirmcode() {
		return resconfirmcode;
	}

	public void setResconfirmcode(String resconfirmcode) {
		this.resconfirmcode = resconfirmcode;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	public Timestamp getHolduntil() {
		return holduntil;
	}

	public void setHolduntil(Timestamp holduntil) {
		this.holduntil = holduntil;
	}

	@Override
	public String toString() {
		return "Seats [id=" + id + ", rowid=" + rowid + ", rowprefid=" + rowprefid + ", seatid=" + seatid + ", status="
				+ status + ", holdid=" + holdid + ", email=" + email + ", resconfirmcode=" + resconfirmcode
				+ ", updated=" + updated + ", holduntil=" + holduntil + "]";
	}

}
