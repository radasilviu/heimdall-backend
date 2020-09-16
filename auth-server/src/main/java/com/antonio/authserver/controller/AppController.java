//package com.antonio.authserver.controller;
//
//import com.antonio.authserver.entity.Comment;
//import com.antonio.authserver.entity.Post;
//import com.antonio.authserver.repository.CommentRepository;
//import com.antonio.authserver.repository.PostRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("post")
//public class AppController {
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @GetMapping
//    public List<Post> getAllPost(){
//        return postRepository.findAll();
//    }
//
//    @PostMapping
//    public Post savePost(@RequestBody Post post){
//        return postRepository.save(post);
//    }
//
//    @GetMapping("/{id}/comment")
//    public List<Comment> getPostsComment(@PathVariable(value = "id")Integer postId){
//        return commentRepository.findCommentByPostId(postId);
//    }
//
//    @PostMapping("/{id}/comment")
//    public Comment saveComment(@PathVariable(value = "id")Integer postId, @RequestBody Comment comment) throws Exception {
//        return postRepository.findById(postId).map(post -> {
//            comment.setPost(post);
//            return commentRepository.save(comment);
//        }).orElseThrow(() -> new Exception());
//    }
//
//}
