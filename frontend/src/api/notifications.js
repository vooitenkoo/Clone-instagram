import api from "./index";

export const getNotifications = (userId) =>
    api.get(`/api/notifications/${userId}`);
export const markNotificationAsRead = (id) =>
    api.put(`/api/notifications/${id}/read`);