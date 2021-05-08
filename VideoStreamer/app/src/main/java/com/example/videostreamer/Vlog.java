package com.example.videostreamer;
public class Vlog {
    private String title,desc,videoUrl,username,profileImageUrl;

    public Vlog(String title, String desc, String videoUrl, String username, String profileImageUrl) {
        this.title = title;
        this.desc = desc;
        this.videoUrl = videoUrl;
        this.username = username;
        this.profileImageUrl=profileImageUrl;
    }
    //empty ctor required for Firebase
    public Vlog(){ }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getProfileImageUrl() { return profileImageUrl; }

    public String getUsername() {
        return username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public void setUsername(String username) {
        this.username = username;
    }
}
