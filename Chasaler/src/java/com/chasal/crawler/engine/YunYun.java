package com.chasal.crawler.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class YunYun {
    
    public static DefaultHttpClient httpclient = new DefaultHttpClient();
    public static CookieStore cookieStore = new BasicCookieStore();
    public static HttpContext localContext = new BasicHttpContext();
    
    public static String getYunYunUrl(String keyword,int page,String bi){
        String url=null;
        long time=System.currentTimeMillis();
        try {
            keyword=URLEncoder.encode(keyword,"gb2312");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        url="http://weibo.yunyun.com/Ajax/Search.php?o=json&q="+keyword+"&srt=mb&c="+page*10+"&l=10&bi="+bi+"&rsl=1&ts="+time+"&usg="+getCookies();
        return url;
        
    }
    //5849538268771334817
    public static String getBi(String jason){
        String bi=null;
        
         Pattern pattern = Pattern.compile(",\"((\\d){19})\",");    
            Matcher matcher = pattern.matcher(jason);
            while (matcher.find()) {
                bi= matcher.group(1);
            }
        return bi;
    }
    //yrssid=1428113173188867729;
    public static String getCookies(){
        String yrssid=null;
        String repose=localContext.getAttribute("http.response").toString();
         Pattern pattern = Pattern.compile("yrssid=(\\d+);");    
            Matcher matcher = pattern.matcher(repose);
            while (matcher.find()) {
                yrssid= matcher.group(1);
            }
        return yrssid;
    }
    
    public static String getHttpHtml(String url) {
    
        HttpGet httpGet = new HttpGet(url);
//         httpGet.setHeader("Content-Type","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//          httpGet.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
          httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17");
         // httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
//          httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
//          httpGet.setHeader("Cache-Control", "max-age=0");
          httpGet.setHeader("Host", "weibo.yunyun.com");
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpGet,localContext);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String html = null;
        HttpEntity entity = response.getEntity();
        try {
            html = EntityUtils.toString(entity, "utf-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    return html;
    }
    
    
    
    /**
     * 
     * 将 \u9ed1\u8272\u7248\u56fe 编码的字符串，转为可阅读的文字
     * 用于新浪微博 正文解析
     * @param dataStr
     * @return
     * 
     * @since  crawler_weibo　Ver1.0
     */
    public static String decodeUnicode(String dataStr) {
        if(dataStr == null){
            return null;
        }
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");    
        Matcher matcher = pattern.matcher(dataStr);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            dataStr = dataStr.replace(matcher.group(1), ch + "");    
        }
        
        Pattern pattern1 = Pattern.compile("(&#(\\d+);)");    
        Matcher matcher1 = pattern1.matcher(dataStr);
        char ch1;
        while (matcher1.find()) {
            ch1 = (char) Integer.parseInt(matcher1.group(2), 10);
            dataStr = dataStr.replace(matcher1.group(1), ch1 + "");    
        }
       // dataStr=dataStr.replaceAll("\\[\\[\\[", "廖");
        //dataStr=dataStr.replaceAll("\\\\/", "/").replaceAll("\\\\\"", "\"");
        return dataStr;
    } 
    public static String getFile(String filename) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String s = "";
        StringBuilder sb = new StringBuilder("");
        try {
            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static void extractJSON(String content){
        
        content=content.replaceAll("\\\\/", "/");
        // Pattern pattern = Pattern.compile("\\[\\[\\[[^(\\],\\[\\[\\[)]*\\],\\[\\[\\[");   
         Pattern pattern = Pattern.compile("\\[\\[\\[((?:(?!\\[\\[\\[).)*)");
        //Pattern pattern = Pattern.compile("(.(?!\\d+,\\d+\\]))*");
         Matcher matcher = pattern.matcher(content);
         while (matcher.find()) {
             content=matcher.group(1);
             System.out.println("");
             System.out.println(content);
             System.out.println("帖子url："+content.split(",")[0].replaceAll("\\\\\"", "\""));
             System.out.println("发帖人："+content.split(",")[1].replaceAll("\\\\\"", "\""));
             System.out.println("帖子内容："+content.split(",")[2].replaceAll("\\\\\"", "\"").replaceAll("<([^>])*>", ""));
             
             Pattern pattern1 = Pattern.compile("(1(\\d){9}),");
             Matcher matcher1 = pattern1.matcher(content);
             while (matcher1.find()) {
                 SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                 long time=Integer.parseInt(matcher1.group(1));
                 time*=1000;
                System.out.println("发帖时间："+sdf.format(new Date(time))); 
             }
             
             //String content1="\"http://weibo.com/1420174783\",86,5,null,false,true,0";
             Pattern pattern2 = Pattern.compile("\"([^\"]*.(jpg|bmp|gif))\",\"[^\"]*\",(\\d+),(\\d+),[^,]*,[^,]*,[^,]*,[^,]*");
             Matcher matcher2 = pattern2.matcher(content);
             while (matcher2.find()) {
                 System.out.println("图片链接:"+matcher2.group(1));
                System.out.println("转发量:"+matcher2.group(3));
                System.out.println("评论量:"+matcher2.group(4));        
             }
             //content="1361923299,\"http://weibo.com\",[\"http://weibo.com/1745573472/zl4xDp29U\",\"急诊科女超人于莺\",\"大清早收到那么长的一条微信\",null,null,null,\"http://weibo.com/1745573472\",934,129,null,false,true,0]";
            //content= content.replaceAll("\\\\/", "/");
             Pattern pattern3 = Pattern.compile("1(\\d){9},\"[^\"]*\",\\[\"([^\"]*)\",\"([^\"]*)\",\"(.*)\",[^,]*,[^,]*,[^,]*,\"http:.*\",(\\d+),(\\d+),[^,]*,[^,]*,[^,]*,[^,]*\\]");
             Matcher matcher3 = pattern3.matcher(content);
//while(matcher3.find()){
//     System.out.println("被转发帖子url:"+matcher3.group());
//}
          
             while (matcher3.find()) {
                 System.out.println("被转发帖子url:"+matcher3.group(2));
                 System.out.println("被转发用户昵称:"+matcher3.group(3));
                 System.out.println("被转发帖子内容:"+matcher3.group(4).replaceAll("<([^>])*>", ""));
                 System.out.println("被转发帖子转发量:"+matcher3.group(5));
                 System.out.println("被转发帖子评论量:"+matcher3.group(6));
             }
             
             
             
             //System.out.println(content);   
         }
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        System.out.println(getHttpHtml("http://weibo.yunyun.com/Weibo.php?p=1&q=%E6%9D%8E%E5%8F%8C%E6%B1%9F"));
//        String url="";
//        String keyword="李双江";
//        int page=2;
//        System.out.println(getYunYunUrl(keyword,page));
//        System.out.println(getHttpHtml(url));
        String content=getFile(".\\content.txt");
//        content=decodeUnicode(content);
//        System.out.println(content);
//        extractJSON(content);
        System.out.println(getBi(content));
    }
}