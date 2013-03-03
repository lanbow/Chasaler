package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SougouUrl extends AbstractEngine {

	public SougouUrl() {
		
		engine_name="sogou";
		domain="www.sogou.com";
		tempaltePath="./src/resources/template/sogou.xquery";
		if (PATTERN_META == null) {
			PATTERN_META = Pattern
					.compile("(http://www.sogou.com/web)?\\?query=(.*)(&tsn|&sut)=.*");
		}
		if (PATTERN_PAGE == null) {
			PATTERN_PAGE = Pattern
					.compile("(http://www.sogou.com/web)?\\?query=(.*)((&tsn|&sut)=\\d+)&page=(\\d+)(&.*)?");
		}
		keywordIndex = 2;
		encoding = "gb2312";
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
		url = "http://www.sogou.com/web?query=" + En_Keyword + "&tsn="
				+ time_limited + "&page=1";

		return url;
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = Integer.parseInt(m.group(5));
		}
		return page;
	}
}
