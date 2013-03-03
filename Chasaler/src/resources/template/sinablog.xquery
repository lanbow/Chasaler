xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://search.sina.com.cn/?by=all&q=%C5%A3%C4%CC+%D6%D0%B6%BE&c=blog&range=article:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//DIV[@class="result-boxes"]/DIV
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode//H2/A//text()
	let $titleLight := $topicNode//H2/A/SPAN//text()
	let $topicUrl := $topicNode//H2/A/@href
	let $publishTime := $topicNode//H2/SPAN/text()
	let $briefContent := $topicNode//P[1]//text()
	let $contentLight := $topicNode//P[1]/SPAN//text()
	return 
		<Topic>
		    <topicTitle>{$topicTitle}</topicTitle>
			<topicUrl>{$topicUrl}</topicUrl>
			<publishTime>{$publishTime}</publishTime>
			<content>{$briefContent}</content>
			<titleLight>{$titleLight}</titleLight>
	        <contentLight>{$contentLight}</contentLight>
		</Topic>
};


declare function local:pagelist() as node()*
{
for $page in $rootNode/BODY//DIV[@class="pagebox"]/A[starts-with(@title,"第")]
    return 
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