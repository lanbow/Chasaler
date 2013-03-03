xquery version "1.0";
declare namespace h = "http://www.w3.org/1999/xhtml";
(:http://www.sogou.com/web?query=%C9%CD%BD%F0%C1%D4%C8%CB%B3%F6%D7%B0+site%3Asina.com.cn&sut=10685&sst0=1356588421102&page=30&p=40040100&dp=1&w=01029901&dr=1:)

declare variable $encoding := gbk;
declare variable $rootNode := /HTML;

declare function local:getTopicNodes() as node()*
{
let $topicNode := $rootNode/BODY//DIV[@id="wrapper"]/DIV[@id="main"]/DIV/DIV[@class="results"]/DIV[@class="rb"]
return $topicNode
};

declare function local:getTopics($topicNodes as node()*) as node()*
{
for $topicNode in $topicNodes
    return local:parseTopic($topicNode)   
};

declare function local:parseTopic($topicNode as node()) as node()
{
let $topicTitle := $topicNode/H3[@class="pt"]/A//text()
let $titleLight := $topicNode/H3[@class="pt"]/A[1]/EM//text()
let $topicUrl := $topicNode/H3[@class="pt"]/A[1]/@href
let $publishTime := if(contains($topicNode/DIV[@class="fb"]/CITE/text(),"-&amp;nbsp;"))
       then tokenize($topicNode/DIV[@class="fb"]/CITE//text(),"-&amp;nbsp;")[last()]
       else tokenize($topicNode/DIV[@class="fb"]/CITE//text()," - ")[last()]

                       
let $briefContent := $topicNode/DIV[@class="ft"]//text()
let $contentLight := $topicNode/DIV[@class="ft"]/EM//text()
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
for $page in $rootNode/BODY/DIV[@id="wrapper"]/DIV[@id="main"]/DIV[@id="pagebar_container"]/A
    return 
    if ($page/@id="sogou_prev" or $page/@id="sogou_next")
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