package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoudaoUrl extends AbstractEngine {

	public YoudaoUrl() {
		
		engine_name="youdao";
		domain="www.youdao.com";
		tempaltePath="./src/resources/template/youdao.xquery";
		PATTERN_META = Pattern
				.compile("(http://www.youdao.com)?/search\\?q=(.*)&start=(\\d+)&ue=utf8&keyfrom=web.page(\\d+)&lq=(.*)(&lm=\\d+)?&timesort=(\\d+)");
		PATTERN_PAGE = Pattern
				.compile("(http://www.youdao.com)?/search\\?q=(.*)&start=(\\d+).*");
		keywordIndex = 2;
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = (Integer.parseInt(m.group(3))) / 10 + 1;
		}
		return page;
	}

	public String generateURL(String Keyword, int t) {
		String En_Keyword = "";
		try {
			En_Keyword = java.net.URLEncoder.encode(Keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "http://www.youdao.com/search?q=" + En_Keyword
				+ "&start=0&ue=utf8&keyfrom=web.page1&lq=" + En_Keyword
				+ "&timesort=0";
		return url;
	}
}
