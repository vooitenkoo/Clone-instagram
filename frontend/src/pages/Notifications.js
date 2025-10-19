import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getNotifications, markNotificationAsRead } from "../api";
import { motion } from "framer-motion";
import Loader from "../component/Loader";
import { useAuth } from "../hooks/useAuth";
import { formatDistanceToNow } from "date-fns";
import { Link } from "react-router-dom";

const Notifications = () => {
  const { user } = useAuth();
  const queryClient = useQueryClient();

  const { data: notifications, isLoading, error } = useQuery({
    queryKey: ["notifications"],
    queryFn: () => getNotifications(user?.id),
  });

  const markAsReadMutation = useMutation({
    mutationFn: (id) => markNotificationAsRead(id),
    onSuccess: () => queryClient.invalidateQueries(["notifications"]),
  });

  if (isLoading) return <Loader />;
  if (error)
    return (
        <div className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black">
          <p className="text-red-500 text-lg font-semibold">Error: {error.message}</p>
        </div>
    );

  return (
      <motion.div
          className="max-w-md mx-auto pt-4 pb-20 bg-insta-gray dark:bg-black"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
      >
        <div className="p-4">
          <h2 className="text-lg font-semibold text-insta-dark dark:text-white mb-4">Notifications</h2>
          {notifications?.length > 0 ? (
              <div className="space-y-4">
                {notifications.map((notification) => (
                    <div
                        key={notification.id}
                        className={`flex items-center p-2 rounded-lg ${
                            notification.read ? "bg-gray-100 dark:bg-gray-800" : "bg-white dark:bg-gray-900"
                        }`}
                    >
                      <Link to={`/profile/${notification.fromUser?.username}`}>
                        <img
                            src={notification.fromUser?.profilePicture || "https://via.placeholder.com/40"}
                            alt="avatar"
                            className="w-10 h-10 rounded-full object-cover mr-3"
                        />
                      </Link>
                      <div className="flex-1">
                        <p className="text-sm text-insta-dark dark:text-white">
                          <Link to={`/profile/${notification.fromUser?.username}`}>
                            <strong>{notification.fromUser?.username}</strong>
                          </Link>{" "}
                          {notification.message}
                        </p>
                        <p className="text-xs text-gray-500 dark:text-gray-400">
                          {formatDistanceToNow(new Date(notification.createdAt), { addSuffix: true })}
                        </p>
                      </div>
                      {!notification.read && (
                          <button
                              onClick={() => markAsReadMutation.mutate(notification.id)}
                              className="text-insta-blue text-sm"
                          >
                            Mark as read
                          </button>
                      )}
                    </div>
                ))}
              </div>
          ) : (
              <p className="text-center text-insta-dark dark:text-gray-300">No notifications yet.</p>
          )}
        </div>
      </motion.div>
  );
};

export default Notifications;