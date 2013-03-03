package com.chasal.crawler.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 主题型网页正文抽取，比较适合于新闻和Blog的正文抽取
 * 最先根据格式时间的数量确定数据是否为论坛类型,对于格式时间数量少的论坛可以作文主题页处理
 * 最后才能确认主题与索引类型
 * 采用《基于行块分布函数的通用网页正文抽取》的算法，该算法时间复杂度为线性，
 * @add ljc
 */
public class TextExtractor {
	
	private static Logger logger = Logger.getLogger(TextExtractor.class.getName());

	/**@add*/
	private final static int _BBS = 3;
	private final static int _BLOG = 4;
	private final static int _NEWS = 2;
	
	//是否需要探测网页类型
	private boolean needDetect = true;
	
	private int _style = 0;
	//用于测试@test
	private boolean newsOnly = false;
	
	private static Pattern pattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}:\\d{1,2}[(:\\d{1,2})]");
	/** 判断如果页面时间数量大于等于_stylelimit则为论坛*/
	private static int _stylelimit = 5;
	/** 在排除论坛可能性后判断是否为博客 以url是否包含"blog"字符区分*/
	private static String _blog_flag = "blog";
	private String _url = "";
	private String _site = "";
	private String _title_seprater_flag = "_|-|——";
	private String _host = "";
	
	private static int _filte_length_bbs = 10;
	
	private static int _filte_length_news = 40;
	/** 行块大小. */
	private static Integer _block = 3;
	
	/** The Constant _titlePattern. */
	private final static String _titlePattern = "<title>(.*?)</title>";

	/** The Constant _titleRegexPattern. */
	private final static Pattern _titleRegexPattern = Pattern.compile(
			_titlePattern, Pattern.CANON_EQ | Pattern.DOTALL
					| Pattern.CASE_INSENSITIVE);
	
	

	/** The _title. */
	private String _title = "";

	/** The _text. */
	private String _text = "";
	
	public TextExtractor(){
		
	}

	/**
	 * Sets the block.
	 * 
	 * @param block the new block
	 */
	@SuppressWarnings("unused")
	private static void setBlock(Integer block) {
		_block = block;
	}

	/**
	 * Extract html.
	 * 
	 * @param htmlText the html text
	 */
	public void extractHTML(String htmlText) {
		extractTitle(htmlText);
		htmlText = preProcess(htmlText);
		if (!isContentPage(htmlText)) {
			setINDEX();
			_text = null;
			logger.log(Level.FINE, "推测您提供的网页为非主题型网页，目前暂不处理！");   
			return;
		} else	if( needDetect && isBBS(htmlText) ){
			setBBS();
		} else if( needDetect && isBlog() ){
			setBLOG();
		}

		List<String> lines = Arrays.asList(htmlText.split("\n"));
		List<Integer> indexDistribution = lineBlockDistribute(lines);

		List<String> textList = new ArrayList<String>();
		List<Integer> textBeginList = new ArrayList<Integer>();
		List<Integer> textEndList = new ArrayList<Integer>();

		for (int i = 0; i < indexDistribution.size(); i++) {
			if (indexDistribution.get(i) > 0) {
				StringBuilder tmp = new StringBuilder();
				textBeginList.add(i);
				while (i < indexDistribution.size()
						&& indexDistribution.get(i) > 0) {
					tmp.append(lines.get(i)).append("\n");
					i++;
				}
				textEndList.add(i);
				textList.add(tmp.toString());
			}
		}

		// 如果两块只差两个空行，并且两块包含文字均较多，则进行块合并，以弥补单纯抽取最大块的缺点
		for (int i = 1; i < textList.size(); i++) {
			if (textBeginList.get(i) == textEndList.get(i - 1) + 1
					&& textEndList.get(i) > textBeginList.get(i) + _block
					&& textList.get(i).replaceAll("\\s+", "").length() > 40) {
				if (textEndList.get(i - 1) == textBeginList.get(i - 1) + _block
						&& textList.get(i - 1).replaceAll("\\s+", "").length() < 40) {
					continue;
				}
				textList.set(i - 1, textList.get(i - 1) + textList.get(i));
				textEndList.set(i - 1, textEndList.get(i));

				textList.remove(i);
				textBeginList.remove(i);
				textEndList.remove(i);
				--i;
			}
		}	
		
		//if( isBBS() ) 以下用于测试@test
		if ( isBBS() && !newsOnly )
			setBBSText( textList );
		else {
			setNEWSText( textList );
		}
	}
	
	private boolean isBlog() {
		int index = _url.indexOf( _blog_flag );
		return index > -1;
	}
	
	
	
	private void setNEWSText ( List<String> textList ) {
		String result = "";
		for (String text : textList) {

			if (text.replaceAll("\\s+", "").length() > result.replaceAll(
					"\\s+", "").length())
				result = text;
		}

		// 最长块长度小于100，归为非主题型网页
		if (result.replaceAll("\\s+", "").length() < 100){
			_text = null;
			logger.info("推测您提供的网页为非主题型网页，目前暂不处理！");
		}
		else {
			if( needDetect && isBlog() )
				setBLOG();
			else if( needDetect )
				setNEWS();
			_text = result;
		}
	}
	
	private void setBBSText ( List<String> textList ) {
		StringBuffer result = new StringBuffer();
		for ( String text : textList ) {
			if ( isContent ( text ) )
				result.append( text ).append( "\n" );
		}
		_text = result.toString();
	}
	private boolean isContent ( String text ) {
		return ( text.contains("，") || text.contains("。") );
	}
	/**
	 * Checks if is content page.
	 * 
	 * @param htmlText the html text
	 * 
	 * @return true, if is content page
	 */
	private boolean isContentPage(String htmlText) {
		int count = 0;
		for (int i = 0; i < htmlText.length() && count < 5; i++) {
			if (htmlText.charAt(i) == '，' || htmlText.charAt(i) == '。')
				count++;
		}

		return count >= 5;
	}

	/**
	 * Extract title.
	 * 
	 * @param htmlText the html text
	 */
	private void extractTitle(String htmlText) {
		Matcher m1 = _titleRegexPattern.matcher(htmlText);
		
		String title = "";
		if (m1.find()) {
			title = replaceSpecialChar(m1.group(1));
		}
		title = title.replaceAll("\n+", "");
		String[] separtors = title.split(_title_seprater_flag );
		_title = separtors.length > 0 ? separtors[0] : title;
		_site = separtors.length > 1 ? separtors[separtors.length - 1] : "未知";
		
	}

	/**
	 * Gets the title.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return _title;
	}
	
	public String getSite(){
		return _site;
	}
	
	public void setUrl( String url ) {
		_url = url;
	}
	/**
	 * Gets the text.
	 * 
	 * @return the text
	 */
	public String getText() {
	
		return (_text == null) ? null : _text.trim();
	}

	/**
	 * Line block distribute.
	 * 
	 * @param lines the lines
	 * 
	 * @return the list< integer>
	 */
	private List<Integer> lineBlockDistribute(List<String> lines) {
		List<Integer> indexDistribution = new ArrayList<Integer>();

		for (int i = 0; i < lines.size(); i++) {
			indexDistribution.add(lines.get(i).replaceAll("\\s+", "").length());
		}
		// 删除上下存在两个空行的文字行
		for (int i = 0; i + 4 < lines.size(); i++) {
			if (indexDistribution.get(i) == 0
					&& indexDistribution.get(i + 1) == 0
					&& indexDistribution.get(i + 2) > 0
					&& indexDistribution.get(i + 2) < ( isBBS() ? _filte_length_bbs : _filte_length_news)
					&& indexDistribution.get(i + 3) == 0
					&& indexDistribution.get(i + 4) == 0) {

				lines.set(i + 2, "");
				indexDistribution.set(i + 2, 0);
				i += 3;
			}
		}

		for (int i = 0; i < lines.size() - _block; i++) {
			int wordsNum = indexDistribution.get(i);
			for (int j = i + 1; j < i + _block && j < lines.size(); j++) {
				wordsNum += indexDistribution.get(j);
			}
			indexDistribution.set(i, wordsNum);
		}

		return indexDistribution;
	}

	/**
	 * Pre processing.
	 * 
	 * @param htmlText the html text
	 * 
	 * @return the string
	 */
	private String preProcess(String htmlText) {
		// DTD
		htmlText = htmlText.replaceAll("(?is)<!DOCTYPE.*?>", "");
		// html comment
		htmlText = htmlText.replaceAll("(?is)<!--.*?-->", "");
		// js
		htmlText = htmlText.replaceAll("(?is)<script.*?>.*?</script>", "");
		// css
		htmlText = htmlText.replaceAll("(?is)<style.*?>.*?</style>", "");
		// a
		htmlText = htmlText.replaceAll("(?is)<a.*?>.*?</a>", "");
		// html
		htmlText = htmlText.replaceAll("(?is)<.*?>", "");

		return replaceSpecialChar(htmlText);
	}

	/**
	 * Replace special char.
	 * 
	 * @param content the content
	 * 
	 * @return the string
	 */
	private String replaceSpecialChar(String content) {
		String text = content.replaceAll("&quot;", "\"");
		text = text.replaceAll("&ldquo;", "“");
		text = text.replaceAll("&rdquo;", "”");
		text = text.replaceAll("&middot;", "·");
		text = text.replaceAll("&#8231;", "·");
		text = text.replaceAll("&#8212;", "——");
		text = text.replaceAll("&#28635;", "濛");
		text = text.replaceAll("&hellip;", "…");
		text = text.replaceAll("&#23301;", "嬅");
		text = text.replaceAll("&#27043;", "榣");
		text = text.replaceAll("&#8226;", "·");
		text = text.replaceAll("&#40;", "(");
		text = text.replaceAll("&#41;", ")");
		text = text.replaceAll("&#183;", "·");
		text = text.replaceAll("&amp;", "&");
		text = text.replaceAll("&bull;", "·");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&#60;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&#62;", ">");
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&#160;", " ");
		text = text.replaceAll("&tilde;", "~");
		text = text.replaceAll("&mdash;", "—");
		text = text.replaceAll("&copy;", "@");
		text = text.replaceAll("&#169;", "@");
		text = text.replaceAll("♂", "");
		text = text.replaceAll("\r\n|\r", "\n");

		return text;
	}
	/**
	 * 设定文本的类型
	 */
	private void setBBS() {
		_style = _BBS;
	}
	
	private void setNEWS() {
		_style = _NEWS;
	}
	
	private void setBLOG() {
		_style = _BLOG;
	}
	
	private void setINDEX() {
		_style = 0;
	}
	/***
	 * 给出此链接的类型
	 * @return
	 */
	public boolean isBBS() {
		return _style == _BBS;
	}
	
	public boolean isNEWS() {
		return _style == _NEWS;
	}
	
	public boolean isBLOG() {
		return _style == _BLOG;
	}
	
	public boolean isINDEX() {
		return _style == 0;
	}
	public int getStyle() {
		return _style;
	}
	public String getHost(){
//		if( _host == null || "".equals( _host ) )
		parseHost();
		return _host;
	}
	
	private void parseHost() {
		String url = _url.replace("http://", "");
		int index = url.indexOf( "/" );
		if( index > -1 )
			_host = url.substring( 0, index );
		else
			_host = url;
	}
	/***
	 * 根据时间判断时候为论坛类
	 * @param htmlString
	 * @return
	 */
	private boolean isBBS( String htmlString ) {
		Matcher matcher = pattern.matcher( htmlString );
		int count = 0;
		while ( matcher.find() )
			count++;
		return judgeStyle ( count );
	}
	/**
	 * 根据时间数量来判定是否为论坛类
	 * @param count
	 * @return
	 */
	private boolean judgeStyle ( int count ) {
		return count >= _stylelimit;
	}


}