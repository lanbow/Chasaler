xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.baidu.com/s?wd=%E7%89%9B%E5%A5%B6%20%E4%B8%AD%E6%AF%92&pn=50&tn=baiduhome_pg&ie=utf-8&usm=2:)

declare variable $encoding := gb2312;
declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	(:let $topicNode := $rootNode/BODY//DIV[@id="container"]/TABLE/TBODY/TR/TD[@class="f"]:)
	let $topicNode := $rootNode/BODY//DIV[@id="container"]/DIV[@id="content_left"]/TABLE[starts-with(@class,"result")]/TBODY/TR/TD[@class="f"]
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode/H3/A//text()
	let $titleLight := $topicNode/H3/A/EM//text()
	let $topicUrl := $topicNode/H3/A/@href
	(:let $publishTime := substring-after(($topicNode/FONT/SPAN[last()]/text())[last()]," "):)
	let $publishTime := if((tokenize(($topicNode/FONT/SPAN/text())[last()],"\s+")[last()])="")
	                       then tokenize(($topicNode/FONT/SPAN/text())[last()],"\s+")[last()-1]
	                       else tokenize(($topicNode/FONT/SPAN/text())[last()],"\s+")[last()]
	                       
	let $briefContent := ($topicNode/FONT//text())[position()<last()-3]
	let $contentLight := $topicNode/FONT/EM//text()
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
for $page in $rootNode/BODY//P[@id="page"]/A
    return 
    if ($page/@class="n")
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