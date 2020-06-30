package com.google.sps.data;

import java.util.*;

public class CommentData {

  private String name;
  private String comment;
  private Date posted;

  public CommentData(String name, String comment, Date posted) {
    this.name = name;
    this.comment = comment;
    this.posted = posted;
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
}