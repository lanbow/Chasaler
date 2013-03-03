package com.chasal.crawler.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractEngine implements IEngine {
	
	protected int pageLimit = 20;
	
	protected  Pattern PATTERN_META;//判断链接是否为该搜索引擎的列表页链接时用到的正则表达式
	protected  Pattern PATTERN_PAGE;//提取关键词和翻页时候用到的正则表达式
	protected String encoding = "utf-8";//提取出关键词的编码
	protected int keywordIndex;//提取关键词时正则表达式抽取的位置
	protected String engine_name;
	protected String domain;
	protected String tempaltePath;
	
//	protected static JDBCHelper jdbcHelper = new JDBCHelper();
	
	@Override
	public boolean isMetaUrl(String url) {
		Matcher m = PATTERN_META.matcher(url);
		if(m.matches()){
			return true;
		}
		return false;	
	}


	@Override
	public String getKeyword(String url) {
		String keyWord = "";
		Matcher m = PATTERN_PAGE.matcher(url);
		while (m.find()) {
			keyWord = m.group(keywordIndex);
		}
		try {
			keyWord = java.net.URLDecoder.decode(keyWord, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return keyWord;
	}
	
	@Override
	public int getLimitedPage(){
		return this.pageLimit;
	}
	
	protected int getLimitedPage(String engineName) {	
//		String sql = "select engine_page from crawl_search_engine where engine_name=?";
//		Object obj =  jdbcHelper.queryBySql(sql,new Object[] { engineName });
//		if(obj != null){
//			return Integer.parseInt(obj.toString());
//		}
//		else return 20;
		return 60;
	}
	
	@Override
	public  boolean isEngineMatch(String engine){
		return engine.equals(engine_name);
	}
	
	@Override
	public  String getTemplate(){
		 BufferedReader br = null;
	        try {
	            br = new BufferedReader(new FileReader(tempaltePath));
	        } catch (FileNotFoundException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        String s = "";
	        StringBuilder sb = new StringBuilder("");
	        try {
	            while ((s = br.readLine()) != null) {
	                sb.append(s + "\r\n");
	            }
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return sb.toString();
	}
	
	@Override
	public  String getDomain(){
		return domain;
	}
	@Override
	public  String getTemplateRegex(){
		return PATTERN_META.pattern();
	}

}
