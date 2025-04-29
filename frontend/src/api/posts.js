import api from "./index";

export const createPost = (formData) =>
    api.post("/api/posts/createPosts", formData, {
        headers: { "Content-Type": "multipart/form-data" },
    });

export const getPosts = async (offset = 0, limit = 10) => {
    try {
        const response = await api.get(`/api/posts?offset=${offset}&limit=${limit}`);
        console.log("getPosts response:", response.data); // Логируем ответ
        // Если бэкенд возвращает объект { data: [...] }, извлекаем массив
        return Array.isArray(response.data) ? response.data : response.data.data || [];
    } catch (error) {
        console.error("getPosts error:", error);
        throw error;
    }
};

export const getRecommendedPosts = () => api.get("/api/posts/recommendation");