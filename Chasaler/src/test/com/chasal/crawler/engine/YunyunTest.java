package com.chasal.crawler.engine;

import com.chasal.crawler.engine.YunYunUrl;
import com.chasal.crawler.utils.HttpClientHelper;

public class YunyunTest {
	
	public static void startJob(String keyword){
		String url=YunYunUrl.getSeed(keyword);
		String html=HttpClientHelper.downloadHtml(url, "gb2312", "weibo.yunyun.com");
		System.out.println(html);
		int count=1;
		while(html!=null){
			count++;
			String bi=YunYunUrl.getBi(html);
			url=YunYunUrl.getNextUrl(keyword, count, bi);
			html=YunYunUrl.getJason(url);
		}	
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String keyword="李双江";
		startJob(keyword);
		
	}

}
