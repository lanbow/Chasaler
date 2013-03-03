package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleUrl extends AbstractEngine {

	//提取关键词时候用到的一种正则表达式
	private static Pattern PATTERN_GOOGLE_KEYWORD_1 = Pattern
			.compile("(http://www.google.com.hk)?/search\\?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q=(.*)&btnK=Google\\+%E6%90%9C%E7%B4%A2.*");
	//提取关键词时候用到的另一种正则表达式
	private static Pattern PATTERN_GOOGLE_KEYWORD_2 = Pattern
			.compile("(http://www.google.com.hk)?/search\\?q=(.*)&hl=zh-CN&newwindow=.*");
	//提取谷歌元搜索链接中的随机码将其去掉时候用到的正则表达式
	public static Pattern PATTERN_GOOGLE_RANDOM_CODE = Pattern
			.compile("/search\\?q=.*(&ei=.*)&start=.*&sa=N");
	//提取谷歌元搜索链接中关于时间选项时候用到的正则表达式，需要将其放到尾部，否则会造成请求网页失败
	public static Pattern PATTERN_GOOGLE_PAGE_TIME = Pattern
			.compile("/search\\?q=.*(&tbs=qdr:(.*))(&prmd|&start)=.*&sa=N");
	//重定向之后的网页链接可以直接从模板抽取出来的链接中提取出来，这就是用到的提取正则表达式
	public static Pattern PATTERN_GOOGLE_RECORD = Pattern
			.compile("/url\\?q=(http://.*)&sa=U&ei=.*");

	public GoogleUrl() {

		engine_name="google";
		domain="www.google.com.hk";
		tempaltePath="./src/resources/template/google.xquery";
		PATTERN_META = Pattern
				.compile("(http(s)?://www.google.com.hk)?/search\\?(q|hl)=(.*)");
		PATTERN_PAGE = Pattern
				.compile("(http://www.google.com.hk/search)?\\?q=.*&hl=zh-CN&newwindow=.*&start=(\\d+)&sa=N");
		this.pageLimit = this.getLimitedPage(engine_name);
	}

	public String generateURL(String Keyword, int time_limited) {
		String En_Keyword = "";
		String url = null;
		try {
			// En_Keyword = java.net.URLEncoder.encode(Keyword, "gb2312");
			En_Keyword = java.net.URLEncoder.encode(Keyword, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (time_limited == 0) {
			url = "http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q="
					+ En_Keyword
					+ "&btnK=Google+%E6%90%9C%E7%B4%A2"
					+ "&as_qdr=d3";
		} else {
			url = "http://www.google.com.hk/search?hl=zh-CN&newwindow=1&safe=strict&site=&source=hp&q="
					+ En_Keyword
					+ "&btnK=Google+%E6%90%9C%E7%B4%A2"
					+ "&as_qdr=d" + time_limited;
		}

		return url;
	}

	public String getKeyword(String url) {
		String keyWord = "";
		Matcher m1 = PATTERN_GOOGLE_KEYWORD_1.matcher(url);
		while (m1.find()) {
			keyWord = m1.group(2);
		}
		Matcher m2 = PATTERN_GOOGLE_KEYWORD_2.matcher(url);
		while (m2.find()) {
			keyWord = m2.group(2);
		}
		try {
			keyWord = java.net.URLDecoder.decode(keyWord, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return keyWord;
	}

	public int getPage(String url) {
		int page = 1;
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			page = (Integer.parseInt(m.group(2))) / 10 + 1;
		}
		return page;
	}
}
