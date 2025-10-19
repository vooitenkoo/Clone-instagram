import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { motion, AnimatePresence } from 'framer-motion';
import { MagnifyingGlassIcon } from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';

export default function Search() {
  const { api } = useAuth();
  const [query, setQuery] = useState('');
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const searchUsers = async () => {
      if (!query.trim()) {
        setUsers([]);
        return;
      }

      setIsLoading(true);
      try {
        const response = await api.get('/profile/search', {
          params: { query },
        });
        setUsers(response.data);
      } catch (error) {
        console.error('Error searching users:', error);
      } finally {
        setIsLoading(false);
      }
    };

    const timeoutId = setTimeout(searchUsers, 300);
    return () => clearTimeout(timeoutId);
  }, [query]);

  return (
    <div className="max-w-2xl mx-auto px-4 py-8">
      <div className="relative">
        <div className="relative">
          <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-primary-400" />
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Search users..."
            className="w-full pl-10 pr-4 py-3 rounded-lg border border-primary-200 dark:border-primary-800 bg-white dark:bg-primary-900 text-primary-900 dark:text-white placeholder-primary-400 focus:outline-none focus:ring-2 focus:ring-instagram-blue"
          />
        </div>

        <AnimatePresence mode="wait">
          {isLoading ? (
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              className="flex justify-center py-8"
            >
              <div className="w-6 h-6 border-2 border-instagram-blue border-t-transparent rounded-full animate-spin" />
            </motion.div>
          ) : (
            users.length > 0 && (
              <motion.div
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: 20 }}
                className="absolute inset-x-0 top-full mt-2 bg-white dark:bg-primary-900 rounded-lg shadow-lg border border-primary-200 dark:border-primary-800 overflow-hidden"
              >
                {users.map((user) => (
                  <Link
                    key={user.id}
                    to={`/profile/${user.username}`}
                    className="flex items-center gap-3 p-4 hover:bg-primary-50 dark:hover:bg-primary-800 transition-colors"
                  >
                    <img
                      src={user.profilePicture || '/default-avatar.png'}
                      alt={user.username}
                      className="w-10 h-10 rounded-full object-cover"
                    />
                    <div>
                      <div className="font-semibold text-primary-900 dark:text-white">
                        {user.username}
                      </div>
                      {user.bio && (
                        <div className="text-sm text-primary-500 dark:text-primary-400 line-clamp-1">
                          {user.bio}
                        </div>
                      )}
                    </div>
                  </Link>
                ))}
              </motion.div>
            )
          )}
        </AnimatePresence>
      </div>

      {!isLoading && query && users.length === 0 && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-center py-8 text-primary-500 dark:text-primary-400"
        >
          No users found
        </motion.div>
      )}

      {!query && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-center py-8 text-primary-500 dark:text-primary-400"
        >
          Try searching for usernames
        </motion.div>
      )}
    </div>
  );
} 