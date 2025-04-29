import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createPost } from "../api";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

const PostCreate = () => {
    const [content, setContent] = useState("");
    const [image, setImage] = useState(null);
    const navigate = useNavigate();
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: (formData) => createPost(formData),
        onSuccess: () => {
            queryClient.invalidateQueries(["posts"]);
            navigate("/");
        },
    });

    const handleSubmit = (e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append("content", content);
        if (image) formData.append("image", image);
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
                <h2 className="text-lg font-semibold text-insta-dark dark:text-white mb-4">Create a Post</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
          <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="What's on your mind?"
              className="insta-input w-full h-24 resize-none"
          />
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setImage(e.target.files[0])}
                        className="block w-full text-sm text-gray-500 dark:text-gray-400 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-semibold file:bg-insta-blue file:text-white hover:file:bg-blue-700"
                    />
                    {image && (
                        <img
                            src={URL.createObjectURL(image)}
                            alt="preview"
                            className="w-full h-64 object-cover rounded-lg mt-2"
                        />
                    )}
                    <button type="submit" className="insta-button w-full" disabled={mutation.isLoading}>
                        {mutation.isLoading ? "Posting..." : "Post"}
                    </button>
                </form>
            </div>
        </motion.div>
    );
};

export default PostCreate;