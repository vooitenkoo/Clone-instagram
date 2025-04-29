import { NavLink } from "react-router-dom";
import {
    HomeIcon,
    MagnifyingGlassIcon,
    PlusCircleIcon,
    ChatBubbleOvalLeftIcon,
    UserIcon,
    BellIcon,
} from "@heroicons/react/24/outline";
import { motion } from "framer-motion";

const Navbar = () => (
    <motion.nav
        className="fixed bottom-0 w-full bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700 flex justify-around py-3 shadow-lg z-50"
        initial={{ y: 100 }}
        animate={{ y: 0 }}
        transition={{ duration: 0.3 }}
    >
        <NavLink
            to="/"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <HomeIcon className="h-7 w-7" />
        </NavLink>
        <NavLink
            to="/search"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <MagnifyingGlassIcon className="h-7 w-7" />
        </NavLink>
        <NavLink
            to="/post"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <PlusCircleIcon className="h-7 w-7" />
        </NavLink>
        <NavLink
            to="/notifications"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <BellIcon className="h-7 w-7" />
        </NavLink>
        <NavLink
            to="/direct"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <ChatBubbleOvalLeftIcon className="h-7 w-7" />
        </NavLink>
        <NavLink
            to="/profile"
            className={({ isActive }) =>
                `p-2 ${isActive ? "text-insta-blue" : "text-insta-dark dark:text-gray-300"} hover:text-insta-blue transition`
            }
        >
            <UserIcon className="h-7 w-7" />
        </NavLink>
    </motion.nav>
);

export default Navbar;