package com.example.cloneInstragram.services;

import com.example.cloneInstragram.entity.Like;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.PostInteraction;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.LikeRepo;
import com.example.cloneInstragram.repos.PostInteractionRepo;
import com.example.cloneInstragram.repos.PostRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class LikeService {

    @Autowired
    private LikeRepo likeRepository;

    @Autowired
    private PostInteractionRepo postInteractionRepo;

    @Autowired
    private PostRepo postRepository;
    @Autowired
    private NotificationService notificationService;

    public void likePost(User user, Long postId) {
        Post post = postRepository.findById((long) Math.toIntExact(postId))
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            throw new RuntimeException("User already liked this post");
        }

        Post post1 = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        PostInteraction interaction = new PostInteraction();
        interaction.setUser(user);
        interaction.setPost(post1);
        interaction.setType("LIKE");
        postInteractionRepo.save(interaction);

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
        notificationService.createNotification(
                post.getUser(), // Владелец поста (он получит уведомление)
                user,           // Кто поставил лайк
                "LIKE",                 // Тип уведомления
                user.getUsername() + " liked your post",
                postId                   // ID поста
        );
    }

    public void unlikePost(User user, Long postId) {
        Like like = likeRepository.findByUserIdAndPostId(user.getId(), postId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
    }
}



