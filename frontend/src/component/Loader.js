import { motion } from "framer-motion";

const Loader = () => (
    <motion.div
        className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black"
        animate={{ rotate: 360 }}
        transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
    >
        <div className="w-12 h-12 border-4 border-t-insta-blue border-gray-200 dark:border-gray-700 rounded-full" />
    </motion.div>
);

export default Loader;