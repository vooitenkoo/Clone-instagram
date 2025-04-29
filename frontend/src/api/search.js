import api from "./index";

export const searchUsers = (query) => api.get(`/api/profile/search?query=${query}`);