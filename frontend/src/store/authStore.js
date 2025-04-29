import { create } from "zustand";
import { login, refreshToken } from "../api";

const useAuthStore = create((set) => ({
    token: localStorage.getItem("authToken") || null,
    user: null,
    isLoading: false,
    error: null,

    setToken: (token) => {
        localStorage.setItem("authToken", token);
        set({ token });
    },

    setUser: (user) => set({ user }),

    login: async (username, password) => {
        set({ isLoading: true, error: null });
        try {
            const { data } = await login({ username, password });
            localStorage.setItem("authToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);
            set({ token: data.accessToken, isLoading: false });
        } catch (error) {
            set({ error: error.response?.data?.error || "Login failed", isLoading: false });
            throw error;
        }
    },

    logout: () => {
        localStorage.removeItem("authToken");
        localStorage.removeItem("refreshToken");
        set({ token: null, user: null });
    },

    refresh: async () => {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) return;
        try {
            const { data } = await refreshToken(refreshToken);
            localStorage.setItem("authToken", data.accessToken);
            set({ token: data.accessToken });
        } catch (error) {
            set({ token: null, user: null });
            localStorage.removeItem("authToken");
            localStorage.removeItem("refreshToken");
        }
    },
}));

export default useAuthStore;