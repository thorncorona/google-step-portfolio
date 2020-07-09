package com.google.sps.data;

import java.util.*;

public class Comment {

  private long id;
  private String name;
  private String comment;
  private Date posted;
  private float sentimentScore;

  public Comment(long id, String name, String comment, Date posted, float sentimentScore) {
    this.id = id;
    this.name = name;
    this.comment = comment;
    this.posted = posted;
    this.sentimentScore = sentimentScore;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getDate() {
    return posted;
  }

  public void setDate(Date posted) {
    this.posted = posted;
  }

  public float getSentimentScore() {
    return sentimentScore;
  }

  public void setSentimentScore(float sentimentScore) {
    this.sentimentScore = sentimentScore;
  }

}