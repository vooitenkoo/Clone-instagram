package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.PostDto;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.PostRepo;

import com.example.cloneInstragram.repos.UserRepo;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepository;
    private final UserRepo userRepository;
    private final MinioService minioService;

    public PostDto createPost(String username, String content, MultipartFile image) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = minioService.uploadFile(image,"post");
        }

        Post post = new Post();
        post.setCaption(content);
        post.setImageUrl(imageUrl);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);
        return toPostDto(post);
    }


    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return toPostDto(post);
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

        return posts.stream().map(this::toPostDto).collect(Collectors.toList());
    }

    public List<PostDto> getAllPosts(Integer userId, int offset, int limit) {
        if (userId == null) {
            return Collections.emptyList();
        }

        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.by("createdAt").descending());
        Page<Post> posts = postRepository.findByUserId(userId, pageRequest);

        return posts.stream().map(this::toPostDto).collect(Collectors.toList());
    }

    private PostDto toPostDto(Post post) {
        return new PostDto(
                Math.toIntExact(post.getId()),
                post.getCaption(),
                post.getImageUrl(),
                post.getLikes().size(),
                post.getComments().size(),
                post.getUser().getUsername()
        );
    }
}
