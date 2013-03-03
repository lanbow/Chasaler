package com.chasal.crawler.verify;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduManualVerify extends AbstractIVerify {

	// 抽取图片地址的正则表达式
	private static Pattern PATTERN_VCODE = Pattern
			.compile("name=\"vcode\" value=\"(.*)\">");
	private static Pattern PATTERN_ID = Pattern
			.compile("name=\"id\" value=\"(.*)\">");
	private static Pattern PATTERN_DI = Pattern
			.compile("name=\"di\" value=\"(.*)\">");
	

	public BaiduManualVerify() {
		verifyer="baidu";
		hopEncoding="gb2312";
		PATTERN_PICTURE = Pattern.compile("<img src=\"(.*)\" width=");
	}

	public String getVcode(String html) {
		String vcode = null;
		Matcher m = PATTERN_VCODE.matcher(html);
		while (m.find()) {
			vcode = m.group(1);
		}
		return vcode;
	}

	public String getId(String html) {
		String id = null;
		Matcher m = PATTERN_ID.matcher(html);
		while (m.find()) {
			id = m.group(1);
		}
		return id;
	}

	public String getDi(String html) {
		String di = null;
		Matcher m = PATTERN_DI.matcher(html);
		while (m.find()) {
			di = m.group(1);
		}
		return di;
	}

	@Override
	public String getTargetUrl(String url, String html) {
		String targetUrl = null;
		String vcode = getVcode(html);
		String id = getId(html);
		String di = getDi(html);
		String verifyCode = getVerifyCode(url, html);
		String preUrl = url.replace("http://verify.baidu.com/vcode?","");
		if (verifyCode != null && vcode != null && id != null ) {
			try {			
				targetUrl = "http://verify.baidu.com/verify?url="
						+ java.net.URLEncoder.encode(preUrl, "utf-8")
						+ "&vcode=" + vcode + "&id=" + id + "&di=" + di
						+ "&verifycode=" + verifyCode;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return targetUrl;
	}
	
	@Override
	public boolean isSuitable(String url) {
		if(url==null){
			return false;
		}
		return url.contains("http://verify.baidu.com");
	}


}
