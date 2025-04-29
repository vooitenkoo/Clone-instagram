import { useQuery } from "@tanstack/react-query";
import { getProfile } from "../api";
import { useAuth } from "../hooks/useAuth";
import { Link, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import Loader from "../component/Loader";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { followUser, unfollowUser } from "../api";

const Profile = () => {
    const { username } = useParams();
    const { user } = useAuth();
    const queryClient = useQueryClient();

    const { data: profile, isLoading, error } = useQuery({
        queryKey: ["profile", username],
        queryFn: () => getProfile(username || user?.username),
    });

    const followMutation = useMutation({
        mutationFn: (followingId) => followUser(followingId),
        onSuccess: () => queryClient.invalidateQueries(["profile", username]),
    });

    const unfollowMutation = useMutation({
        mutationFn: (followingId) => unfollowUser(followingId),
        onSuccess: () => queryClient.invalidateQueries(["profile", username]),
    });

    if (isLoading) return <Loader />;
    if (error)
        return (
            <div className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black">
                <p className="text-red-500 text-lg font-semibold">Error: {error.message}</p>
            </div>
        );

    const isOwnProfile = user?.username === profile?.username;
    const isFollowing = profile?.followers?.some((f) => f.id === user?.id);

    return (
        <motion.div
            className="max-w-md mx-auto pt-4 pb-20 bg-insta-gray dark:bg-black"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <div className="p-4">
                <div className="flex items-center">
                    <img
                        src={profile?.profilePicture || "https://via.placeholder.com/80"}
                        alt="profile"
                        className="w-20 h-20 rounded-full object-cover border border-gray-200 dark:border-gray-700 mr-4"
                    />
                    <div className="flex-1">
                        <h2 className="font-semibold text-insta-dark dark:text-white">{profile?.username}</h2>
                        <div className="flex space-x-4 mt-2">
                            <div>
                                <span className="font-semibold text-insta-dark dark:text-white">{profile?.posts?.length || 0}</span>{" "}
                                <span className="text-gray-500 dark:text-gray-400">posts</span>
                            </div>
                            <div>
                                <span className="font-semibold text-insta-dark dark:text-white">{profile?.followers?.length || 0}</span>{" "}
                                <span className="text-gray-500 dark:text-gray-400">followers</span>
                            </div>
                            <div>
                                <span className="font-semibold text-insta-dark dark:text-white">{profile?.following?.length || 0}</span>{" "}
                                <span className="text-gray-500 dark:text-gray-400">following</span>
                            </div>
                        </div>
                    </div>
                </div>
                <p className="mt-2 text-insta-dark dark:text-white">{profile?.bio || "No bio yet."}</p>
                {isOwnProfile ? (
                    <Link to="/edit-profile">
                        <button className="insta-button w-full mt-4">Edit Profile</button>
                    </Link>
                ) : (
                    <button
                        onClick={() =>
                            isFollowing
                                ? unfollowMutation.mutate(profile.id)
                                : followMutation.mutate(profile.id)
                        }
                        className={`insta-button w-full mt-4 ${isFollowing ? "bg-gray-300 dark:bg-gray-600" : ""}`}
                    >
                        {isFollowing ? "Unfollow" : "Follow"}
                    </button>
                )}
            </div>
            <div className="grid grid-cols-3 gap-1">
                {profile?.posts?.map((post) => (
                    <img
                        key={post.id}
                        src={post.image || "https://via.placeholder.com/150"}
                        alt="post"
                        className="w-full h-32 object-cover"
                    />
                ))}
            </div>
        </motion.div>
    );
};

export default Profile;