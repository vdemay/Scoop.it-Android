package com.odeval.scoopit.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

    public Post() {
        super();
    }

    private Long id;
    private String content;
    private String htmlFragment;
    private String htmlContent;
    private String title;
    private int thanksCount;
    private int reactionsCount;
    private Source source;
    private String twitterAuthor;
    private String url;
    private String scoopUrl;
    private String smallImageUrl;
    private String mediumImageUrl;
    private String imageUrl;
    private String largeImageUrl;
    private int imageWidth;
    private int imageHeight;
    private int imageSize;
    private String imagePosition;
    private ArrayList<String> imageUrls;
    private ArrayList<String> tags;
    private int commentsCount;
    private boolean isUserSuggestion;
    private Long pageViews;
    private boolean edited;
    private int publicationDate;
    private int curationDate;
    // comments
    // thanked
    private Topic topic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlFragment() {
        return htmlFragment;
    }

    public void setHtmlFragment(String htmlFragment) {
        this.htmlFragment = htmlFragment;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getThanksCount() {
        return thanksCount;
    }

    public void setThanksCount(int thanksCount) {
        this.thanksCount = thanksCount;
    }

    public int getReactionsCount() {
        return reactionsCount;
    }

    public void setReactionsCount(int reactionsCount) {
        this.reactionsCount = reactionsCount;
    }

    public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getTwitterAuthor() {
        return twitterAuthor;
    }

    public void setTwitterAuthor(String twitterAuthor) {
        this.twitterAuthor = twitterAuthor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getScoopUrl() {
        return scoopUrl;
    }

    public void setScoopUrl(String scoopUrl) {
        this.scoopUrl = scoopUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLargeImageUrl() {
        return largeImageUrl;
    }

    public void setLargeImageUrl(String largeImageUrl) {
        this.largeImageUrl = largeImageUrl;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public String getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(String imagePosition) {
        this.imagePosition = imagePosition;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public boolean isUserSuggestion() {
        return isUserSuggestion;
    }

    public void setUserSuggestion(boolean isUserSuggestion) {
        this.isUserSuggestion = isUserSuggestion;
    }

    public Long getPageViews() {
        return pageViews;
    }

    public void setPageViews(Long pageViews) {
        this.pageViews = pageViews;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public int getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(int publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getCurationDate() {
        return curationDate;
    }

    public void setCurationDate(int curationDate) {
        this.curationDate = curationDate;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public void popupateFromJsonObject(JSONObject obj) {
    	if (obj == null) {
    		return;
    	}
    	
        id = obj.optLong("id");
        content = obj.optString("content", null);
        htmlFragment = obj.optString("htmlFragment", null);
        htmlContent = obj.optString("htmlContent", null);
        title = obj.optString("title", null);
        thanksCount = obj.optInt("thanksCount");
        reactionsCount = obj.optInt("reactionsCount");
        source = new Source(obj.optJSONObject("source"));
        twitterAuthor = obj.optString("twitterAuthor", null);
        url = obj.optString("url", null);
        scoopUrl = obj.optString("scoopUrl", null);
        smallImageUrl = obj.optString("smallImageUrl", null);
        mediumImageUrl = obj.optString("mediumImageUrl", null);
        imageUrl = obj.optString("imageUrl", null);
        largeImageUrl = obj.optString("largeImageUrl", null);
        imageWidth = obj.optInt("imageWidth");
        imageHeight = obj.optInt("imageHeight");
        imageSize = obj.optInt("imageSize");
        imagePosition = obj.optString("imagePosition", null);
        imageUrls = new ArrayList<String>();
        JSONArray array = obj.optJSONArray("imageUrls");
        if (array != null) {
	        for (int i = 0; i < array.length(); i++) {
	            imageUrls.add(array.optString(i));
	        }
        }
        tags = new ArrayList<String>();
        array = obj.optJSONArray("tags");
        if (array != null) {
	        for (int i = 0; i < array.length(); i++) {
	            tags.add(array.optString(i));
	        }
        }
        commentsCount = obj.optInt("commentsCount");
        isUserSuggestion = obj.optBoolean("isUserSuggestion");
        pageViews = obj.optLong("pageViews");
        edited = obj.optBoolean("edited");
        publicationDate = obj.optInt("publicationDate");
        curationDate = obj.optInt("curationDate");
        topic = new Topic();
        topic.popupateFromJsonObject(obj.optJSONObject("topic"));
    }

    public static ArrayList<Post> createPostArrayFromJsonArray(JSONArray array) {
    	if (array == null) {
    		return null;
    	}
    	
        ArrayList<Post> posts = new ArrayList<Post>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonPost;
            jsonPost = array.optJSONObject(i);

            Post post = new Post();
            post.popupateFromJsonObject(jsonPost);
            posts.add(post);
        }
        return posts;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(content);
        dest.writeString(htmlFragment);
        dest.writeString(htmlContent);
        dest.writeString(title);
        dest.writeInt(thanksCount);
        dest.writeInt(reactionsCount);
        dest.writeParcelable(source, flags);
        dest.writeString(twitterAuthor);
        dest.writeString(url);
        dest.writeString(scoopUrl);
        dest.writeString(smallImageUrl);
        dest.writeString(mediumImageUrl);
        dest.writeString(imageUrl);
        dest.writeString(largeImageUrl);
        dest.writeInt(imageWidth);
        dest.writeInt(imageHeight);
        dest.writeInt(imageSize);
        dest.writeString(imagePosition);
        dest.writeStringList(imageUrls);
        dest.writeStringList(tags);
        dest.writeInt(commentsCount);
        //TODO edited
        dest.writeInt(publicationDate);
        dest.writeInt(curationDate);
        //TODO comments
        //TODO thanked
        //TODO topic
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post[] newArray(int size) {
            return new Post[size];
        }

        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }
    };

    private Post(Parcel in) {
        id = in.readLong();
        content = in.readString();
        htmlFragment = in.readString();
        htmlContent = in.readString();
        title = in.readString();
        thanksCount = in.readInt();
        reactionsCount = in.readInt();
        source = in.readParcelable(this.getClass().getClassLoader());
        twitterAuthor = in.readString();
        url = in.readString();
        scoopUrl = in.readString();
        smallImageUrl = in.readString();
        mediumImageUrl = in.readString();
        imageUrl = in.readString();
        largeImageUrl = in.readString();
        imageWidth = in.readInt();
        imageHeight = in.readInt();
        imageSize = in.readInt();
        imagePosition = in.readString();
        imageUrls = new ArrayList<String>();
        in.readStringList(imageUrls);
        tags = new ArrayList<String>();
        in.readStringList(tags);
        commentsCount = in.readInt();
        //TODO edited
        publicationDate = in.readInt();
        curationDate = in.readInt();
        //TODO comments
        //TODO thanked
        //TODO topic
    }

}
