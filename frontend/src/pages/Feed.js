import { useQuery } from "@tanstack/react-query";
import { getPosts } from "../api";
import Post from "../component/Post";
import Story from "../component/Story";
import Loader from "../component/Loader";
import { motion } from "framer-motion";

const mockStories = [
    { id: 1, username: "user1", profilePicture: "https://via.placeholder.com/60" },
    { id: 2, username: "user2", profilePicture: "https://via.placeholder.com/60" },
    { id: 3, username: "user3", profilePicture: "https://via.placeholder.com/60" },
];

const Feed = () => {
    const { data, isLoading, error } = useQuery({
        queryKey: ["posts"],
        queryFn: () => getPosts(),
    });

    if (isLoading) return <Loader />;
    if (error)
        return (
            <div className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black">
                <p className="text-red-500 text-lg font-semibold">Error: {error.message}</p>
            </div>
        );

    // Логируем данные с сервера
    console.log("Feed: Data from getPosts:", data);

    // Проверяем, является ли data массивом
    const posts = Array.isArray(data) ? data : data?.data || [];

    return (
        <div className="max-w-md mx-auto pt-4 pb-20 bg-insta-gray dark:bg-black">
            <motion.div
                className="flex overflow-x-auto space-x-4 mb-6 px-4 hide-scrollbar"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ duration: 0.5 }}
            >
                {mockStories.map((story) => (
                    <Story key={story.id} user={story} />
                ))}
            </motion.div>
            <div className="space-y-6">
                {posts.length > 0 ? (
                    posts.map((post) => <Post key={post.id} post={post} />)
                ) : (
                    <p className="text-center text-insta-dark dark:text-gray-300">No posts available.</p>
                )}
            </div>
        </div>
    );
};

export default Feed;