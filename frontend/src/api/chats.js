import api from "./index";

export const createChat = (recipientId) =>
    api.post(`/chats/create?recipientId=${recipientId}`);
export const getChats = () => api.get("/chats");
export const getChatUsers = (chatId) => api.get(`/chats/${chatId}/users`);
export const getMessages = (senderId, recipientId, page = 0, size = 100) =>
    api.get(`/messages/${senderId}/${recipientId}?page=${page}&size=${size}`);
export const markMessagesAsRead = (chatId) =>
    api.post(`/messages/${chatId}/read`);