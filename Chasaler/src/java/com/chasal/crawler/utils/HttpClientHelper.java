package com.chasal.crawler.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientHelper {

	public static PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
	public static DefaultHttpClient httpclient = new DefaultHttpClient(cm);
	public static CookieStore cookieStore = new BasicCookieStore();
	public static HttpContext getLocalContext() {
		return localContext;
	}

	public static void setLocalContext(HttpContext localContext) {
		HttpClientHelper.localContext = localContext;
	}

	public static HttpContext localContext = new BasicHttpContext();

	public static String getRedirectUrl(String url) {

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

		try {
			httpGet.setURI(new URI(url));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
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
			httpGet.releaseConnection();
			return url;
		} else if (code == 302 || code == 301) {

			newUrl = response.getFirstHeader("Location").getValue();
			if (newUrl != null && newUrl.contains("http://zhidao.baidu.com")) {
				int index = newUrl.indexOf("html");
				newUrl = newUrl.substring(0, index + 4);
			}

		} else {
			httpGet.releaseConnection();
			return null;
		}
		httpGet.releaseConnection();
		// httpclient.getConnectionManager().shutdown();
		if (newUrl.contains("/baidu.php?sc")) {
			// newUrl=getRedirectUrl(url);
			newUrl = getRedirectUrl("http://www.baidu.com" + newUrl);
		}
		if (newUrl.contains("utm_source")) {
			newUrl=newUrl.substring(0,newUrl.indexOf("utm_source")-1);
		}
		return newUrl;

	}
	
	public static String downloadHtml(String url,String encoding,String host) {
		String html = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		//HttpClientParams.setRedirecting(httpclient.getParams(), true);
		if(host!=null){
			httpGet.setHeader("Host", host);
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
		HttpEntity entity = response.getEntity();
		try {
			html = EntityUtils.toString(entity,encoding);
			EntityUtils.consume(entity);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpGet.releaseConnection();
		return html;
	}
	
	public static String getRedirectUrlBypost(String url,Map<String,String>data) {

		String newUrl = null;
		int code;
		HttpClientParams.setRedirecting(httpclient.getParams(), false);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> set =data.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String key=it.next();
			nvps.add(new BasicNameValuePair(key,data.get(key)));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpPost,localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		code = response.getStatusLine().getStatusCode();
		if (code == 200) {
			httpPost.releaseConnection();
			return url;
		} else if (code == 302 || code == 301) {		
			newUrl = response.getFirstHeader("Location").getValue();
		} else {
		}
		httpPost.releaseConnection();
		return newUrl;
	}
	
public static String downloadHtmlByPost(String url,Map<String,String> map,String encoding){
		
		String html=null;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.89 Safari/537.1");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> set =map.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String key=it.next();
			nvps.add(new BasicNameValuePair(key,map.get(key)));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps,"utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpPost,localContext);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		HttpEntity entity = response.getEntity();
		try {
			html = EntityUtils.toString(entity,encoding);
			EntityUtils.consume(entity);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpPost.releaseConnection();
		return html;	
	}

}
