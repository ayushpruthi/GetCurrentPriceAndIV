package com.standarddeviationanalysis.htmlresponse;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.standarddeviationanalysis.optionchain.OptionChainData;

public class ParseHtmlResponse {
	private Document doc;

	public ParseHtmlResponse(String response) {
		doc = Jsoup.parse(response);
	}

	public String getSecurityPrice() {
		String price = null;
		Element element = doc.getElementById("wrapper_btm");
		List<Element> elements = element.getElementsByTag("span");
		for (Element ele : elements) {
			String text = ele.ownText();
			if (text.contains("Underlying")) {
				List<Element> eles = ele.getElementsByTag("b");
				price = eles.get(0).ownText().split(" ")[1];
				break;
			}

		}
		return price;
	}

	public List<OptionChainData> getOptionChainData() {
		List<OptionChainData> optionData = new ArrayList<>();
		Element element = doc.getElementById("octable");
		List<Element> rows = element.getElementsByTag("tr");
		for (Element row : rows) {
			OptionChainData data = new OptionChainData();
			List<Element> cells = row.getElementsByTag("td");
			if (cells.size() > 10) {
				data.setCallIV(cells.get(4).ownText());
				data.setStrikePrice(cells.get(11).getElementsByTag("b").get(0).ownText());
				data.setPutIV(cells.get(18).ownText());
				optionData.add(data);
			}
		}
		return optionData;
	}

}
