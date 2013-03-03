package com.chasal.crawler.frontier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.protocol.HttpContext;

public class CrawlUri {
	String url;
	String html;
	String host;
	String encoding;
	HttpContext context;
	String fetchTime;
	String viaUrl;
    Map<String,Object> dataStore=new HashMap<String,Object>();
    
    public  CrawlUri(String url,String viaUrl){
    	this.url =url;
    	this.viaUrl =viaUrl;
    	setHost();
    }
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getHost() {
		return host;
	}
	
	public void setHost() {
		Pattern pattern = Pattern.compile("(http://[^/]*)/.*");
		Matcher m = pattern.matcher(this.url);
		while (m.find()) {
			this.host = m.group(1);
		}
	}
	
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public HttpContext getContext() {
		return context;
	}
	public void setContext(HttpContext context) {
		this.context = context;
	}
	public String getFetchTime() {
		return fetchTime;
	}
	public void setFetchTime(String fetchTime) {
		this.fetchTime = fetchTime;
	}
	public String getViaUrl() {
		return viaUrl;
	}
	public void setViaUrl(String viaUrl) {
		this.viaUrl = viaUrl;
	}
	public Map<String, Object> getDataStore() {
		return dataStore;
	}
	public void setDataStore(Map<String, Object> dataStore) {
		this.dataStore = dataStore;
	}

}
