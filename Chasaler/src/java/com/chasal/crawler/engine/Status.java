
package com.chasal.crawler.engine;

/**
 * 微博的文字
 * 
 * @author xv
 * @version ${version} 2012-7-12 ${desc}
 * @since CodingExample Ver 1.1
 * @see YourClass, HisClass
 */
public class Status {
    
    // 内部ID
    private String id;
    
    // 站点编号
    private String domain;
    
    private String releaseTime;
    
    private String url;
    
    private String authorId;
    
    private String authorName;
    
    private String authorUrl;
    
    private String content;
    
    // 转发数
    private String repostCount;
    
    // 评论数
    private String commentCount;
    
    // 转发信息
    private String fUrl;
    
    private String fAuthorId;
    
    private String fAuthorName;
    
    private String fAuthorUrl;
    
    private String fPublishTime;
    
    private String fContent;
    
    private String spideTime;
    
    private String picUrl; 
    
    // 域名
    private String host;
    
    private String keyword;
    
    public Status(){
        
    }
    
    /**
     * 构造函数，抓取用户 的微博信息
     */
    public Status(String id, String releaseTime, String url, String content, String repostCount, String commentCount,
        String fUrl, String fAuthorId, String fAuthorName, String fAuthorUrl, String fPublishTime, String fContent) {
        super();
        this.id = id;
        this.releaseTime = releaseTime;
        this.url = url;
        this.content = content;
        this.repostCount = repostCount;
        this.commentCount = commentCount;
        this.fUrl = fUrl;
        this.fAuthorId = fAuthorId;
        this.fAuthorName = fAuthorName;
        this.fAuthorUrl = fAuthorUrl;
        this.fPublishTime = fPublishTime;
        this.fContent = fContent;
    }
    
    /**
     * 构造函数，用于 微博搜索数据
     */
    public Status(String id, String releaseTime, String url, String authorId, String authorName, String authorUrl,
        String content, String repostCount, String commentCount, String fUrl, String fAuthorId, String fAuthorName,
        String fAuthorUrl, String fPublishTime, String fContent) {
        super();
        this.id = id;
        this.releaseTime = releaseTime;
        this.url = url;
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorUrl = authorUrl;
        this.content = content;
        this.repostCount = repostCount;
        this.commentCount = commentCount;
        this.fUrl = fUrl;
        this.fAuthorId = fAuthorId;
        this.fAuthorName = fAuthorName;
        this.fAuthorUrl = fAuthorUrl;
        this.fPublishTime = fPublishTime;
        this.fContent = fContent;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getReleaseTime() {
        return releaseTime;
    }
    
    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getAuthorId() {
        return authorId;
    }
    
    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }
    
    public String getAuthorName() {
        return authorName;
    }
    
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
    
    public String getAuthorUrl() {
        return authorUrl;
    }
    
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = authorUrl;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getRepostCount() {
        return repostCount;
    }
    
    public void setRepostCount(String repostCount) {
        this.repostCount = repostCount;
    }
    
    public String getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }
    
    public String getfUrl() {
        return fUrl;
    }
    
    public void setfUrl(String fUrl) {
        this.fUrl = fUrl;
    }
    
    public String getfAuthorId() {
        return fAuthorId;
    }
    
    public void setfAuthorId(String fAuthorId) {
        this.fAuthorId = fAuthorId;
    }
    
    public String getfAuthorName() {
        return fAuthorName;
    }
    
    public void setfAuthorName(String fAuthorName) {
        this.fAuthorName = fAuthorName;
    }
    
    public String getfAuthorUrl() {
        return fAuthorUrl;
    }
    
    public void setfAuthorUrl(String fAuthorUrl) {
        this.fAuthorUrl = fAuthorUrl;
    }
    
    public String getfPublishTime() {
        return fPublishTime;
    }
    
    public void setfPublishTime(String fPublishTime) {
        this.fPublishTime = fPublishTime;
    }
    
    public String getfContent() {
        return fContent;
    }
    
    public void setfContent(String fContent) {
        this.fContent = fContent;
    }
    
    public String getSpideTime() {
        return spideTime;
    }
    
    public void setSpideTime(String spideTime) {
        this.spideTime = spideTime;
    }

    public String getPicUrl() {
        return picUrl;
    }

    
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Status other = (Status) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        return true;
    }

    public String getHost() {
        return host;
    }

    
    public void setHost(String host) {
        this.host = host;
    }

    
    public String getKeyword() {
        return keyword;
    }

    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "Status [id=" + id + ", domain=" + domain + ", releaseTime=" + releaseTime + ", url=" + url
                + ", authorId=" + authorId + ", authorName=" + authorName + ", authorUrl=" + authorUrl + ", content="
                + content + ", repostCount=" + repostCount + ", commentCount=" + commentCount + ", fUrl=" + fUrl
                + ", fAuthorId=" + fAuthorId + ", fAuthorName=" + fAuthorName + ", fAuthorUrl=" + fAuthorUrl
                + ", fPublishTime=" + fPublishTime + ", fContent=" + fContent + ", spideTime=" + spideTime
                + ", pic=" + picUrl + "]";
    }

    
    public String getDomain() {
        return domain;
    }

    
    public void setDomain(String domain) {
        this.domain = domain;
    }
    
}
