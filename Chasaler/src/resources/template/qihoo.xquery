xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.so.com/s?ie=utf-8&src=hao_360so&_re=0&q=%E7%89%9B%E5%A5%B6+%E4%B8%AD%E6%AF%92:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//DIV[@id="container"]//UL[@class="result"]/LI[@class="res-list"]
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode/H3[@class="res-title"]/A//text()
	let $titleLight := $topicNode/H3[@class="res-title"]/A/EM//text()
	let $topicUrl := $topicNode/H3[@class="res-title"]/A/@href
	let $publishTime := tokenize(($topicNode/P[@class="res-desc"]/CITE/text()),"\s+")[last()-1]
	let $briefContent := $topicNode/P[@class="res-desc"]//text()
	let $contentLight := $topicNode/P[@class="res-desc"]/EM//text()
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
for $page in $rootNode/BODY//DIV[@id="container"]/DIV[@id="page"]/A
    return 
    if ($page/@id="snext" or $page/@id="spre")
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