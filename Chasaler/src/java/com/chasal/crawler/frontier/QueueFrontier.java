package com.chasal.crawler.frontier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class QueueFrontier {
	
	private static QueueFrontier instance = QueueFrontier.getInstance();
	private static List<String> WorkingQueue = Collections
            .synchronizedList(new LinkedList<String>());
    private static List<String> FinishedQueue = Collections
            .synchronizedList(new LinkedList<String>());
    
    public static synchronized QueueFrontier getInstance() {
        if (instance == null)
            return new QueueFrontier();
        return instance;
    }
    
    public synchronized boolean addWorkingQueue(CrawlUri uri){
    	
    	boolean addSucess=false;
    	String url=uri.getUrl();
    	if(!WorkingQueue.contains(url)){
    		WorkingQueue.add(url);
    		addSucess=true;
    	}
    	return addSucess;	
    }
    public synchronized void addBatchWorkingQueue(List<CrawlUri> uris){
    	String url;
    	for(CrawlUri uri:uris){
    		url=uri.getUrl();
    		if(!WorkingQueue.contains(url)){
        		WorkingQueue.add(url);
        	}
    	}
    }
    
    public synchronized String getProcessingUrl(){
    	String url=null;
    	if(!WorkingQueue.isEmpty()){
    		url=WorkingQueue.remove(0);
    	}
    	return url;	
    }
        
    public synchronized boolean addFinishedQueue(String url){
    	boolean addSucess=false;
    	if(!FinishedQueue.contains(url)){
    		FinishedQueue.add(url);
    		addSucess=true;
    	}
    	return addSucess;	
    }
    
    public synchronized boolean removeFinishedQueue(String url){
    	boolean removeSucess=false;
    	if(FinishedQueue.contains(url)){
    		removeSucess=FinishedQueue.remove(url);
    	}
    	return removeSucess;	
    }

}
