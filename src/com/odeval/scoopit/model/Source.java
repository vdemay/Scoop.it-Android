package com.odeval.scoopit.model;

import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class Source implements Parcelable {

	private Long id;
	private String name;
	private String description;
	private String type;
	private String iconUrl;
	private String url;
	
	public Source() {
        super();
    }
	
	public Source(JSONObject json) {
        super();
        popupateFromJsonObject(json);
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(type);
        dest.writeString(iconUrl);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
        public Source[] newArray(int size) {
            return new Source[size];
        }

        public Source createFromParcel(Parcel source) {
            return new Source(source);
        }
    };

    private Source(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        type = in.readString();
        iconUrl = in.readString();
        url = in.readString();
    }
    
    public int describeContents() {
        return 0;
    }
    
    public void popupateFromJsonObject(JSONObject obj) {
    	if (obj == null) {
    		return;
    	}
    	id = obj.optLong("id");
    	name = obj.optString("name", null);
    	description = obj.optString("description", null);
    	type = obj.optString("type", null);
    	iconUrl = obj.optString("iconUrl", null);
    	url = obj.optString("url", null);
    }
}
