package com.chasal.crawler.engine;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.chasal.crawler.utils.HttpClientHelper;

public class YunYunUrl {

	private static Pattern DIVIDEWEIBO = Pattern
			.compile("\\[\\[\\[((?:(?!\\[\\[\\[).)*)");
	private static Pattern PUBTIME = Pattern.compile("(1(\\d){9}),");
	private static Pattern RESPONSE = Pattern
			.compile("\"([^\"]*.(jpg|bmp|gif))\",\"[^\"]*\",(\\d+),(\\d+),[^,]*,[^,]*,[^,]*,[^,]*");
	private static Pattern SOURCE = Pattern
			.compile("1(\\d){9},\"[^\"]*\",\\[\"([^\"]*)\",\"([^\"]*)\",\"(.*)\",[^,]*,[^,]*,[^,]*,\"http:.*\",(\\d+),(\\d+),[^,]*,[^,]*,[^,]*,[^,]*\\]");
	private static Pattern BI = Pattern.compile(",\"((\\d){19})\",");
	private static Pattern COOKIE = Pattern.compile("yrssid=(\\d+);");
	private static Pattern ENCODE16 = Pattern
			.compile("(\\\\u(\\p{XDigit}{4}))");
	private static Pattern ENCODE10 = Pattern.compile("(&#(\\d+);)");
	private static Pattern JASON = Pattern.compile("\"data\":\\[(.*)]}");

	public static String getSeed(String keyword) {
		String url = null;
		try {
			keyword = URLEncoder.encode(keyword, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = "http://weibo.yunyun.com/Weibo.php?p=1&q=" + keyword;
		return url;
	}

	public static String getNextUrl(String keyword, int page, String bi) {
		String url = null;
		long time = System.currentTimeMillis();
		try {
			keyword = URLEncoder.encode(keyword, "gb2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = "http://weibo.yunyun.com/Ajax/Search.php?o=json&q=" + keyword
				+ "&srt=mb&c=" + page * 10 + "&l=10&bi=" + bi + "&rsl=1&ts="
				+ time + "&usg=" + getCookies();
		return url;

	}

	// 5849538268771334817
	public static String getBi(String jason) {
		String bi = null;
		Matcher bi_matcher = BI.matcher(jason);
		while (bi_matcher.find()) {
			bi = bi_matcher.group(1);
		}
		return bi;
	}

	// yrssid=1428113173188867729;
	public static String getCookies() {
		String yrssid = null;
		String repose = HttpClientHelper.getLocalContext()
				.getAttribute("http.response").toString();
		Matcher matcher = COOKIE.matcher(repose);
		while (matcher.find()) {
			yrssid = matcher.group(1);
		}
		return yrssid;
	}
	
	public static String getJason(String url){
		String jason=null;
		jason=HttpClientHelper.downloadHtml(url, "gb2312", "weibo.yunyun.com");
		Matcher matcher = JASON.matcher(jason);
		while (matcher.find()) {
			jason = matcher.group(1);
		}
		return jason;
	}

	/**
	 * 
	 * 将 \u9ed1\u8272\u7248\u56fe 编码的字符串，转为可阅读的文字 用于新浪微博 正文解析
	 * 
	 * @param dataStr
	 * @return
	 * 
	 * @since crawler_weibo　Ver1.0
	 */
	public static String decodeUnicode(String dataStr) {
		if (dataStr == null) {
			return null;
		}
		Matcher encode16_matcher = ENCODE16.matcher(dataStr);
		char ch;
		while (encode16_matcher.find()) {
			ch = (char) Integer.parseInt(encode16_matcher.group(2), 16);
			dataStr = dataStr.replace(encode16_matcher.group(1), ch + "");
		}

		Matcher encode10_matcher = ENCODE10.matcher(dataStr);
		char ch1;
		while (encode10_matcher.find()) {
			ch1 = (char) Integer.parseInt(encode10_matcher.group(2), 10);
			dataStr = dataStr.replace(encode10_matcher.group(1), ch1 + "");
		}
		// dataStr=dataStr.replaceAll("\\\\/", "/").replaceAll("\\\\\"", "\"");
		return dataStr;
	}

	public List<Status> extractJSON(String content) {

		List<Status> WeiBoList = new ArrayList<Status>();

		String weibo="";
		content = content.replaceAll("\\\\/", "/");
		Matcher weibo_matcher = DIVIDEWEIBO.matcher(content);
		while (weibo_matcher.find()) {
			Status status = new Status();
			weibo = weibo_matcher.group(1);
			String[] author=weibo.split(",");
			status.setUrl(author[0].replaceAll("\\\\\"", "\""));
			status.setAuthorName(author[1].replaceAll("\\\\\"", "\""));
			status.setContent(author[2].replaceAll("\\\\\"", "\""));
			
//			System.out.println("");
//			System.out.println(content);
//			System.out.println("帖子url："
//					+ weibo.split(",")[0].replaceAll("\\\\\"", "\""));
//			System.out.println("发帖人："
//					+ weibo.split(",")[1].replaceAll("\\\\\"", "\""));
//			System.out.println("帖子内容："
//					+ weibo.split(",")[2].replaceAll("\\\\\"", "\"")
//							.replaceAll("<([^>])*>", ""));

			Matcher time_matcher = PUBTIME.matcher(content);
			while (time_matcher.find()) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long time = Integer.parseInt(time_matcher.group(1));
				time *= 1000;
				status.setReleaseTime(sdf.format(new Date(time)));
				//System.out.println("发帖时间：" + sdf.format(new Date(time)));
			}

			Matcher response_matcher = RESPONSE.matcher(content);
			while (response_matcher.find()) {
				status.setCommentCount(response_matcher.group(4));
				status.setRepostCount(response_matcher.group(3));
				status.setPicUrl(response_matcher.group(1));
				
//				System.out.println("图片链接:" + response_matcher.group(1));
//				System.out.println("转发量:" + response_matcher.group(3));
//				System.out.println("评论量:" + response_matcher.group(4));
			}

			Matcher source_matcher = SOURCE.matcher(content);
			while (source_matcher.find()) {
				
				status.setfAuthorName(source_matcher.group(3));
				status.setfContent(source_matcher.group(4).replaceAll("<([^>])*>", ""));
				status.setfUrl(source_matcher.group(2));
//				System.out.println("被转发帖子url:" + source_matcher.group(2));
//				System.out.println("被转发用户昵称:" + source_matcher.group(3));
//				System.out.println("被转发帖子内容:"
//						+ source_matcher.group(4).replaceAll("<([^>])*>", ""));
//				System.out.println("被转发帖子转发量:" + source_matcher.group(5));
//				System.out.println("被转发帖子评论量:" + source_matcher.group(6));
			}
			WeiBoList.add(status);

		}
		return WeiBoList;
	}

}
