package com.standarddeviationanalysis.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.standarddeviation.excelutils.ExcelData;
import com.standarddeviation.excelutils.SuiteController;
import com.standarddeviationanalysis.htmlresponse.ParseHtmlResponse;
import com.standarddeviationanalysis.httprequest.HttpRequest;
import com.standarddeviationanalysis.optionchain.OptionChainData;
import com.standarddeviationanalysis.properties.ConfigProperties;

public class ExecutionStart {
	private static Map<String, String> properties = null;
	private static String baseUrl = null;
	private static List<String> bannedSecurities = null;

	static {
		try {
			properties = ConfigProperties.getProperties();
			baseUrl = properties.get("BaseUrl");
		} catch (IOException e) {
			System.out.println("Not able to read config.properties file");
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static Map<String, String> getProperties() {
		return properties;
	}

	public static void main(String[] args) {
		List<ExcelData> suiteData = null;
		Logger.getRootLogger().setLevel(Level.OFF);
		System.out.println("Starting execution...");
		System.out.println("Getting banned securities...");
		bannedSecurities = getBannedSecurities();
		System.out.println("Reading excel file: " + properties.get("SheetName"));
		try {
			suiteData = SuiteController.getExcelData(properties.get("SheetName"), properties.get("TabName"));
		} catch (IOException e) {
			System.out.println("Not able to read excel file " + properties.get("SheetName"));
			e.printStackTrace();
			System.exit(0);
		}
		executeSuite(suiteData);
		System.out.println("Putting results in excel file...");
		try {
			SuiteController.dumpExcelResults(suiteData, properties.get("SheetName"), properties.get("TabName"));
		} catch (IOException e) {
			System.out.println("Error in writing execution results in excel");
			e.printStackTrace();
		}
		System.out.println("Execution ended...");
	}

	private static void executeSuite(List<ExcelData> suiteData) {
		for (ExcelData data : suiteData) {
			System.out.println("Executing request for security: " + data.getSecurity());
			String response = null;
			ParseHtmlResponse parseResponse = null;
			try {
				if (data.getSecurity().length() > 0) {
					String securityUrl = baseUrl.replace("securityName", data.getSecurity().trim())
							.replace("expiryDate", data.getExpiryDate().replaceAll("-", "").toUpperCase().trim());
					response = HttpRequest.executeRequestAndGetResponse(securityUrl);
					parseResponse = new ParseHtmlResponse(response);
					String securityPrice = parseResponse.getSecurityPrice();
					if (securityPrice == null) {
						securityPrice = "NA";
					}
					List<OptionChainData> optionData = parseResponse.getOptionChainData();
					data.setCurrentPrice(securityPrice);
					List<OptionChainData> result = getMatchingStrikePriceData(optionData, data.getStrike());
					if (result.size() == 0) {
						data.setIV("NA");
					} else {
						if (data.getCallPut().contains("c") || data.getCallPut().contains("C")) {
							try {
								data.setIV(
										new Long(Math.round(Double.parseDouble(result.get(0).getCallIV()))).toString());
							} catch (NumberFormatException e) {
								data.setIV("NA");
							}
						} else {
							try {
								data.setIV(
										new Long(Math.round(Double.parseDouble(result.get(0).getPutIV()))).toString());
							} catch (NumberFormatException e) {
								data.setIV("NA");
							}
						}
					}
					if (bannedSecurities.contains(data.getSecurity().trim())) {
						data.setBanned(true);
					} else {
						data.setBanned(false);
					}
				} else {
					data.setCurrentPrice("NA");
					data.setIV("NA");
				}

			} catch (IOException e) {
				System.out.println("Error in executing request for " + data.getSecurity());
				e.printStackTrace();
			}
		}
	}

	private static List<OptionChainData> getMatchingStrikePriceData(List<OptionChainData> optionData,
			String strikePrice) {
		List<OptionChainData> result = (List<OptionChainData>) optionData.stream()
				.filter(item -> item.getStrikePrice().equals(Double.parseDouble(strikePrice)))
				.collect(Collectors.toList());
		return result;
	}

	private static List<String> getBannedSecurities() {
		List<String> bannedSecurities = new ArrayList<>();
		try {
			String response = HttpRequest.executeRequestAndGetResponse(properties.get("BannedSecuritiesUrl"));
			String[] bannedSecuritiesData = response.split(":")[1].split("\n");
			for (int i = 0; i < bannedSecuritiesData.length; i++) {
				if (bannedSecuritiesData[i] != null && bannedSecuritiesData[i].length() != 0
						&& bannedSecuritiesData[i] != " " && bannedSecuritiesData[i].contains(",")) {
					bannedSecurities.add(bannedSecuritiesData[i].split(",")[1].trim());
				}
			}
		} catch (IOException e) {
			System.out.println("Error encountered in getting banned securities...");
			e.printStackTrace();
		}
		return bannedSecurities;
	}

}
