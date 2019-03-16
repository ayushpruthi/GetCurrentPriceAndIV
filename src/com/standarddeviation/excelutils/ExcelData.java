package com.standarddeviation.excelutils;

public class ExcelData {

	private String security;
	private String url;
	private String callPut;
	private String currentPrice;
	private String strike;
	private String IV;
	private String expiryDate;
	private boolean isBanned;

	public boolean isBanned() {
		return isBanned;
	}

	public void setBanned(boolean isBanned) {
		this.isBanned = isBanned;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCallPut() {
		return callPut;
	}

	public void setCallPut(String callPut) {
		this.callPut = callPut;
	}

	
	public String getStrike() {
		return strike;
	}

	public void setStrike(String strike) {
		this.strike = strike;
	}

	

	public String getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getIV() {
		return IV;
	}

	public void setIV(String iV) {
		IV = iV;
	}

}
