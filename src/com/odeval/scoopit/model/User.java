package com.odeval.scoopit.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
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
        try { id = obj.getLong("id");} catch (JSONException e) {}
        try { name = obj.getString("name");} catch (JSONException e) {}
        try { shortName = obj.getString("shortName");} catch (JSONException e) {}
        try { bio = obj.getString("bio");} catch (JSONException e) {}
        try { smallAvatarUrl = obj.getString("smallAvatarUrl");} catch (JSONException e) {}
        try { mediumAvatarUrl = obj.getString("mediumAvatarUrl");} catch (JSONException e) {}
        try { largeAvatarUrl = obj.getString("largeAvatarUrl");} catch (JSONException e) {}
        
        try { 
            sharers = new ArrayList<Sharer>();
            JSONArray jsonSharers = obj.getJSONArray("sharers");
            for (int i=0; i<jsonSharers.length(); i++) {
                JSONObject jsonSharer = jsonSharers.getJSONObject(i);
                Sharer sharer = new Sharer();
                sharer.popupateFromJsonObject(jsonSharer);
            }
        } catch (JSONException e) {}
        
        try { 
            curatedTopics = Topic.createTopicArrayFromJsonArray(obj.getJSONArray("curatedTopics"));
        } catch (JSONException e) {}
        
        try { 
            followedTopics = Topic.createTopicArrayFromJsonArray(obj.getJSONArray("followedTopics"));
        } catch (JSONException e) {}
    }
}
