package com.chasal.crawler.verify;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * 验证码工具类
 * 还原 验证码链接对应的 原始链接
 *
 * @author   xv
 * @version  2013-1-18
 */
public class VerifyHelper {
    
    private static Pattern[] PATTERN_EMBEDED={
        Pattern.compile("http://verify.baidu.com/vcode\\?(.*)%20LLR%20.*"),
        Pattern.compile("http://verify.baidu.com/vcode\\?(.*)"),
        Pattern.compile("http://www.google.com.hk/sorry/\\?continue=(.*)"),
        Pattern.compile("http://www.sogou.com/antispider/\\?from=(.*)")
            };
    
    /**
     * 获取包含在验证链接中的真实种子，有可能需要进行编解码处理
     * @param url 验证链接
     * @return 种子链接或列表页链接
     */
    public static String getEmbededUrl(String url){
        String embed=null;
        for(int i=0;i<PATTERN_EMBEDED.length;i++){
            Matcher m=PATTERN_EMBEDED[i].matcher(url);
            if(m.matches()){
                embed = m.group(1);
                //Google链接需要编解码处理，才可用
                if(embed.contains("http://www.google.com.hk/search")){
                    try {
                        embed=URLDecoder.decode(embed,"gb2312");
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }               
                }
                //搜狗链接需要编解码处理，才可用
                if(embed.contains("%2fweb%3Fquery%3d")){
                    try {
                        embed=URLDecoder.decode(embed,"gb2312");
                        Matcher m1=null;
                        if(embed.contains("site")){
                             m1=Pattern.compile("/web\\?query=(.*)site.*").matcher(embed);
                        }
                        else{
                            m1=Pattern.compile("/web\\?query=(.*)&tsn.*").matcher(embed);
                        }
                        String key=null;
                        if(m1.matches()){
                            key=m1.group(1);                            
                        }
                        embed=embed.replace(key, URLEncoder.encode(key, "gb2312"));         
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    embed="http://www.sogou.com"+embed;
                }
                return embed;
            }
        }
        return embed;
    }
    
    public static void main(String[] args){
        String input = "http://verify.baidu.com/vcode?http://www.baidu.com/s?wd=%E4%B9%9D%E9%98%B3%26%E8%B1%86%E6%B5%86%E6%9C%BA+site%3Ajfdaily.com&pn=0&ie=utf-8&lm=1";
        System.out.println(getEmbededUrl(input));
    }
    
}
