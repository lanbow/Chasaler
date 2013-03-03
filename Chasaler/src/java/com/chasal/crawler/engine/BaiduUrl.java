package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduUrl extends AbstractEngine {

	public BaiduUrl() {

		engine_name="baidu";
		domain="www.baidu.com";
		tempaltePath="./src/resources/template/baidu.xquery";
		PATTERN_META = Pattern
				//.compile("(http://www.baidu.com)?/s\\?wd=(.*)&pn=(\\d+)&ie=utf-8(&lm=\\d+)?(&usm=\\d+)?");
				.compile("(http://www.baidu.com)?/s\\?wd=(.*)&pn=(\\d+).*");
		PATTERN_PAGE = Pattern
				.compile("(http://www.baidu.com)?/s\\?wd=(.*)&pn=(\\d+)&ie=utf-8.*");
		keywordIndex = 2;
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	@Override
	public String generateURL(String Keyword, int time_limited) {
		String En_Keyword = "";
		String url = null;
		try {
			En_Keyword = java.net.URLEncoder.encode(Keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url = "http://www.baidu.com/s?wd=" + En_Keyword + "&pn=0&ie=utf-8"
				+ "&lm=" + time_limited;
		return url;
	}

	@Override
	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = Integer.parseInt(m.group(3)) / 10 + 1;
		}
		return page;
	}
}
