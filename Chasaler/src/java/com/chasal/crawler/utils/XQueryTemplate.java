package com.chasal.crawler.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class XQueryTemplate {
    
    private String content; 
    private String encoding;    
    private String domain;

    
    public XQueryTemplate(){
        
    }
    
    /**
     * 从数据库 读取模板
     * @param encoding
     * @param content
     * @param domain
     * @param domain_id
     */
    public XQueryTemplate(String encoding, String content, String domain) {
        super();
        this.encoding = encoding;
        this.content = content;
        this.domain = domain;
    }

	/**
     * 从文件读取模板
     * @param file
     */
    public XQueryTemplate(File file){
        String encoding = "gb2312";
        String content = "";
        if (file == null) {
            return ;
        }

        FileInputStream is = null;
        InputStreamReader inReader = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = new FileInputStream(file);
            inReader = new InputStreamReader(is, "utf-8");
            reader = new BufferedReader(inReader);
            String line ;
            while((line=reader.readLine())!=null) {
                if(line.indexOf("declare variable $encoding") != -1){
                    int index = line.indexOf("=");
                    encoding = line.substring(index + 1, line.length() - 1).trim();
                }
                sb.append(line);
            }
            content = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(is != null)
                    is.close();
                if(inReader != null)
                    inReader.close();
                if(reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.setContent(content);
        this.setEncoding(encoding);
    }

    
    public String getContent() {
        return content;
    }

    
    public void setContent(String content) {
        this.content = content;
    }

    
    public String getEncoding() {
        return encoding;
    }

    
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
    @Override
    public String toString() {
        return "XQueryTemplate [content=" + content + ", encoding=" + encoding + "]";
    }

}
