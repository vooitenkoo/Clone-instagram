package com.example;

import com.example.cloneInstragram.application.post.dto.PostDto;
import com.example.cloneInstragram.application.post.service.PostService;
import com.example.cloneInstragram.application.storage.service.MinioService;
import com.example.cloneInstragram.domain.post.model.Post;
import com.example.cloneInstragram.domain.post.repository.PostInteractionRepo;
import com.example.cloneInstragram.domain.post.repository.PostRepo;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.FollowRepo;
import com.example.cloneInstragram.domain.user.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PostServiceTest {

    private PostService postService;
    private PostRepo postRepository;
    private UserRepo userRepository;
    private MinioService minioService;
    private FollowRepo followRepo;
    private PostInteractionRepo postInteractionRepo;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepo.class);
        userRepository = mock(UserRepo.class);
        minioService = mock(MinioService.class);
        followRepo = mock(FollowRepo.class);
        postInteractionRepo = mock(PostInteractionRepo.class);

        postService = new PostService(postRepository, userRepository, minioService, followRepo, postInteractionRepo);
    }

    @Test
    void testCreatePost_Success() {
        // Arrange
        String username = "testuser";
        String content = "This is a test post";
        MultipartFile image = mock(MultipartFile.class);

        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(image.isEmpty()).thenReturn(false);
        when(minioService.uploadFile(image, "post")).thenReturn("http://image.url");
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            post.setId(1L);  // Симулируем, что у сохраненного поста есть ID
            return post;
        });

        // Act
        PostDto createdPost = postService.createPost(username, content, image);

        // Assert
        assertNotNull(createdPost);
        assertEquals(content, createdPost.getContent());
        assertEquals(username, createdPost.getUsername());
        assertEquals("http://image.url", createdPost.getImageUrl());

        // Дополнительно проверяем что сохраняется пост
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();
        assertEquals(content, savedPost.getCaption());
        assertEquals(user, savedPost.getUser());
        assertNotNull(savedPost.getCreatedAt());
    }

    @Test
    void testCreatePost_UserNotFound_ShouldThrow() {
        // Arrange
        String username = "nonexistent";
        String content = "Some content";
        MultipartFile image = null;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.createPost(username, content, image);
        });

        assertEquals("User not found", exception.getMessage());
    }
}
