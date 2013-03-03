package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SosoUrl extends AbstractEngine {

	public SosoUrl() {
		
		engine_name="soso";
		domain="www.soso.com";
		tempaltePath="./src/resources/template/soso.xquery";
		PATTERN_META = Pattern
				.compile("(http://www.soso.com)?/q\\?w=(.*)&lr=.*");
		PATTERN_PAGE = Pattern
				.compile("(http://www.soso.com)?/q\\?w=(.*)&lr=&sc=web&ch=w.p&num=10&gid=&cin=&site=&sf=0&sd=0&nf=0&pg=(\\d+)");
		encoding = "gb2312";
		keywordIndex = 2;
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public String generateURL(String Keyword, int time_limited) {

		String En_Keyword = "";
		String url = null;
		try {
			En_Keyword = java.net.URLEncoder.encode(Keyword, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = "http://www.soso.com/q?w=" + En_Keyword
				+ "&lr=&sc=web&ch=w.p&num=10&gid=&cin=&site=&sf=0&sd="
				+ time_limited + "&nf=0&pg=1";

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
