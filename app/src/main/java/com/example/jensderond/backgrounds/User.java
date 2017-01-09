package com.example.jensderond.backgrounds;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jensderond on 04/01/2017.
 */

public class User {
    private String id, username, name, profile_link, profile_image;

    public User(JSONObject user){
        ParseJSON(user);
    }

    public void ParseJSON(JSONObject user){
        try {
            this.setId(user.getString("id"));
            this.setName(user.getString("name"));
            this.setUsername(user.getString("username"));

            JSONObject objectProfileImage = ((JSONObject) user).getJSONObject("profile_image");
            this.setProfile_image(objectProfileImage.getString("medium"));

            JSONObject objectProfileLink = ((JSONObject) user).getJSONObject("links");
            this.setProfile_link(objectProfileLink.getString("html"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_link() {
        return profile_link;
    }

    public void setProfile_link(String profile_link) {
        this.profile_link = profile_link;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
