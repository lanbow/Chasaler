xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.soso.com/q?w=%C0%B3%B6%F7%B3%F6%D7%B0%20site%3Aauto.sina.com.cn&lr=&sc=web&ch=w.p&num=10&gid=&cin=&site=&sf=0&sd=0&nf=0&pg=1:)

declare variable $encoding := gb2312;
declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY/DIV[@id="main"]//DIV[@id="result"]/OL/LI
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode/DIV[starts-with(@class,"selected")]/H3/A//text()
	let $titleLight := $topicNode/DIV[starts-with(@class,"selected")]/H3/A/EM//text()
	let $topicUrl := $topicNode/DIV[starts-with(@class,"selected")]/H3/A/@href
	let $publishTime := replace(tokenize($topicNode/DIV[starts-with(@class,"selected")]/DIV[@class="result_summary"]/DIV[@class="url"]/CITE//text(),"&amp;nbsp;")[last()]," -","")
	let $briefContent := $topicNode/DIV[starts-with(@class,"selected")]/P[@class="ds"]//text()
	let $contentLight := $topicNode/DIV[starts-with(@class,"selected")]/P[@class="ds"]/EM//text()
	return 
	if($topicUrl)
	then	
	<Topic>
			<topicTitle>{$topicTitle}</topicTitle>
			<topicUrl>{$topicUrl}</topicUrl>
			<publishTime>{$publishTime}</publishTime>
			<content>{$briefContent}</content>
			<titleLight>{$titleLight}</titleLight>
			<contentLight>{$contentLight}</contentLight>
	</Topic>
	else
	<Topic>
	</Topic>
};

declare function local:pagelist() as node()*
{
for $page in $rootNode/BODY//DIV[@id="pager"]/DIV[@class="pg"]/A
    return 
    if ($page/@class="next" or $page/@class="pre")
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