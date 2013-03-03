package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QihooUrl extends AbstractEngine {

	public QihooUrl() {
		
		engine_name="qihoo";
		domain="www.so.com";
		tempaltePath="./src/resources/template/qihoo.xquery";
		PATTERN_META = Pattern.compile("(http://www.so.com)?/s\\?q=(.*)&pn=.*");

		PATTERN_PAGE = Pattern
				.compile("(http://www.so.com)?/s\\?q=(.*)&pn=(\\d+)&j=(\\d+)&_re=(\\d+)");
		keywordIndex = 2;
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public String generateURL(String Keyword, int t) {
		String En_Keyword = "";
		try {
			En_Keyword = java.net.URLEncoder.encode(Keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://www.so.com/s?q=" + En_Keyword + "&pn=1&j=0&_re=0";

		return url;
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = Integer.parseInt(m.group(3));
		}
		return page;
	}
}
