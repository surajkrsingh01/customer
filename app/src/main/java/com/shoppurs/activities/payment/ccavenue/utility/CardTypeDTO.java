package com.shoppurs.activities.payment.ccavenue.utility;

import java.io.Serializable;

public class CardTypeDTO implements Serializable {
	private String cardName;
	private String cardType;
	private String payOptType;
	private String dataAcceptedAt;
	private String status;
	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public String getCardName() {
		return cardName;
	}
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getPayOptType() {
		return payOptType;
	}
	public void setPayOptType(String payOptType) {
		this.payOptType = payOptType;
	}
	public String getDataAcceptedAt() {
		return dataAcceptedAt;
	}
	public void setDataAcceptedAt(String dataAcceptedAt) {
		this.dataAcceptedAt = dataAcceptedAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}