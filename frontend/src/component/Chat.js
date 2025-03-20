import React, { useState, useEffect, useRef } from "react";
import { Button, message } from "antd";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const Chat = ({ chatId }) => {
    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const [currentUser, setCurrentUser] = useState(null);
    const stompClient = useRef(null);

    useEffect(() => {
        if (!chatId) {
            console.error("❌ chatId is missing!");
            return;
        }

        console.log("✅ chatId:", chatId);

        const token = localStorage.getItem("authToken");
        if (token) {
            const userData = parseJwt(token);
            setCurrentUser(userData);
            connect(token);
        } else {
            message.error("No authentication token found.");
        }

        return () => {
            disconnect();
        };
    }, [chatId]);

    const connect = (token) => {
        const socket = new SockJS("http://localhost:8082/ws");

        stompClient.current = new Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            onConnect: () => {
                console.log("✅ WebSocket connected!");
                subscribe(chatId);
            },
            onWebSocketError: (error) => {
                console.error("❌ WebSocket error:", error);
            },
            onStompError: (frame) => {
                console.error("❌ STOMP error:", frame.body);
            },
            onDisconnect: () => {
                console.log("❌ WebSocket disconnected");
            },
        });

        stompClient.current.activate();
    };

    const disconnect = () => {
        if (stompClient.current) {
            stompClient.current.deactivate();
        }
    };

    const subscribe = (chatId) => {
        if (!stompClient.current) {
            console.error("STOMP client is null, cannot subscribe");
            return;
        }

        console.log(`🔗 Subscribing to chat: /topic/chat/${chatId}`);

        stompClient.current.subscribe(
            `/topic/chat/${chatId}`,
            (msg) => onMessageReceived(msg),
            { id: `sub-${chatId}` }
        );
    };

    const onMessageReceived = (msg) => {
        try {
            const newMessage = JSON.parse(msg.body);
            console.log("📩 Received message:", newMessage);
            setMessages((prevMessages) => [...prevMessages, newMessage]);
            message.info(`New message from ${newMessage.sender.username}`);
        } catch (error) {
            console.error("❌ Error parsing message:", error);
        }
    };

    const sendMessage = () => {
        if (!currentUser || !currentUser.sub) {
            message.error("User not found. Please log in again.");
            return;
        }

        if (stompClient.current && stompClient.current.connected && text.trim() !== "") {
            const messageToSend = {
                sender: {
                    username: currentUser.sub,
                },
                content: text,
                chatId: chatId,
            };

            console.log("📤 Sending message:", messageToSend);

            stompClient.current.publish({
                destination: "/app/chat.send",
                body: JSON.stringify(messageToSend),
            });

            setText("");
        } else {
            message.error("WebSocket not connected yet or message is empty!");
        }
    };

    const parseJwt = (token) => {
        try {
            const base64Url = token.split(".")[1];
            const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
            return JSON.parse(atob(base64));
        } catch (e) {
            console.error("Failed to parse JWT:", e);
            return null;
        }
    };

    return (
        <div>
            <h1>Chat {chatId}</h1>
            <div>
                <ul>
                    {messages.map((msg, index) => (
                        <li key={index}>
                            <strong>{msg.sender.username}: </strong>
                            {msg.content}
                        </li>
                    ))}
                </ul>
            </div>
            <input
                type="text"
                value={text}
                onChange={(e) => setText(e.target.value)}
                placeholder="Type a message..."
            />
            <Button onClick={sendMessage}>Send</Button>
        </div>
    );
};

export default Chat;