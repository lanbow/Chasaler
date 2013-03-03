package com.chasal.crawler.restlet;  
  
import java.io.File;

import org.restlet.Application;  
import org.restlet.Restlet;  
import org.restlet.resource.Directory;
import org.restlet.routing.Router;  
public class HunterApplication extends Application {   
    @Override    
    public synchronized Restlet createRoot() {     
    
        Router router = new Router(getContext());     
        router.attach("/add/{taskName}",HunterResource.class);     
        Directory staticDir = new Directory(getContext(),getStaticUri()); 
        staticDir.setListingAllowed(true);
        staticDir.setDeeplyAccessible(true);
        staticDir.setNegotiatingContent(true);
        router.attach("/crawler/static/",staticDir);
        return router;     
    }  
    
    public String getStaticUri(){
    	String url=null;
    	File directory = new File("");  
    	url=directory.getAbsolutePath().replaceAll("\\\\", "/");
        url = "file:///"+url+"/src/resources/style";
        System.out.println(url); 
    	return url;
    }
}    