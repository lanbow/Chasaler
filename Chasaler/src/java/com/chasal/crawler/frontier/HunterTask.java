package com.chasal.crawler.frontier;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import com.chasal.crawler.extractor.EngineExtractor;
import com.chasal.crawler.utils.HttpClientHelper;
import com.chasal.crawler.utils.XQueryTemplate;

public class HunterTask extends Task{
	
	protected List<String> seeds=null;

	public HunterTask(List<String> seeds){
		super();
		this.seeds=seeds;	
	}
	@Override
	public Task[] taskCore() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean useDb() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean needExecuteImmediate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String CreateTime=sdf.format(getGenerateTime());
		return "任务序号："+this.getTaskId()+" 创建时间："+CreateTime;
	}
	
	@Override
	public void run() {
        /**
        * 相关执行代码
        * 
        * beginTransaction();
        * 
        * 执行过程中可能产生新的任务 subtask = taskCore();
        * 
        * commitTransaction();
        * 
        * 增加新产生的任务 ThreadPool.getInstance().batchAddTask(taskCore());
        */
		for(String url:seeds){
			System.out.println(this.info() +" 下载链接:"+url);
			String html=HttpClientHelper.downloadHtml(url, "gb2312", null);
			List<String> newUrls=extractHtml(url,html);
//			QueueFrontier.getInstance().addBatchWorkingQueue(newUrls);
		}
    }
	
	public List<String> extractHtml(String url,String html){
		List<String> urls=new ArrayList<String>();
		EngineExtractor extractor=new EngineExtractor();
		CrawlUri sUri=new CrawlUri(url,"");
		extractor.extract(sUri, html);
		return urls;
	}


}
