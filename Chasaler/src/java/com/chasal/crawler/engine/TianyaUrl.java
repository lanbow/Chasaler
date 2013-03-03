package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TianyaUrl extends AbstractEngine {

	public TianyaUrl() {
		
		engine_name="tianya";
		domain="search.tianya.cn";
		tempaltePath="./src/resources/template/tianya.xquery";
		PATTERN_META = Pattern
				.compile("(http://search.tianya.cn)?/ns\\?tn=sty&rn=(\\d+)&pn=(\\d+)&s=(\\d+)&pid=(\\d+)?&f=(\\d+)&h=(\\d+)&ma=(\\d+)&q=(.*)");
		PATTERN_PAGE = Pattern
				.compile("(http://search.tianya.cn)?/ns\\?tn=sty&rn=10&pn=(\\d+)&s=0&pid=&f=0&h=1&ma=0&q=(.*)");
		keywordIndex = 3;
		encoding = "gb2312";
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = Integer.parseInt(m.group(2)) + 1;
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
		String url = "http://search.tianya.cn/ns?tn=sty&rn=10&pn=0&s=0&pid=&f=0&h=1&ma=0&q="
				+ En_Keyword;
		return url;
	}

}
