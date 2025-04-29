import api from "./index";

export const followUser = (followingId) => api.post(`/api/follow/${followingId}`);
export const unfollowUser = (followingId) =>
    api.delete(`/api/follow/${followingId}`);
export const getFollowers = () => api.get("/api/follow/followers");
export const getFollowing = () => api.get("/api/follow/following");
export const getFollowersByUserId = (userId) =>
    api.get(`/api/follow/${userId}/followers`);
export const getFollowingByUserId = (userId) =>
    api.get(`/api/follow/${userId}/following`);