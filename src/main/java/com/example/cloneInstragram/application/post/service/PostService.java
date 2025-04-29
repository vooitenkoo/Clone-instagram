package com.example.cloneInstragram.application.post.service;

import com.example.cloneInstragram.application.post.dto.PostDto;
import com.example.cloneInstragram.application.post.mapper.PostMapper;
import com.example.cloneInstragram.application.storage.service.MinioService;
import com.example.cloneInstragram.domain.post.model.Post;
import com.example.cloneInstragram.domain.post.repository.PostInteractionRepo;
import com.example.cloneInstragram.domain.post.repository.PostRepo;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.FollowRepo;
import com.example.cloneInstragram.domain.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepository;
    private final UserRepo userRepository;
    private final MinioService minioService;
    private final FollowRepo followRepo;
    private final PostInteractionRepo postInteractionRepo;

    public PostDto createPost(String username, String content, MultipartFile image) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = minioService.uploadFile(image, "post");
        }

        PostDto postDto = new PostDto(0, content, imageUrl, 0, 0, username);
        Post post = PostMapper.toPost(postDto);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);
        return PostMapper.toPostDto(savedPost);
    }

    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return PostMapper.toPostDto(post);
    }

    public Post getPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You can delete only your own posts");
        }

        postRepository.delete(post);
    }

    public List<PostDto> getAllPosts(int offset, int limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findAll(pageRequest);

        return posts.stream()
                .map(PostMapper::toPostDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getAllPosts(Integer userId, int offset, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByUserId(userId, pageRequest);

        return posts.stream()
                .map(PostMapper::toPostDto)
                .collect(Collectors.toList());
    }

    public List<PostDto> getRecommendedPosts(User user) {
        // 1. Получаем ID пользователей, на которых подписан юзер
        List<Long> followedUserIds = followRepo.findFollowedUserIdsByFollowingId(user.getId());
        System.out.println("Followed users: " + followedUserIds);

        List<Post> friendPosts = postRepository.findByUserIdIn(followedUserIds);

        List<Long> likedPostIds = postInteractionRepo.findPostIdsLikedByUser(user.getId());
        List<User> similarUsers = postInteractionRepo.findUsersWhoLikedSamePosts(likedPostIds, user.getId());

        List<Long> recommendedPostIds = postInteractionRepo.findPostsLikedByUser(similarUsers);
        List<Post> recommendedPosts = postRepository.findByIdIn(recommendedPostIds);

        // 5. Добавляем самые популярные посты (по лайкам и комментариям)
        List<Post> trendingPosts = postRepository.findTopTrendingPosts();

        Set<Post> finalFeed = new LinkedHashSet<>();
        finalFeed.addAll(friendPosts);
        finalFeed.addAll(recommendedPosts);
        finalFeed.addAll(trendingPosts);

        finalFeed = finalFeed.stream()
                .filter(post -> !post.getUser().getId().equals(user.getId()))
                .collect(Collectors.toSet());

        return finalFeed.stream()
                .map(PostMapper::toPostDto)
                .toList();
    }
}