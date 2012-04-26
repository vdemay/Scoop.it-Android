package com.scoopit.android.curation.model;

import java.io.Serializable;

import org.json.JSONObject;

public class Sharer implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5615240378921118079L;
    
    
    private String sharerName;
    private String sharerId;
    private Long cnxId;
    private String name;
    private boolean mustSpecifyShareText;

    public String getSharerName() {
        return sharerName;
    }

    public void setSharerName(String sharerName) {
        this.sharerName = sharerName;
    }

    public String getSharerId() {
        return sharerId;
    }

    public void setSharerId(String sharerId) {
        this.sharerId = sharerId;
    }

    public Long getCnxId() {
        return cnxId;
    }

    public void setCnxId(Long cnxId) {
        this.cnxId = cnxId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMustSpecifyShareText() {
        return mustSpecifyShareText;
    }

    public void setMustSpecifyShareText(boolean mustSpecifyShareText) {
        this.mustSpecifyShareText = mustSpecifyShareText;
    }

    public void popupateFromJsonObject(JSONObject obj) {
        if (obj == null) {
            return;
        }
        cnxId = obj.optLong("cnxId");
        sharerName = obj.optString("sharerName", null);
        sharerId = obj.optString("sharerId", null);
        name = obj.optString("name", null);
        mustSpecifyShareText = obj.optBoolean("mustSpecifyShareText", false);
    }
    
    public String generateJsonFragment(String text) {
        String fragment = "{\"sharerId\": \""+ sharerId +"\", \"cnxId\": "+ cnxId;
        if (text != null) {
            fragment +=", \"text\": \"" + text + "\"";
        }
        fragment += "}";
        return fragment;
    }

}
