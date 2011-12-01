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
        
        try { id = obj.getLong("id");} catch (JSONException e) {}
        try { smallImageUrl = obj.getString("smallImageUrl");} catch (JSONException e) {}
        try { mediumImageUrl = obj.getString("mediumImageUrl");} catch (JSONException e) {}
        try { largeImageUrl = obj.getString("largeImageUrl");} catch (JSONException e) {}
        try { backgroundImage = obj.getString("backgroundImage");} catch (JSONException e) {}
        try { backgroundRepeat = obj.getString("backgroundRepeat");} catch (JSONException e) {}
        try { backgroundColor = obj.getString("backgroundColor");} catch (JSONException e) {}
        try { description = obj.getString("description");} catch (JSONException e) {}
        try { name = obj.getString("name");} catch (JSONException e) {}
        try { shortname = obj.getString("shortname");} catch (JSONException e) {}
        try { url = obj.getString("url");} catch (JSONException e) {}
        try { isCurator = obj.getBoolean("isCurator");} catch (JSONException e) {}
        try { curablePostCount = obj.getInt("curablePostCount");} catch (JSONException e) {}
        try { curatedPostCount = obj.getInt("curatedPostCount");} catch (JSONException e) {}
        try { unreadPostCount = obj.getInt("unreadPostCount");} catch (JSONException e) {}
        
        try {
            creator = new User();
            creator.popupateFromJsonObject(obj.getJSONObject("creator"));
        } catch (JSONException e) {}
        
        try {
            pinnedPost = new Post();
            pinnedPost.popupateFromJsonObject(obj.getJSONObject("pinnedPost"));
        } catch (JSONException e) {}
        
        try {
            curablePosts = Post.createPostArrayFromJsonArray(obj.getJSONArray("curablePosts"));
        } catch (JSONException e) {}
        
        try {
            curatedPosts = Post.createPostArrayFromJsonArray(obj.getJSONArray("curatedPosts"));
        } catch (JSONException e) {}
        
    }

    public static ArrayList<Topic> createTopicArrayFromJsonArray(JSONArray array) {
        try {
            ArrayList<Topic> topics = new ArrayList<Topic>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonTopic;
                jsonTopic = array.getJSONObject(i);

                Topic topic = new Topic();
                topic.popupateFromJsonObject(jsonTopic);
                topics.add(topic);
            }
            return topics;
        } catch (JSONException e) {
            return null;
        }
    }
}
