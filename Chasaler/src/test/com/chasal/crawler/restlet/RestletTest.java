package com.chasal.crawler.restlet;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestletTest {

	public static void main(String[] args) {   
	    try {   
	        // Create a new Component.   
	        Component component = new Component();   
	  
	        // Add a new HTTP server listening on port 8182.   
	        component.getServers().add(Protocol.HTTP, 8182);   
	  
	        // Attach the sample application.   
	        component.getDefaultHost().attach("/tasks/hunter",new HunterApplication());
	  
	        // Start the component.   
	        component.start();   
	    } catch (Exception e) {   
	        // Something is wrong.   
	        e.printStackTrace();   
	    }   
	}
}
