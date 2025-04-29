import axios from "axios";
import useAuthStore from "../store/authStore";

const api = axios.create({
    baseURL: "http://localhost:8082",
    headers: {
        "Content-Type": "application/json",
    },
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("authToken");
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            try {
                const { refresh } = useAuthStore.getState();
                await refresh();
                const newToken = localStorage.getItem("authToken");
                originalRequest.headers.Authorization = `Bearer ${newToken}`;
                return api(originalRequest);
            } catch (refreshError) {
                useAuthStore.getState().logout();
                window.location.href = "/login";
                return Promise.reject(refreshError);
            }
        }
        return Promise.reject(error);
    }
);

export * from "./auth";
export * from "./posts";
export * from "./profile";
export * from "./chats";
export * from "./follow";
export * from "./comments";
export * from "./likes";
export * from "./notifications";
export * from "./search";

export default api;