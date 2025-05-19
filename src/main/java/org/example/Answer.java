package org.example;

public class Answer {
    private int id;
    private String question_id;
    private String content;

    public Answer() {
    }
    public Answer(String question_id, String content) {
        this.question_id = question_id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
