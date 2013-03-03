package com.chasal.crawler.utils;

import java.util.HashMap;
import java.util.Map;
import com.chasal.crawler.Chasaler;
import com.chasal.crawler.engine.IEngine;


public class TemplateCache {
	
	private static TemplateCache template = null;
	
	private Map<String, Map<String, XQueryTemplate>> templateCache;

	public static TemplateCache getInstance(){
		if(template == null){
			template = new TemplateCache();
		}
		return template;
	}
	
	private TemplateCache() {
		templateCache = new HashMap<String, Map<String, XQueryTemplate>>();
		//this.jdbcTemplate = jdbcTemplate;
		//初始化templateCache，从数据库中将数据读入其中,形式为<domain, <pattern, template>>，减少查找代价
		insertTemplate();
	}

	public Map<String, Map<String, XQueryTemplate>> getTemplateCache() {
		return templateCache;
	}

	public void setTemplateCache(Map<String, Map<String, XQueryTemplate>> templateCache) {
		this.templateCache = templateCache;
	}
	
	public void insertTemplate(){
	
		//List list = this.jdbcTemplate.queryForList(sql);
	
		IEngine[] engines=Chasaler.engines;
		for(int i = 0; i < engines.length; i++){
			IEngine engine = engines[i];
			if(templateCache.containsKey(engine.getDomain())){
				templateCache.get(engine.getDomain()).put(engine.getTemplateRegex(), 
						new XQueryTemplate("gb2312", engine.getTemplate(), engine.getDomain()));
			}
			else{
				Map<String, XQueryTemplate> temp = new HashMap<String, XQueryTemplate>();
				XQueryTemplate xt = new XQueryTemplate("gb2312", engine.getTemplate(), engine.getDomain());
				temp.put(engine.getTemplateRegex(), xt);
				templateCache.put(engine.getDomain(), temp);
			}
		}
		
	}
	
	public void putTemplate(String domain, String domain_id, String url_regex, String content){
		if(templateCache.containsKey(domain)){
			templateCache.get(domain).put(url_regex, new XQueryTemplate("gb2312", content, domain));
		}
		else{
			Map<String, XQueryTemplate> temp = new HashMap<String, XQueryTemplate>();
			XQueryTemplate xt = new XQueryTemplate("gb2312", content, domain);
			temp.put(url_regex, xt);
			templateCache.put(domain, temp);
		}
	}
	
	public void deleteTemplate(String domain, String domain_id, String url_regex, String content){
		if(templateCache.containsKey(domain)){
			templateCache.remove(domain);
		}
		else{
			
		}
	}
	
	
	public static void main(String[] args) {
		// for test, pass
		TemplateCache template = TemplateCache.getInstance();
		template.putTemplate("a", "b", "c", "d");
		template.deleteTemplate("a", "b", "c", "d");
	}
}
