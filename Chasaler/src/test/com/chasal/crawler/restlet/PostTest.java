package com.chasal.crawler.restlet;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

public class PostTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClientResource client = new ClientResource("http://localhost:8182/tasks/hunter/add/mengnniu");
		 Form form = new Form();   
		 form.add("taskId", "百度_"+System.currentTimeMillis());
		 form.add("engineName", "baidu");
		 form.add("keyword", "蒙牛&特仑苏##伊利&纯牛奶");
		 form.add("timeLimit","1");
		 String reply = null;
		try {
			 reply = client.post(form.getWebRepresentation()).getText();
		} catch (ResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		 System.out.println("收到回复："+reply);   
	}

}
