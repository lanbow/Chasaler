package com.chasal.crawler.verify;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;



public class GoogleManualVerify extends AbstractIVerify {
	
	private static Pattern PATTERN_ID = Pattern
			.compile("<input type=\"hidden\" name=\"id\" value=\"(.*)\"><input type=\"text\" name=\"captcha\"");

	public GoogleManualVerify() {
		verifyer="google";
		hopEncoding="utf-8";
		PATTERN_PICTURE = Pattern.compile("<img src=\"(.*)\" border=");
	}
	
	public String getId(String html) {
		String id = null;
		Matcher m = PATTERN_ID.matcher(html);
		while (m.find()) {
			id = m.group(1);
		}
		return id;
	}
	
//	http://www.google.com.hk/sorry/?continue=http://www.google.com.hk/search%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2

//	http://www.google.com.hk/sorry/Captcha?continue=
//		http%3A%2F%2Fwww.google.com.hk%2Fsearch%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2
//&id=7691688738147005944&captcha=81039&submit=%E6%8F%90%E4%BA%A4
	@Override
	public String getTargetUrl(String url, String html) {
		String targetUrl = null;
		String id = getId(html);
		String verifyCode = getVerifyCode(url, html);
		String preUrl = url.replace("http://www.google.com.hk/sorry/?continue=","");
		if (verifyCode != null && id != null ) {
			try {			
				targetUrl = "http://www.google.com.hk/sorry/Captcha?continue="
						+ java.net.URLEncoder.encode(java.net.URLDecoder.decode(preUrl,"utf-8"),"utf-8")
						+ "&id=" + id + "&captcha=" + verifyCode + "&submit=%E6%8F%90%E4%BA%A4";
			} catch (Exception e) {
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
		return (url.contains("http://www.google.com.hk/sorry/")||url.contains("http://www.google.com/sorry/"));
	}
	/**
	 * 获取图片链接
	 * 
	 * @param html
	 * @return
	 */
	@Override
	public String getPictureUrl(String html) {
		String pictureUrl = null;
		Matcher m = PATTERN_PICTURE.matcher(html);
		while (m.find()) {
			pictureUrl = m.group(1);
		}
		return "http://www.google.com.hk"+pictureUrl.replaceAll("&amp;", "&");
	}
	/**
	 * 提交验证码
	 * 
	 * @param curi
	 * @return 返回跳转之后的网页链接
	 */
	@Override
	public String submitVerifyCode(String targetUrl,String html) {
		String newUrl = null;
		int code;
		HttpClientParams.setRedirecting(httpclient.getParams(), false);
		HttpGet httpGet = new HttpGet();

		httpGet.setHeader("Content-Type",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		httpGet.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
		httpGet.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
		httpGet.setHeader("Cache-Control", "max-age=0");
		httpGet.setHeader("host","www.google.com.hk");
		try {
			httpGet.setURI(new URI(targetUrl));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet,localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		code = response.getStatusLine().getStatusCode();

		if (code == 200) {
			newUrl = response.getFirstHeader("Location").getValue();
			return newUrl;
		}else {
			httpGet.releaseConnection();
			return null;
		}
	}
//	public static void main(String[] args){
//		String url = "http://www.google.com.hk/sorry/?continue=http://www.google.com.hk/search%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2";
//		String encoding = "UTF-8";
//		String html = HttpClientHelper.downloadHtml(url, encoding,"");
//		GoogleManualVerify gmv = new GoogleManualVerify();
//		String picUrl = gmv.handleVerifyCode(url,html);
//		System.out.println(picUrl);
//	}
	
	
//	http://www.google.com.hk/sorry/?continue=http://www.google.com.hk/search%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2
//	http://www.google.com.hk/sorry/Captcha?continue=http%3A%2F%2Fwww.google.com.hk%2Fsearch%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2&id=7691688738147005944&captcha=81039&submit=%E6%8F%90%E4%BA%A4
//	http://www.google.com.hk/sorry/Captcha?continue=http%3A%2F%2Fwww.google.com.hk%2Fsearch%3Fhl%3Dzh-CN%26newwindow%3D1%26safe%3Dstrict%26tbo%3Dd%26site%3D%26source%3Dhp%26q%3D%25E9%2594%25A6%25E9%25B8%25BF%26btnG%3DGoogle%2B%25E6%2590%259C%25E7%25B4%25A2&id=15119907147629816389&captcha=1189722&submit=%E6%8F%90%E4%BA%A4
}
