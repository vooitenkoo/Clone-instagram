import api from "./index";

export const likePost = (postId) => api.post(`/api/posts/likes/${postId}`);
export const unlikePost = (postId) => api.delete(`/api/posts/likes/${postId}`);