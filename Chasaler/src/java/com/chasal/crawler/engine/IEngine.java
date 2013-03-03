package com.chasal.crawler.engine;

public interface IEngine {

	/**
	 * 判断所给链接是不是该搜索引擎的列表页链接
	 * @param url
	 * @return
	 */
	public abstract boolean isMetaUrl(String url);

	/**
	 * 根据关键词和时间限制生成元搜索种子链接
	 * @param Keyword
	 * @param time_limited
	 * @return
	 */
	public abstract String generateURL(String Keyword, int time_limited);

	/**
	 * 从给出的链接当中提取出搜索关键词
	 * @param url
	 * @return
	 */
	public abstract String getKeyword(String url);
	

	/**
	 * 从给出的链接当中提取出搜索关键词
	 * @param url
	 * @return
	 */
	public abstract String getTemplate();
	
	/**
	 * 从给出的链接当中提取出翻页数
	 * @param url
	 * @return
	 */
	public abstract int getPage(String url);
	/**
	 * 获取翻页限制页数
	 * @return
	 */
	public abstract int getLimitedPage();
	
	/**
	 * 根据名称来选择搜索引擎
	 * @param engine
	 * @return
	 */
	public abstract boolean isEngineMatch(String engine);
	
	public abstract String getDomain();
	
	public abstract String getTemplateRegex();
	

}