package com.odeval.scoopit.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class User {
    private Long id;
    private String name;
    private String shortName;
    private String bio;
    private String smallAvatarUrl;
    private String mediumAvatarUrl;
    private String largeAvatarUrl;
    private ArrayList<Sharer> sharers;
    private ArrayList<Topic> curatedTopics;
    private ArrayList<Topic> followedTopics;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getShortName() {
        return shortName;
    }
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public String getSmallAvatarUrl() {
        return smallAvatarUrl;
    }
    public void setSmallAvatarUrl(String smallAvatarUrl) {
        this.smallAvatarUrl = smallAvatarUrl;
    }
    public String getMediumAvatarUrl() {
        return mediumAvatarUrl;
    }
    public void setMediumAvatarUrl(String mediumAvatarUrl) {
        this.mediumAvatarUrl = mediumAvatarUrl;
    }
    public String getLargeAvatarUrl() {
        return largeAvatarUrl;
    }
    public void setLargeAvatarUrl(String largeAvatarUrl) {
        this.largeAvatarUrl = largeAvatarUrl;
    }
    public List<Sharer> getSharers() {
        return sharers;
    }
    public void setSharers(ArrayList<Sharer> sharers) {
        this.sharers = sharers;
    }
    public ArrayList<Topic> getCuratedTopics() {
        return curatedTopics;
    }
    public void setCuratedTopics(ArrayList<Topic> curatedTopics) {
        this.curatedTopics = curatedTopics;
    }
    public ArrayList<Topic> getFollowedTopics() {
        return followedTopics;
    }
    public void setFollowedTopics(ArrayList<Topic> followedTopics) {
        this.followedTopics = followedTopics;
    }
    
    
    public void popupateFromJsonObject(JSONObject obj) {
        id = obj.optLong("id");
        name = obj.optString("name", null);
        shortName = obj.optString("shortName", null);
        bio = obj.optString("bio", null);
        smallAvatarUrl = obj.optString("smallAvatarUrl", null);
        mediumAvatarUrl = obj.optString("mediumAvatarUrl", null);
        largeAvatarUrl = obj.optString("largeAvatarUrl", null);
        
        sharers = new ArrayList<Sharer>();
        JSONArray jsonSharers = obj.optJSONArray("sharers");
        if (jsonSharers != null) {
	        for (int i=0; i<jsonSharers.length(); i++) {
	            JSONObject jsonSharer = jsonSharers.optJSONObject(i);
	            Sharer sharer = new Sharer();
	            sharer.popupateFromJsonObject(jsonSharer);
	        }
        }
        
        curatedTopics = Topic.createTopicArrayFromJsonArray(obj.optJSONArray("curatedTopics"));
        followedTopics = Topic.createTopicArrayFromJsonArray(obj.optJSONArray("followedTopics"));
    }
}
