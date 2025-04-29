import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateProfile } from "../api";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

const EditProfile = () => {
    const { user } = useAuth();
    const [bio, setBio] = useState(user?.bio || "");
    const [profilePicture, setProfilePicture] = useState(null);
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: (formData) => updateProfile(formData),
        onSuccess: () => {
            queryClient.invalidateQueries(["profile"]);
            navigate("/profile");
        },
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("bio", bio);
        if (profilePicture) formData.append("profilePicture", profilePicture);
        mutation.mutate(formData);
    };

    return (
        <motion.div
            className="max-w-md mx-auto pt-4 pb-20 bg-insta-gray dark:bg-black"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <div className="p-4">
                <h2 className="text-lg font-semibold text-insta-dark dark:text-white mb-4">Edit Profile</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="flex items-center">
                        <img
                            src={
                                profilePicture
                                    ? URL.createObjectURL(profilePicture)
                                    : user?.profilePicture || "https://via.placeholder.com/80"
                            }
                            alt="profile"
                            className="w-20 h-20 rounded-full object-cover mr-4"
                        />
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setProfilePicture(e.target.files[0])}
                            className="block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-insta-blue file:text-white hover:file:bg-blue-700"
                        />
                    </div>
                    <textarea
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                        placeholder="Bio"
                        className="insta-input w-full h-24 resize-none"
                    />
                    <button type="submit" className="insta-button w-full" disabled={mutation.isLoading}>
                        {mutation.isLoading ? "Saving..." : "Save"}
                    </button>
                </form>
            </div>
        </motion.div>
    );
};

export default EditProfile;