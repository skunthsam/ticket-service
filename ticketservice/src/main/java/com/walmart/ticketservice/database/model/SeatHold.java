package com.walmart.ticketservice.database.model;

import java.util.List;

/**
 * @author Satya
 *
 */

public class SeatHold {



	public List<String> seatIds;
	public String email;
	public int holdid;
	public String reservedTime;
	public boolean isholdSuccess;
	public String errorMessage;

	public List<String> getSeatIds() {
		return seatIds;
	}

	public void setSeatIds(List<String> seatIds) {
		this.seatIds = seatIds;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getHoldid() {
		return holdid;
	}

	public void setHoldid(int holdid) {
		this.holdid = holdid;
	}

	public String getReservedTime() {
		return reservedTime;
	}

	public void setReservedTime(String reservedTime) {
		this.reservedTime = reservedTime;
	}

	public boolean isIsholdSuccess() {
		return isholdSuccess;
	}

	public void setIsholdSuccess(boolean isholdSuccess) {
		this.isholdSuccess = isholdSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "SeatHold [seatIds=" + seatIds + ", email=" + email + ", holdid=" + holdid + ", reservedTime="
				+ reservedTime + "]";
	}

}
