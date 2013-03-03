package com.chasal.crawler.restlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.chasal.crawler.engine.IEngine;
import com.chasal.crawler.frontier.HunterTask;
import com.chasal.crawler.frontier.QueueFrontier;
import com.chasal.crawler.frontier.ThreadPool;
import com.chasal.crawler.Chasaler;

/**
 * Resource which has only one representation.
 */
public class HunterResource extends ServerResource {

	String taskName = "";
	String engineName = null;

	@Override
	protected void doInit() throws ResourceException {
		this.taskName = (String) getRequest().getAttributes().get("taskName");
	}

	@Get
	public Representation represent(Variant variant) throws ResourceException {
        Representation representation;   
        representation = new WriterRepresentation(MediaType.TEXT_HTML) {
            public void write(Writer writer) throws IOException {
               writeHtml(writer);
            }
        };
    representation.setCharacterSet(CharacterSet.UTF_8);
    return representation;
}

	@Post
	public Representation acceptTask(Representation entity) {
		Form form = new Form(entity);
		Representation representation = new StringRepresentation(
				"System has received your post_request,"
						+ "your task's name is " + taskName + ",get id= "
						+ form.getFirstValue("taskName"), MediaType.TEXT_PLAIN);
		engineName = form.getFirstValue("engineName");
		String keys = form.getFirstValue("keyword");
		String timeLimit = form.getFirstValue("timeLimit");
		System.out.println("engine: "+engineName+" keys:"+keys+" limit:"+timeLimit);
		List<String> urls = new ArrayList<String>();
		int limit = 0;
		if (timeLimit != null) {
			limit = Integer.parseInt(timeLimit);
		}
		if (engineName != null && !keys.isEmpty()) {
			String[] keywords = keys.split("##");
			IEngine engine = getEngine(engineName);
			if (engine != null) {
				String url;
				for (String keyword : keywords) {
					url = engine.generateURL(keyword, limit);
					urls.add(url);
				}
			}

		}
		HunterTask task=new HunterTask(urls);
		task.setTaskId(System.currentTimeMillis());
		//QueueFrontier.getInstance().addBatchWorkingQueue(urls);
		ThreadPool.getInstance().addTask(task);
		return representation;
	}

	public IEngine getEngine(String engineName) {
		IEngine engine = null;
		for (IEngine e : Chasaler.engines) {
			if (e.isEngineMatch(engineName)) {
				engine = e;
				break;
			}
		}
		return engine;

	}
	
	protected void writeHtml(Writer writer) {
	   
        String baseRef = getRequest().getResourceRef().getBaseRef().toString();
        if(!baseRef.endsWith("/")) {
            baseRef += "/";
        }
        PrintWriter pw = new PrintWriter(writer); 
        pw.println("<!DOCTYPE html>");
        pw.println("<html>");
        pw.println("<head><title>Task Management</title>");
        pw.println("<base href='"+baseRef+"'/>");
        pw.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getStylesheetRef() + "\">");
        System.out.println(getStylesheetRef());
        pw.println("</head>\n<body>");
        pw.println("<h1>任务添加器</h1>");   
        pw.println("<form method='POST'>");
        pw.println("<div><label for='job'>任务名称:</label><input type='text' size='10' name='taskName'></br><label for='key'>关键词:</label><input type='text' size='20' name='keyword'></br>");
        pw.println("<table><tr><td>搜索引擎: </td><td><select name='engineName' style='HEIGHT: 80px; WIDTH: 70px' multiple><OPTION VALUE='baidu' selected='selected'>百度</OPTION><OPTION VALUE='google'>谷歌</OPTION><OPTION VALUE='sogou'>搜狗</OPTION><OPTION VALUE='soso'>搜搜</OPTION></select></td><td>时间限制: </br></td><td><select name='timeLimit' style='HEIGHT: 80px; WIDTH: 70px' multiple><OPTION VALUE='1' selected='selected'>一天</OPTION><OPTION VALUE='7'>一周</OPTION><OPTION VALUE='30'>一月</OPTION><OPTION VALUE='365'>一年</OPTION></td></tr></table>");
        pw.println("<input type='submit' id='action' value='add'>");
        pw.println("</div></form>");
        pw.println("</body>");
        pw.println("</html>");
        pw.flush();
    }
	
	protected String getStaticRef(String resource) {
        String rootRef = getRequest().getRootRef().toString();
        return rootRef + "/crawler/static/" + resource;
    }

    protected String getStylesheetRef() {
        return getStaticRef("task.css");
    }

}