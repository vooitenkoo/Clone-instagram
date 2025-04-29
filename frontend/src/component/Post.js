import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { likePost, unlikePost } from "../api";
import { createComment, deleteComment } from "../api";
import { HeartIcon, ChatBubbleOvalLeftIcon } from "@heroicons/react/24/outline";
import { HeartIcon as HeartSolid } from "@heroicons/react/24/solid";
import { useAuth } from "../hooks/useAuth";
import { motion, AnimatePresence } from "framer-motion";
import { formatDistanceToNow } from "date-fns";
import { Link } from "react-router-dom";

const Post = ({ post }) => {
    const { user } = useAuth();
    const queryClient = useQueryClient();
    const [comment, setComment] = useState("");
    const [showComments, setShowComments] = useState(false);

    const likeMutation = useMutation({
        mutationFn: () => likePost(post.id),
        onSuccess: () => queryClient.invalidateQueries(["posts"]),
    });

    const unlikeMutation = useMutation({
        mutationFn: () => unlikePost(post.id),
        onSuccess: () => queryClient.invalidateQueries(["posts"]),
    });

    const commentMutation = useMutation({
        mutationFn: (text) => createComment(post.id, text),
        onSuccess: () => {
            queryClient.invalidateQueries(["posts"]);
            setComment("");
        },
    });

    const deleteCommentMutation = useMutation({
        mutationFn: (commentId) => deleteComment(commentId),
        onSuccess: () => queryClient.invalidateQueries(["posts"]),
    });

    const isLiked = post.likes?.some((like) => like.userId === user?.id);

    // Логируем post для отладки
    console.log("Post data:", post);

    // Проверяем наличие post.user и post.user.username
    const username = post.user?.username || "Unknown User";
    const profilePicture = post.user?.profilePicture || "https://via.placeholder.com/40";

    // Безопасно форматируем дату
    const formatTimeAgo = (date) => {
        try {
            if (!date) return "Неизвестная дата";
            const parsedDate = new Date(date);
            if (isNaN(parsedDate.getTime())) return "Некорректная дата";
            return formatDistanceToNow(parsedDate, { addSuffix: true });
        } catch (error) {
            console.error("Ошибка форматирования даты:", error);
            return "Ошибка даты";
        }
    };

    const timeAgo = formatTimeAgo(post.createdAt);

    return (
        <motion.div
            className="insta-card mb-6"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
        >
            <div className="flex items-center p-4">
                <Link to={`/profile/${username}`}>
                    <img
                        src={profilePicture}
                        alt="avatar"
                        className="w-10 h-10 rounded-full object-cover border border-gray-200 dark:border-gray-700"
                    />
                </Link>
                <Link to={`/profile/${username}`}>
                    <span className="ml-3 font-semibold text-insta-dark dark:text-white">{username}</span>
                </Link>
            </div>
            <img
                src={post.image || "https://via.placeholder.com/500"}
                alt="post"
                className="w-full h-auto object-cover"
            />
            <div className="p-4">
                <div className="flex space-x-4 mb-3">
                    <button
                        onClick={() => (isLiked ? unlikeMutation.mutate() : likeMutation.mutate())}
                        className="flex items-center hover:text-red-500 transition"
                    >
                        {isLiked ? (
                            <HeartSolid className="h-6 w-6 text-red-500" />
                        ) : (
                            <HeartIcon className="h-6 w-6 text-insta-dark dark:text-gray-300" />
                        )}
                        <span className="ml-1 text-sm text-insta-dark dark:text-gray-300">{post.likes?.length || 0}</span>
                    </button>
                    <button
                        onClick={() => setShowComments(!showComments)}
                        className="flex items-center text-insta-dark dark:text-gray-300 hover:text-insta-blue transition"
                    >
                        <ChatBubbleOvalLeftIcon className="h-6 w-6" />
                        <span className="ml-1 text-sm">{post.comments?.length || 0}</span>
                    </button>
                </div>
                <p className="text-insta-dark dark:text-white text-sm mb-2">{post.content}</p>
                <p className="text-gray-500 dark:text-gray-400 text-xs mb-2">
                    {timeAgo}
                </p>
                <AnimatePresence>
                    {showComments && (
                        <motion.div
                            initial={{ height: 0, opacity: 0 }}
                            animate={{ height: "auto", opacity: 1 }}
                            exit={{ height: 0, opacity: 0 }}
                            transition={{ duration: 0.3 }}
                            className="mb-3"
                        >
                            {post.comments?.map((c) => (
                                <div key={c.id} className="flex justify-between items-start text-sm mb-2">
                                    <p>
                                        <strong className="text-insta-dark dark:text-white">{c.user?.username || "Unknown"}</strong>{" "}
                                        <span className="text-gray-600 dark:text-gray-400">{c.text}</span>
                                    </p>
                                    {c.user?.id === user?.id && (
                                        <button
                                            onClick={() => deleteCommentMutation.mutate(c.id)}
                                            className="text-red-500 text-xs hover:text-red-700"
                                        >
                                            Delete
                                        </button>
                                    )}
                                </div>
                            ))}
                        </motion.div>
                    )}
                </AnimatePresence>
                <div className="flex items-center">
                    <input
                        type="text"
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        placeholder="Add a comment..."
                        className="insta-input flex-1"
                    />
                    <button
                        onClick={() => commentMutation.mutate(comment)}
                        className="ml-2 text-insta-blue font-semibold text-sm disabled:opacity-50"
                        disabled={!comment.trim()}
                    >
                        Post
                    </button>
                </div>
            </div>
        </motion.div>
    );
};

export default Post;