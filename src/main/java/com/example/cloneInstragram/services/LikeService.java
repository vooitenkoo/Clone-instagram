package com.example.cloneInstragram.services;

import com.example.cloneInstragram.entity.Like;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.LikeRepo;
import com.example.cloneInstragram.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LikeService {

    @Autowired
    private LikeRepo likeRepository;

    @Autowired
    private PostRepo postRepository;

    public void likePost(User user, Long postId) {
        Post post = postRepository.findById((long) Math.toIntExact(postId))
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new RuntimeException("User already liked this post");
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
    }

    public void unlikePost(User user, Long postId) {
        Like like = likeRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
    }
}



