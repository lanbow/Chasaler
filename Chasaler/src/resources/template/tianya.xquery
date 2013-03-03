xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://search.tianya.cn/ns?tn=sty&rn=10&pn=8&s=0&pid=&f=0&h=1&ma=0&q=%C5%A3%C4%CC+%D6%D0%B6%BE:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//DIV[@id="searchcontent_wrapper"]/DIV[@class="searchlist"]/DIV[@class="listbox"]
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
	let $titleLight := $topicNode/H3/A/SPAN//text()
	let $topicUrl := $topicNode/H3/A/@href
	let $publishTime := substring(($topicNode/P[@class="related"]//text())[last()],16,16)
	let $briefContent := $topicNode/P[@class="summary"]//text()
	let $contentLight := $topicNode/P[@class="summary"]/SPAN//text()
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
for $page in $rootNode/BODY//DIV[@class="pagination"]/A
    return 
    if(contains($page/text(),"一页"))
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