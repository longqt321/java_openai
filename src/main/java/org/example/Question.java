package org.example;


public class Question {
    private int id;
    private String content;
    private String audio_url;
    private String type;
    private String level;

    public Question() {
    }
    public Question(String content, String audio_url, String type, String level) {
        this.content = content;
        this.audio_url = audio_url;
        this.type = type;
        this.level = level;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
