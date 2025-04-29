import { useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import useAuthStore from "../store/authStore";
import { getCurrentUser } from "../api";

export const useAuth = () => {
    const { token, setUser, logout } = useAuthStore();

    const { data, isLoading, error } = useQuery({
        queryKey: ["currentUser"],
        queryFn: async () => {
            try {
                const response = await getCurrentUser();
                return response.data;
            } catch (err) {
                console.error("getCurrentUser error:", err);
                throw err;
            }
        },
        enabled: !!token,
        retry: false,
    });

    useEffect(() => {
        if (data) {
            console.log("useAuth: User data received:", data);
            setUser(data);
        }
        if (error) {
            console.error("useAuth: Error:", error);
            logout();
        }
    }, [data, error, setUser, logout]);

    return {
        user: data || null,
        isLoading,
        isAuthenticated: !!token,
        error,
    };
};