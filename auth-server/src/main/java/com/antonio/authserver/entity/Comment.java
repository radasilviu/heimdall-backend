//package com.antonio.authserver.entity;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import javax.persistence.*;
//
//@Entity
//@Table(name = "comment")
//@Data
//@AllArgsConstructor
//public class Comment {
//    @Id
//    @GeneratedValue
//    private Integer id;
//    private String comment;
//
//    @ManyToOne
//    @JoinColumn(name = "post_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Post post;
//
//    public Comment(String comment, Post post) {
//        this.comment = comment;
//        this.post = post;
//    }
//
//    public Comment() {
//
//    }
//
//    public String getComment() {
//        return comment;
//    }
//
//    public void setComment(String comment) {
//        this.comment = comment;
//    }
//
//    public Post getPost() {
//        return post;
//    }
//
//    public void setPost(Post post) {
//        this.post = post;
//    }
//}
