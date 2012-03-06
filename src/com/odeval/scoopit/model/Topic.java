package com.odeval.scoopit.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Topic {
    private Long id;
    private String smallImageUrl;
    private String mediumImageUrl;
    private String largeImageUrl;
    private String backgroundImage;
    private String backgroundRepeat;
    private String backgroundColor;
    private String description;
    private String name;
    private String shortname;
    private String url;
    private Boolean isCurator;
    private Integer curablePostCount;
    private Integer curatedPostCount;
    private Integer unreadPostCount;
    private User creator;
    private Post pinnedPost;
    private List<Post> curablePosts;
    private List<Post> curatedPosts;

    // private List<Tag> tags;
    // private Stat stat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public void setSmallImageUrl(String smallImageUrl) {
        this.smallImageUrl = smallImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getBackgroundRepeat() {
        return backgroundRepeat;
    }

    public void setBackgroundRepeat(String backgroundRepeat) {
        this.backgroundRepeat = backgroundRepeat;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsCurator() {
        return isCurator;
    }

    public void setIsCurator(Boolean isCurator) {
        this.isCurator = isCurator;
    }

    public Integer getCurablePostCount() {
        return curablePostCount;
    }

    public void setCurablePostCount(Integer curablePostCount) {
        this.curablePostCount = curablePostCount;
    }

    public Integer getCuratedPostCount() {
        return curatedPostCount;
    }

    public void setCuratedPostCount(Integer curatedPostCount) {
        this.curatedPostCount = curatedPostCount;
    }

    public Integer getUnreadPostCount() {
        return unreadPostCount;
    }

    public void setUnreadPostCount(Integer unreadPostCount) {
        this.unreadPostCount = unreadPostCount;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Post getPinnedPost() {
        return pinnedPost;
    }

    public void setPinnedPost(Post pinnedPost) {
        this.pinnedPost = pinnedPost;
    }

    public List<Post> getCurablePosts() {
        return curablePosts;
    }

    public void setCurablePosts(List<Post> curablePosts) {
        this.curablePosts = curablePosts;
    }

    public List<Post> getCuratedPosts() {
        return curatedPosts;
    }

    public void setCuratedPosts(List<Post> curatedPosts) {
        this.curatedPosts = curatedPosts;
    }

    public void popupateFromJsonObject(JSONObject obj) {
        if (obj == null) {
        	return;
        }
        
        id = obj.optLong("id");
        smallImageUrl = obj.optString("smallImageUrl", null);
        mediumImageUrl = obj.optString("mediumImageUrl", null);
        largeImageUrl = obj.optString("largeImageUrl", null);
        backgroundImage = obj.optString("backgroundImage", null);
        backgroundRepeat = obj.optString("backgroundRepeat", null);
        backgroundColor = obj.optString("backgroundColor", null);
        description = obj.optString("description", null);
        name = obj.optString("name", null);
        shortname = obj.optString("shortname", null);
        url = obj.optString("url", null);
        isCurator = obj.optBoolean("isCurator");
        curablePostCount = obj.optInt("curablePostCount");
        curatedPostCount = obj.optInt("curatedPostCount");
        unreadPostCount = obj.optInt("unreadPostCount");
        
        creator = new User();
        creator.popupateFromJsonObject(obj.optJSONObject("creator"));
        
        pinnedPost = new Post();
        pinnedPost.popupateFromJsonObject(obj.optJSONObject("pinnedPost"));
        
        curablePosts = Post.createPostArrayFromJsonArray(obj.optJSONArray("curablePosts"));
        
        curatedPosts = Post.createPostArrayFromJsonArray(obj.optJSONArray("curatedPosts"));
    }

    public static ArrayList<Topic> createTopicArrayFromJsonArray(JSONArray array) {
    	if (array == null) {
    		return null;
    	}
    	
        ArrayList<Topic> topics = new ArrayList<Topic>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonTopic;
            jsonTopic = array.optJSONObject(i);

            Topic topic = new Topic();
            topic.popupateFromJsonObject(jsonTopic);
            topics.add(topic);
        }
        return topics;
    }
    
    public static Topic getTopicFromJson(String json) throws JSONException {
        JSONObject jsonResponse;
        jsonResponse = new JSONObject(json);

        Topic topic = new Topic();
        topic.popupateFromJsonObject(jsonResponse.getJSONObject("topic"));
        return topic;
    }
}
