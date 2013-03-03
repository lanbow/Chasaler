package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaUrl extends AbstractEngine {

	public TiebaUrl() {

		engine_name="tieba";
		domain="tieba.baidu.com";
		tempaltePath="./src/resources/template/tieba.xquery";
		PATTERN_META = Pattern
				.compile("(http://tieba.baidu.com)?/f/search/res\\?(isnew=1&)?kw=&qw=(.*)&rn=(\\d+)&un=&sm=(\\d+)(&sd=&ed=)?&pn=(\\d+)");
		PATTERN_PAGE = Pattern
				.compile("(http://tieba.baidu.com)?/f/search/res\\?(isnew=1&)?kw=&qw=(.*)&rn=10&un=&sm=1.*&pn=(\\d+)");

		keywordIndex = 3;
		encoding = "gb2312";
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
		String url = "http://tieba.baidu.com/f/search/res?isnew=1&kw=&qw="
				+ En_Keyword + "&rn=10&un=&sm=1&sd=&ed=&pn=1";
		return url;
	}
}
