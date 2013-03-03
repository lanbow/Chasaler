package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinaBlogUrl extends AbstractEngine {

	public SinaBlogUrl() {

		engine_name="sinablog";
		domain="search.sina.com.cn";
		tempaltePath="./src/resources/template/sinablog.xquery";
		PATTERN_META = Pattern
				.compile("(http://search.sina.com.cn)?/\\?by=(\\w+)&q=(.*)&c=blog&range=article(&a=)?(&sort=(\\w+))?&page=(\\d+)(&_lid=(.*))?(&sort=(\\w+))?&dpc=(.*)");
		PATTERN_PAGE = Pattern
				.compile("(http://search.sina.com.cn)?/\\?by=all&q=(.*)&c=blog&range=article(&a=)?&page=(\\d+).*");
		encoding = "gb2312";
		keywordIndex = 2;
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = Integer.parseInt(m.group(4));
		}
		return page;
	}

	public String generateURL(String Keyword, int t) {
		String En_Keyword = "";
		try {
			En_Keyword = java.net.URLEncoder.encode(Keyword, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = "http://search.sina.com.cn/?by=all&q="
				+ En_Keyword
				+ "&c=blog&range=article&a=&page=1&_lid=3b43283582f7ea8fe2e17def84b6b78e&dpc=1";
		return url;
	}
}
