import api from "./index";

export const getProfile = (username, offset = 0, limit = 10) =>
    api.get(`/api/profile/${username || ""}?offset=${offset}&limit=${limit}`);
export const updateProfile = (formData) =>
    api.put("/api/profile/edit", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });
export const getCurrentUser = () => api.get("/users/me");