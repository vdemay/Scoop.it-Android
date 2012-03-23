package com.odeval.scoopit.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

public class User implements /*Parcelable,*/ Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = -7311166133047810531L;
    
    
    private Long id;
    private String name;
    private String shortName;
    private String bio;
    private String smallAvatarUrl;
    private String mediumAvatarUrl;
    private String largeAvatarUrl;
    private ArrayList<Sharer> sharers;
    
    private transient ArrayList<Topic> curatedTopics;
    private transient ArrayList<Topic> followedTopics;
    
    public User() {
    }
    
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
	            sharers.add(sharer);
	        }
        }
        
        curatedTopics = Topic.createTopicArrayFromJsonArray(obj.optJSONArray("curatedTopics"));
        followedTopics = Topic.createTopicArrayFromJsonArray(obj.optJSONArray("followedTopics"));
    }
    
    /*
    // -- PARCELABLE -- //
    
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(shortName);
        dest.writeString(bio);
        dest.writeString(smallAvatarUrl);
        dest.writeString(mediumAvatarUrl);
        dest.writeString(largeAvatarUrl);
        dest.writeList(sharers);
        dest.writeList(curatedTopics);
        dest.writeList(followedTopics);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User[] newArray(int size) {
            return new User[size];
        }

        public User createFromParcel(Parcel user) {
            return new User(user);
        }
    };

    private User(Parcel in) {
        id = in.readLong();
        name = in.readString();
        shortName = in.readString();
        bio = in.readString();
        smallAvatarUrl = in.readString();
        mediumAvatarUrl = in.readString();
        largeAvatarUrl = in.readString();
        sharers = new ArrayList<Sharer>();
        in.readList(sharers, this.getClass().getClassLoader());
        curatedTopics = new ArrayList<Topic>();
        in.readList(curatedTopics, this.getClass().getClassLoader());
        followedTopics = new ArrayList<Topic>();
        in.readList(followedTopics, this.getClass().getClassLoader());
    }
    
    public int describeContents() {
        return 0;
    }*/
    
    // -- WRITE -- //
    public void witeToFile(Context context) {

        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput("user", Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            fileOut.getFD().sync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
    }

    
    // -- READ -- //
    public static User readFromFile(Context context) {
        ObjectInputStream objectIn = null;
        User user = new User();
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput("user");
            objectIn = new ObjectInputStream(fileIn);
            user = (User) objectIn.readObject();

        } catch (FileNotFoundException e) {
            // Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }

        return user;
    }
    

    public static User getUserFromJson(String json) throws JSONException {
        if (json == null) return null;
        JSONObject jsonResponse;
        jsonResponse = new JSONObject(json);

        User user = new User();
        user.popupateFromJsonObject(jsonResponse.getJSONObject("user"));
        return user;
    }

}
