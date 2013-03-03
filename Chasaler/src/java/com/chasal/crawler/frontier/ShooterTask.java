package com.chasal.crawler.frontier;

import java.text.SimpleDateFormat;

import com.chasal.crawler.utils.HttpClientHelper;

public class ShooterTask extends Task{

	public ShooterTask(){
		super();
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
		while(true){
			 String url=QueueFrontier.getInstance().getProcessingUrl();
			    if(url!=null){
			    	System.out.println("下载链接"+url);
			    	String html=HttpClientHelper.downloadHtml(url, "", "");
			    	QueueFrontier.getInstance().addFinishedQueue(url);
			    	System.out.println("下载的网页内容为："+html);
			    	
			    }
			    else{
			    	try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
		}
		   
			
		
    }


}

