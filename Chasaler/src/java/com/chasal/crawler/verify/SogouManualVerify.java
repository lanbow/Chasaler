package com.chasal.crawler.verify;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;


public class SogouManualVerify extends AbstractIVerify {

	private static Logger logger=Logger.getLogger("verify");
	private static Pattern PATTERN_R = Pattern
			.compile("name=\"r\" value=\"([^\"]*)\" >");
	private static Pattern PATTERN_KEY = Pattern
			.compile("/web\\?query=(.*)(&tsn=.*)");

	public SogouManualVerify() {
		verifyer="sogou";
		hopEncoding = "gbk";
		PATTERN_PICTURE = Pattern.compile("<img src=\"(.*)\" alt=");
	}

	public String getRcode(String html) {
		String Rcode = null;
		Matcher m = PATTERN_R.matcher(html);
		while (m.find()) {
			Rcode = m.group(1);
		}
		return Rcode;
	}
	
	@Override
	public String getPictureUrl(String html) {
		String pictureUrl = null;
		Matcher m = PATTERN_PICTURE.matcher(html);
		while (m.find()) {
			pictureUrl = m.group(1);
		}
		if(pictureUrl == null){
			return null;
		}
		return pictureUrl = "http://www.sogou.com/antispider/" + pictureUrl;
	}


	@Override
	public boolean isSuitable(String url) {
		if (url == null) {
			return false;
		}
		// return url.contains("http://www.sogou.com/antispider/index.php");
		return url.contains("http://www.sogou.com/antispider/?from");
	}

	@Override
	public String getTargetUrl(String url, String html) {
		return "http://www.sogou.com/antispider/thank.php";
	}

	@Override
	public String handleVerifyCode(String url, String html) {
		String vUrl = url;
		String vHtml = downloadHtml(vUrl, getHopHtmlEncoding());
		return super.handleVerifyCode(vUrl, vHtml);
	}	

	@Override
	/**
	 * 提交验证码
	 * 
	 * @param curi
	 * @return 返回跳转之后的网页链接
	 */
	public String submitVerifyCode(String targetUrl, String html) {

		String hopUrl = null;
		HttpClientParams.setRedirecting(httpclient.getParams(), false);
		HttpPost httpPost = new HttpPost(targetUrl);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
		httpPost.setHeader("Host", "www.sogou.com");
		httpPost.setHeader("Origin", "http://www.sogou.com");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		String verifyCode = getVerifyCode(targetUrl, html);
		String Rcode = getRcode(html);
		nvps.add(new BasicNameValuePair("c", verifyCode));
		nvps.add(new BasicNameValuePair("r", Rcode));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		HttpResponse response = null;
		try {
			response = httpclient.execute(httpPost, localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (response.getFirstHeader("Location") != null) {
			hopUrl = response.getFirstHeader("Location").getValue();
		}
		String tail = null;
		if (hopUrl != null) {
			byte[] bs = null;
			try {
				bs = hopUrl.getBytes("ISO-8859-1");
				hopUrl = new String(bs, "gbk");
				Matcher m = PATTERN_KEY.matcher(hopUrl);
				while (m.find()) {
					hopUrl = m.group(1);
					tail = m.group(2);
				}
				hopUrl = URLEncoder.encode(hopUrl, "gb2312");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.info("encode sogou hopurl UnsupportedEncodingException="+e.toString());
			}
			hopUrl = "http://www.sogou.com/web?query=" + hopUrl + tail;
		}
		return hopUrl;
	}

}
