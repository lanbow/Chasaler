xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.google.com.hk/search?q=%E8%B5%8F%E9%87%91%E7%8C%8E%E4%BA%BA%E5%87%BA%E8%A3%85+site:sina.com.cn&hl=zh-CN&newwindow=1&safe=strict&tbo=d&ei=renbUOXyJeuiiAe4goGABQ&start=50&sa=N:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//DIV[@id="ires"]//LI[@class="g"]
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode//H3//A//text()
	let $titleLight := $topicNode//H3//A/EM//text()
	let $topicUrl := $topicNode//H3[@class="r"]/A[@target="_blank"]/@href
	let $publishTime := if(empty($topicNode//SPAN[@class="f std"]//text()))
	                    then tokenize($topicNode//DIV[@class="f slp"]//text(),"&amp;nbsp;-&amp;nbsp;")[2]
	                    else $topicNode//SPAN[@class="f std"]//text()
	let $briefContent := $topicNode//SPAN[@class="st"]//text()
	let $contentLight := $topicNode//SPAN[@class="st"]/EM//text()
	
	return 
		<Topic>
			<TopicTitle>{$topicTitle}</TopicTitle>
			<TopicUrl>{$topicUrl}</TopicUrl>
			<PublishTime>{$publishTime}</PublishTime>
			<BriefContent>{$briefContent}</BriefContent>
			<titleLight>{$titleLight}</titleLight>
	        <contentLight>{$contentLight}</contentLight>
		</Topic>
};

declare function local:pagelist() as node()*
{
for $page in $rootNode/BODY//DIV[@id="foot"]//TD
    return 
     (:if ($page/@class="b" or empty($page/A)):)
     if(contains($page/@class,"b") or empty($page/A))
    then 
    <nextList>
    <page>{$page/A/@href}</page>
    </nextList>
    else
    <List>
    <page>{$page/A/@href}</page>
    </List>
    
};

(: CODE TO UPDATE - END :)
<BaiduTopic>
	<Topics>
      {
      	let $topicNodes := local:getTopicNodes()
      	let $topics := local:getTopics($topicNodes)
      	return $topics
      }
    </Topics>
    
    <pageList>
    {
    local:pagelist()
    }
    </pageList>
    
</BaiduTopic>