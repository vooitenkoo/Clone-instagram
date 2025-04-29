import { motion } from "framer-motion";
import { Link } from "react-router-dom";

const Story = ({ user }) => (
    <Link to={`/profile/${user.username}`}>
        <motion.div
            className="flex flex-col items-center cursor-pointer"
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
            transition={{ duration: 0.2 }}
        >
            <div className="w-16 h-16 rounded-full bg-gradient-to-r from-pink-500 via-red-500 to-yellow-500 p-1">
                <img
                    src={user.profilePicture || "https://via.placeholder.com/60"}
                    alt="story"
                    className="w-full h-full rounded-full border-2 border-white dark:border-gray-900 object-cover"
                />
            </div>
            <span className="text-xs mt-1 truncate w-16 text-center text-insta-dark dark:text-gray-300">
        {user.username}
      </span>
        </motion.div>
    </Link>
);

export default Story;