import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { searchUsers } from "../api";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import Loader from "../component/Loader";

const Search = () => {
    const [query, setQuery] = useState("");

    const { data: users, isLoading, error } = useQuery({
        queryKey: ["search", query],
        queryFn: () => searchUsers(query),
        enabled: !!query,
    });

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
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Search users..."
                    className="insta-input w-full mb-4"
                />
                {isLoading && <Loader />}
                {users?.length > 0 ? (
                    <div className="space-y-4">
                        {users.map((user) => (
                            <Link to={`/profile/${user.username}`} key={user.id}>
                                <div className="flex items-center p-2 hover:bg-gray-100 dark:hover:bg-gray-800 transition rounded-lg">
                                    <img
                                        src={user.profilePicture || "https://via.placeholder.com/50"}
                                        alt="avatar"
                                        className="w-12 h-12 rounded-full object-cover mr-3"
                                    />
                                    <div>
                                        <p className="font-semibold text-insta-dark dark:text-white">{user.username}</p>
                                        <p className="text-sm text-gray-500 dark:text-gray-400">{user.name || "No name"}</p>
                                    </div>
                                </div>
                            </Link>
                        ))}
                    </div>
                ) : query && !isLoading ? (
                    <p className="text-center text-insta-dark dark:text-gray-300">No users found.</p>
                ) : null}
            </div>
        </motion.div>
    );
};

export default Search;