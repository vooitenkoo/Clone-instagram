import api from "./index";

export const createComment = (postId, text) =>
    api.post(`/api/posts/comments/create/${postId}?text=${text}`);
export const getComments = (postId) => api.get(`/api/posts/comments/${postId}`);
export const deleteComment = (commentId) =>
    api.delete(`/api/posts/comments/${commentId}`);