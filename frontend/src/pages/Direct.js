import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { ChatBubbleOvalLeftIcon, PaperAirplaneIcon } from "@heroicons/react/24/outline";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { getChats, getMessages, markMessagesAsRead } from "../api";
import useWebSocket from "../hooks/useWebSocket";
import Loader from "../component/Loader";

const Direct = () => {
    const queryClient = useQueryClient();
    const [selectedChat, setSelectedChat] = useState(null);
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);

    const { data: chats, isLoading: chatsLoading } = useQuery({
        queryKey: ["chats"],
        queryFn: getChats,
    });

    const { data: chatMessages, isLoading: messagesLoading } = useQuery({
        queryKey: ["messages", selectedChat?.id],
        queryFn: () =>
            getMessages(selectedChat?.senderId, selectedChat?.recipientId),
        enabled: !!selectedChat,
    });

    const { sendMessage } = useWebSocket(selectedChat?.id, (newMessage) => {
        setMessages((prev) => [...prev, newMessage]);
        queryClient.invalidateQueries(["chats"]);
    });

    useEffect(() => {
        if (chatMessages) {
            setMessages(chatMessages);
            if (selectedChat) {
                markMessagesAsRead(selectedChat.id);
            }
        }
    }, [chatMessages, selectedChat]);

    const handleSendMessage = (e) => {
        e.preventDefault();
        if (message.trim()) {
            const newMessage = {
                chatId: selectedChat.id,
                senderId: selectedChat.senderId,
                recipientId: selectedChat.recipientId,
                text: message,
            };
            sendMessage(newMessage);
            setMessages((prev) => [...prev, { ...newMessage, timestamp: new Date() }]);
            setMessage("");
        }
    };

    if (chatsLoading || messagesLoading) return <Loader />;

    // Проверяем, что chats — это массив, если нет — используем пустой массив
    const chatList = Array.isArray(chats) ? chats : [];

    return (
        <motion.div
            className="min-h-screen bg-insta-gray dark:bg-black flex flex-col md:flex-row max-w-4xl mx-auto"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <div className="w-full md:w-1/3 border-r border-gray-200 dark:border-gray-700">
                <div className="p-4 border-b border-gray-200 dark:border-gray-700">
                    <h2 className="text-lg font-semibold text-insta-dark dark:text-white">Messages</h2>
                </div>
                <div className="overflow-y-auto h-[calc(100vh-120px)]">
                    {chatList.length > 0 ? (
                        chatList.map((chat) => (
                            <div
                                key={chat.id}
                                onClick={() => setSelectedChat(chat)}
                                className={`flex items-center p-4 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-800 transition ${
                                    selectedChat?.id === chat.id ? "bg-gray-100 dark:bg-gray-800" : ""
                                }`}
                            >
                                <img
                                    src={chat.recipientProfilePicture || "https://via.placeholder.com/50"}
                                    alt="avatar"
                                    className="w-12 h-12 rounded-full object-cover mr-3"
                                />
                                <div className="flex-1">
                                    <p className="font-semibold text-insta-dark dark:text-white">{chat.recipientUsername}</p>
                                    <p className="text-sm text-gray-500 dark:text-gray-400 truncate">{ chat.lastMessage}</p>
                                </div>
                                <div className="text-xs text-gray-500 dark:text-gray-400">{chat.timestamp}</div>
                                {chat.unreadCount > 0 && (
                                    <div className="ml-2 w-5 h-5 bg-insta-blue text-white rounded-full flex items-center justify-center text-xs">
                                        {chat.unreadCount}
                                    </div>
                                )}
                            </div>
                        ))
                    ) : (
                        <div className="p-4 text-center text-gray-500 dark:text-gray-400">
                            Нет доступных чатов
                        </div>
                    )}
                </div>
            </div>
            <div className="w-full md:w-2/3 flex flex-col">
                {selectedChat ? (
                    <>
                        <div className="p-4 border-b border-gray-200 dark:border-gray-700 flex items-center">
                            <img
                                src={selectedChat.recipientProfilePicture || "https://via.placeholder.com/40"}
                                alt="avatar"
                                className="w-10 h-10 rounded-full object-cover mr-3"
                            />
                            <h2 className="font-semibold text-insta-dark dark:text-white">{selectedChat.recipientUsername}</h2>
                        </div>
                        <div className="flex-1 p-4 overflow-y-auto h-[calc(100vh-180px)]">
                            {messages?.map((msg) => (
                                <div
                                    key={msg.id}
                                    className={`flex ${msg.senderId === selectedChat.senderId ? "justify-end" : "justify-start"} mb-4`}
                                >
                                    <div
                                        className={`max-w-xs p-3 rounded-2xl ${
                                            msg.senderId === selectedChat.senderId
                                                ? "bg-insta-blue text-white"
                                                : "bg-gray-200 dark:bg-gray-700 text-insta-dark dark:text-white"
                                        }`}
                                    >
                                        <p>{msg.text}</p>
                                        <p className="text-xs text-gray-400 mt-1">{new Date(msg.timestamp).toLocaleTimeString()}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                        <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-200 dark:border-gray-700 flex items-center">
                            <input
                                type="text"
                                value={message}
                                onChange={(e) => setMessage(e.target.value)}
                                placeholder="Message..."
                                className="insta-input flex-1 mr-3"
                            />
                            <button type="submit" className="text-insta-blue">
                                <PaperAirplaneIcon className="h-6 w-6" />
                            </button>
                        </form>
                    </>
                ) : (
                    <div className="flex-1 flex items-center justify-center">
                        <p className="text-insta-dark dark:text-gray-300">Select a chat to start messaging</p>
                    </div>
                )}
            </div>
        </motion.div>
    );
};

export default Direct;