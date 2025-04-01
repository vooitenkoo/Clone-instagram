import React, { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import axios from "axios";

const Chat = ({ chatId, senderId, recipientId, onMessagesRead }) => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState("");
    const [error, setError] = useState(null);
    const [isConnected, setIsConnected] = useState(false);
    const stompClientRef = useRef(null);

    useEffect(() => {
        if (!chatId || !senderId || !recipientId) {
            console.error("Missing chat parameters!");
            setError("Invalid chat parameters");
            return;
        }

        const token = localStorage.getItem("authToken");
        const socket = new SockJS(`http://localhost:8082/ws?token=${token}`);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            debug: (str) => console.log(str),
            onConnect: () => {
                console.log(`Connected and subscribed to /topic/chat/${chatId}`);
                setIsConnected(true);
                client.subscribe(`/topic/chat/${chatId}`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    setMessages((prev) => {
                        if (!prev.some((msg) => msg.id === receivedMessage.id)) {
                            return [...prev, receivedMessage];
                        }
                        return prev;
                    });
                    onMessagesRead();
                    // Помечаем сообщения как прочитанные
                    axios.post(`http://localhost:8082/messages/${chatId}/read`, {}, {
                        headers: { Authorization: `Bearer ${token}` },
                    }).catch((err) => console.error("Error marking messages as read:", err));
                });
            },
            onStompError: (frame) => {
                console.error("STOMP Error:", frame.headers["message"]);
                setError("WebSocket connection failed");
                setIsConnected(false);
            },
            onWebSocketClose: () => {
                console.log("WebSocket closed");
                setIsConnected(false);
            },
        });

        client.activate();
        stompClientRef.current = client;

        const fetchMessages = async () => {
            try {
                const response = await axios.get(
                    `http://localhost:8082/messages/${senderId}/${recipientId}`,
                    { headers: { Authorization: `Bearer ${token}` } }
                );
                setMessages(response.data);
            } catch (err) {
                console.error("Error fetching messages:", err);
                setError("Failed to load messages");
            }
        };

        fetchMessages();

        return () => {
            client.deactivate();
            console.log("Disconnected WebSocket");
        };
    }, [chatId, senderId, recipientId, onMessagesRead]);

    const sendMessage = () => {
        if (!newMessage.trim() || !isConnected) return;

        const client = stompClientRef.current;
        const message = {
            chatId,
            sender: { id: senderId },
            content: newMessage,
            timestamp: new Date().toISOString(),
        };

        client.publish({
            destination: "/app/chat.send",
            body: JSON.stringify(message),
        });

        setNewMessage("");
    };

    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <div style={{ height: "300px", overflowY: "auto" }}>
                {messages.length > 0 ? (
                    messages.map((msg) => (
                        <div key={msg.id || msg.timestamp}>
                            <strong>{msg.sender.id === senderId ? "You" : msg.sender.username || "Unknown"}:</strong> {msg.content}
                        </div>
                    ))
                ) : (
                    <div>No messages yet.</div>
                )}
            </div>
            <input
                type="text"
                value={newMessage}
                onChange={(e) => setNewMessage(e.target.value)}
                placeholder="Type a message..."
                disabled={!isConnected}
            />
            <button onClick={sendMessage} disabled={!isConnected}>
                Send
            </button>
            {!isConnected && <p>Connecting to server...</p>}
        </div>
    );
};

export default Chat;