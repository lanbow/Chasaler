xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.youdao.com/search?q=%E7%89%9B%E5%A5%B6+%E4%B8%AD%E6%AF%92&start=20&ue=utf8&keyfrom=web.page3&lq=%E7%89%9B%E5%A5%B6+%E4%B8%AD%E6%AF%92&timesort=0:)

declare variable $encoding := gb2312;

declare variable $rootNode := //HTML;

declare function local:getTopicNodes() as node()*
{
	let $topicNode := $rootNode/BODY//OL[@id="results"]/LI
	return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
	for $topicNode in $topicNodes
	    return local:parseTopic($topicNode)  			
};

declare function local:parseTopic($topicNode as node()) as node()
{
	let $topicTitle := $topicNode//H3/A/SPAN//text()
	let $titleLight := $topicNode//H3/A/SPAN/SPAN//text()
	let $topicUrl := $topicNode//H3/A/@href
	let $publishTime := tokenize(($topicNode//DIV[@class="result-footer"]/CITE/text()),"\s+")[last()]
	let $briefContent := $topicNode//P[last()]//text()
	let $contentLight := $topicNode//P[last()]/SPAN//text()
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
for $page in $rootNode/BODY//DIV[@class="c-pages"]/A
    return 
  if($page/@class="next-page" or $page/@class="prev-page")
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