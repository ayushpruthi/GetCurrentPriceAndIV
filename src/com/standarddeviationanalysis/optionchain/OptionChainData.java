package com.standarddeviationanalysis.optionchain;

public class OptionChainData {

	private String callIV;
	private Double strikePrice;
	private String putIV;

	
	public String getCallIV() {
		return callIV;
	}

	public void setCallIV(String callIV) {
		this.callIV = callIV;
	}

	public Double getStrikePrice() {
		return strikePrice;
	}

	public void setStrikePrice(String strikePrice) {
		this.strikePrice = Double.parseDouble(strikePrice.trim());
	}


	public String getPutIV() {
		return putIV;
	}

	public void setPutIV(String putIV) {
		this.putIV = putIV;
	}

}
