xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://tieba.baidu.com/f/search/res?ie=utf-8&qw=%E7%89%9B%E5%A5%B6%20%E4%B8%AD%E6%AF%92:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//DIV[@class="s_post"]
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode/SPAN[@class="p_title"]/A//text()
	let $titleLight := $topicNode/SPAN[@class="p_title"]/A/EM//text()
	let $topicUrl := $topicNode/SPAN[@class="p_title"]/A/@href
	let $publishTime := $topicNode//FONT[@class="p_green"]//text()
	let $briefContent := $topicNode//DIV[@class="p_content"]//text()
	let $contentLight := $topicNode//DIV[@class="p_content"]/EM//text()
	
	
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
for $page in $rootNode/BODY//DIV[@class="pager pager-search"]/A
    return 
    if (contains($page/text(),"页"))
    then 
    <nextList>
    <page>{$page/@href}</page>
    </nextList>
    else 
    <List>
    <page>{$page/@href}</page>
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