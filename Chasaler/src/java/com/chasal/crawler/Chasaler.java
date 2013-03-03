package com.chasal.crawler;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.chasal.crawler.engine.BaiduUrl;
import com.chasal.crawler.engine.GoogleUrl;
import com.chasal.crawler.engine.IEngine;
import com.chasal.crawler.engine.TianyaUrl;
import com.chasal.crawler.frontier.QueueFrontier;
import com.chasal.crawler.frontier.ThreadPool;
import com.chasal.crawler.restlet.HunterApplication;
import com.chasal.crawler.verify.BaiduManualVerify;
import com.chasal.crawler.verify.GoogleManualVerify;
import com.chasal.crawler.verify.IVerify;
import com.chasal.crawler.verify.SogouManualVerify;

public class Chasaler {
	
	protected Component component;
	protected ThreadPool threadPool=ThreadPool.getInstance();
	protected QueueFrontier frontier=QueueFrontier.getInstance();

	public static IVerify[] verifys = {new BaiduManualVerify(),
		new SogouManualVerify(),new GoogleManualVerify()
	};
	public static IEngine[] engines={new BaiduUrl(),new GoogleUrl(),new TianyaUrl()};
	
	public void initialise(){
		
         component = new Component();   
        component.getServers().add(Protocol.HTTP, 8182); 
        component.getClients().add(Protocol.FILE);
        component.getDefaultHost().attach("/tasks/hunter",new HunterApplication());
        try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}   
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Chasaler().initialise();

	}

}
