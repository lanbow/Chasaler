package com.chasal.crawler.extractor;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.xerces.dom.DeferredElementImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.chasal.crawler.Chasaler;
import com.chasal.crawler.engine.GoogleUrl;
import com.chasal.crawler.engine.IEngine;
import com.chasal.crawler.frontier.CrawlUri;
import com.chasal.crawler.frontier.QueueFrontier;
import com.chasal.crawler.utils.HttpClientHelper;
import com.chasal.crawler.utils.TemplateCache;
import com.chasal.crawler.utils.XQueryHelper;
import com.chasal.crawler.utils.XQueryTemplate;
import com.chasal.crawler.verify.IVerify;


public class EngineExtractor {

	private float light_rate=(float) 0.5;
	private XQueryHelper xqueryHelper = new XQueryHelper();
	
private TemplateCache templateCache = TemplateCache.getInstance();
    
    public TemplateCache getTemplateCache() {
		return templateCache;
	}

	public void setTemplateCache(TemplateCache templateCache){
    	this.templateCache = templateCache;
    }
	
	/**
	 * 覆盖ExtractTextByTemplate的extract()抽取方法，转为元搜索特有的处理流程
	 * 每处理一个链接，先判断是否为列表页，是就调用对应的模板，通过dom树进行解析
	 * 如果不是列表页，即为普通网页链接，那么就不对网页进行解析，直接保存网页源文件和其他元搜索信息
	 */
	public boolean extract(CrawlUri curi, String html) {
		 //System.out.println("进入元搜索处理流程的链接有：" + curi.getURI());
		// 是元搜索的列表页，相当于种子一样，则调用模板解析
		if (isMetaUrl(curi.getUrl())) {
			// 1.根据URI，获得解析模板
			XQueryTemplate template = this.getTemplate(curi);
			if (template == null) {
				return true;
			}
			// 2.解析内容
			Document result = xqueryHelper.parseHTML(html, template);
			// 3.根据不同的模板类型，处理解析结果，同时将domain信息传过去
			this.extract(curi, result);
		}
//		// 请求网页需要验证码输入的抽取
		else if (needVerify(curi.getUrl())) {
			this.verifyExtract(curi, html);
		}
		// 是普通网页，则直接保存网页源文件
		else {
			this.commonExtract(curi, html);
		}
		return true;
	}

	/**
	 * 列表页抽取流程 将从列表页中抽取出普通网页链接和翻页链接
	 */
	
	public void extract(CrawlUri curi, Document result) {
		// TODO Auto-generated method stub

		// 抽取普通网页链接
		int lightCount=0;
		String matchResult=null;
		Node resultNode =result.getElementsByTagName("result").item(0);
		if(resultNode!=null){
			 matchResult=resultNode.getTextContent();
		}
//		curi.getUsrData().put("expectCount", matchResult);//列表页关键词可以匹配的结果数
		NodeList topicNodes = result.getElementsByTagName("Topic");
		int length=topicNodes.getLength();
		for (int i = 0; i < length; i++) {
			if(topicNodes.item(i).getChildNodes().getLength()==0){
				continue;
			}
			String titleLight = topicNodes.item(i).getChildNodes().item(9)
					.getTextContent();
			if(titleLight!=null){
				titleLight=titleLight.trim();
			}
			String contentLight = topicNodes.item(i).getChildNodes().item(11)
					.getTextContent();
			if(contentLight!=null){
				contentLight=contentLight.trim();
			}
			if((titleLight==null||titleLight.equals(""))&&(contentLight==null||contentLight.equals(""))){
				continue;
			}

			lightCount++;
			String title = topicNodes.item(i).getChildNodes().item(1)
					.getTextContent();
			if(title!=null){
				title=title.trim();
			}

			DeferredElementImpl attr = (DeferredElementImpl) topicNodes.item(i)
					.getChildNodes().item(3);
			String newUrl = attr.getAttribute("href");

			if (!newUrl.startsWith("http://")) {
				String parentUrl = curi.getViaUrl();
				if (parentUrl != null) {
					Pattern pattern = Pattern.compile("(http://[^/]*)/.*");
					Matcher m = pattern.matcher(parentUrl);
					String host = "";
					while (m.find()) {
						host = m.group(1);
					}
					newUrl = host + newUrl;
				}
			}
			// 专门对谷歌链接进行的处理，拿到普通网页链接（包含在谷歌链接当中）
			if (curi.getUrl().contains("http://www.google.com.hk/search")) {
				Matcher match = GoogleUrl.PATTERN_GOOGLE_RECORD.matcher(newUrl);
				if (match.find()) {
					newUrl = match.group(1);
				}
			}
			// 对百度进行重定向
			if (newUrl.contains("http://www.baidu.com/link?url=")
					|| newUrl.contains("http://www.baidu.com/baidu.php?url=")) {
				// newUrl = MovedUrlHelper.getBaiduUrl(newUrl);
				newUrl = HttpClientHelper.getRedirectUrl(newUrl);
			}

			// 对360进行重定向
			else if ((newUrl != null)
					&& (newUrl.contains("http://www.so.com/url?u"))) {
				newUrl = HttpClientHelper.getRedirectUrl(newUrl);
			}

			// 对新浪博客进行重定向,不能使用else if ，因为百度重定向之后出来的链接有可能还需要重定向
			if ((newUrl != null)
					&& (newUrl.contains("http://blog.sina.com.cn/u/"))) {
				newUrl = HttpClientHelper.getRedirectUrl(newUrl);
			}

			if (newUrl == null || newUrl.equals("")) {
				continue;
			}
			if(!shouldAdd(newUrl)){
				continue;
			}

			String pulishTime = topicNodes.item(i).getChildNodes().item(5)
					.getTextContent().trim();

			String contentDigest = topicNodes.item(i).getChildNodes().item(7)
					.getTextContent().trim();
			// 利用抽取出来的内容构造新的uri对象，将其加入到调度器frontier中去进行下一次下载
			addNewCrawlURI(curi, newUrl, pulishTime, title, contentDigest);
		}
		float calculate=((float)lightCount)/((float)length);
		if(calculate<=light_rate){
			//log.info("give up extracting url="+curi.getURI()+", too many unrelated contents with light_rate="+calculate);
			return;
		}
		// 抽取翻页链接
		NodeList pageNodes = result.getElementsByTagName("List");

		for (int i = 0; i < pageNodes.getLength(); i++) {
			DeferredElementImpl attr = (DeferredElementImpl) pageNodes.item(i)
					.getChildNodes().item(1);
			String url = attr.getAttribute("href");
			/**
			 * 专门对谷歌链接进行的处理，去除列表页链接的随机码部分 并将关于时间的请求选项放到链接末尾处，如果保留在连接中间
			 * 会造成请求失败（估计这样类型的连接会被封）
			 */
			if (curi.getUrl().contains("http://www.google.com.hk/search")){

				Matcher m = GoogleUrl.PATTERN_GOOGLE_RANDOM_CODE.matcher(url);
				String regex = "";
				while (m.find()) {
					regex = m.group(1);
					url = url.replaceAll(regex, "");
				}

				Matcher m1 = GoogleUrl.PATTERN_GOOGLE_PAGE_TIME.matcher(url);
				String regex1 = "";
				while (m1.find()) {
					regex1 = m1.group(1);
					url = url.replaceAll(regex1, "");
					url = url + "&as_qdr=" + m1.group(2);
				}
			}
			
			IEngine engine=isEngineMatch(url);
			if(engine!=null){
				int page=engine.getPage(url);
				if(page<=engine.getLimitedPage()){
					addNewCrawlURI(curi, url);
				}
				else{
					//log.info("page out of limitation,give up adding url="+curi.getURI()+"into queue, with page="+page);
					return;
				}
			}
		}
	}

	/**
	 * 构造普通网页的CrawlURI对象，将其加入到调度器中
	 * 
	 * @param curi
	 *            列表页的CrawlURI对象
	 * @param newUri
	 *            模板抽取出来的普通网页的url
	 * @param time
	 *            模板抽取出来的普通网页的发表时间
	 * @param title
	 *            模板抽取出来的普通网页的标题
	 */
	private void addNewCrawlURI(CrawlUri curi, String newUri, String time,
			String title, String digest) {
		CrawlUri sUri=new CrawlUri(newUri,curi.getUrl());
		sUri.getDataStore().put("releaseTime", time);
		sUri.getDataStore().put("titile", title);
		sUri.getDataStore().put("digest", digest);
		add(sUri);

	}

	/**
	 * 构造列表页的CrawlURI对象，将其加入到调度其中
	 * 
	 * @param curi
	 *            列表页CrawlURI对象
	 * @param newUri
	 *            模板抽取出来的新列表页url
	 */
	private void addNewCrawlURI(CrawlUri curi, String newUri) {
		// TODO Auto-generated method stub
		CrawlUri sUri=new CrawlUri(newUri,curi.getUrl());
		add(sUri);
		
	}

	private void add(CrawlUri candidate) {
		QueueFrontier.getInstance().addWorkingQueue(candidate);
	}

	/**
	 * 普通网页抽取流程 直接保存网页源文件，再将在列表页抽取的得到的网页标题，链接，时间，摘要一起保存到数据库中
	 */
	void commonExtract(CrawlUri curi, String html) {
	}

	/**
	 * 如果链接网页需要验证码验证，则采取这种抽取方法
	 * 
	 * @param curi
	 * @param html
	 */
	void verifyExtract(CrawlUri curi, String html) {
		String url = curi.getUrl();
		IVerify verify = isVerifyMatch(url);
//		if (verify != null) {	
//			VerifyController.sendVerifyRequest(controller, url);	
//			try {
//			    // 输入验证码之后跳转得到的网页链接
//                String hopUrl = VerifyHelper.getEmbededUrl(url);
//                if(hopUrl != null){
//                    log.info("add " + hopUrl + " from verify_url " + url);
//                    this.addNewCrawlURI(curi, hopUrl);
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace();
//            }
			
			
			// 进入验证码处理流程
//            CrawlURI candidate = null;// 验证码处理完之后再进入元搜索处理流程的对象
//          String hopUrl = null;
//			hopUrl = verify.handleVerifyCode(url, html);
//			if (hopUrl == null) {
//				log.info("give up verifying url="+url+", since hopurl is null");
//				return;
//			}
//			if (!verify.isSuitable(hopUrl)) {
//				hopHtml = HttpClientGetUrlHelper.downloadHtml(hopUrl,
//						verify.getHopHtmlEncoding());
//				UURI dest = null;
//				try {
//					dest = UURIFactory.getInstance(curi.getBaseURI(), hopUrl);
//					UURI src = curi.getUURI();
//					Link link = new Link(src, dest, null, Hop.NAVLINK);
//					candidate = curi.createCrawlURI(curi.getBaseURI(), link);
//					candidate.setFullVia(curi);
//				} catch (URIException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (hopHtml != null) {
//					this.extract(candidate, hopHtml);
//				}
//			}
//		}

	}

	/**
	 * 判断给出链接是否为元搜索的列表页链接
	 * 
	 * @param url
	 * @return
	 */
	private boolean isMetaUrl(String url) {
		return (isEngineMatch(url) != null);
	}

	/**
	 * 提取处理该链接时候用到的IEngine
	 * 
	 * @param url
	 * @return
	 */
	private IEngine isEngineMatch(String url) {

		for (IEngine engine : Chasaler.engines) {
			if (engine.isMetaUrl(url)) {
				return engine;
			}
		}
		return null;
	}

	/**
	 * 从给出的链接当中提取出搜索关键词
	 * 
	 * @param url
	 * @return
	 */
	private String getKeyWord(String url) {
		if (url == null) {
			return "";
		} else {
			IEngine engine = isEngineMatch(url);
			if (engine != null) {
				return engine.getKeyword(url);
			}
		}
		return null;
	}

	/**
	 * 查找处理验证码认证的合适方法
	 * 
	 * @param url
	 * @return
	 */
	private IVerify isVerifyMatch(String url) {
		for (IVerify verify : Chasaler.verifys) {
			if (verify.isSuitable(url)) {
				return verify;
			}
		}
		return null;
	}

	private boolean needVerify(String url) {
		return (isVerifyMatch(url) != null);
	}
	
	private boolean shouldAdd(String url) {
		if (url == null) {
			return false;
		}
		boolean result=true;
		Pattern pattern = Pattern.compile(".*\\.((pdf)|(doc)|(xls)|(docx)|(xlsx))");
		Matcher m = pattern.matcher(url);
		if(m.matches()){
			result= false;
		}
		if(url.contains("baike.baidu.com")||url.contains("wenku.baidu.com/")){
			result= false;
		}
		return result;
	}

	protected boolean shouldProcess(String uri) {
		String url = uri;
		boolean result=false;
		if (url == null) {
			return false;
		}
		 if (url.contains("http://verify.baidu.com/vcode?")
		 || url.contains("http://www.google.com.hk/sorry/?")) {
		 return false;
		 }
		return result;

	}
	
	 /**
     * 
     * 根据URI 的特征，获得解析模板
     * @param url
     * @return
     * 
     * @since  crawler_agent　Ver1.0
     */
    protected XQueryTemplate getTemplate(CrawlUri curi){
        // 1.匹配host
        // 2.逐项匹配正则表达式
    	
    	Pattern pattern = Pattern.compile("http://([^/]*)/.*");
		Matcher m = pattern.matcher(curi.getUrl());
		String host = "";
		while (m.find()) {
			host = m.group(1);
		}
    	String domain = host;
		if (domain != null && templateCache.getTemplateCache().get(domain) != null) {
			Iterator iter = templateCache.getTemplateCache().get(domain).entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String regex = (String) entry.getKey();
				if (Pattern.matches(regex, curi.getUrl())) {
					return (XQueryTemplate) entry.getValue();
				}
			}
		}
        return null;
    }

}
