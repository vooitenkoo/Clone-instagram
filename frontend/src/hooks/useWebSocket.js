import { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const useWebSocket = (chatId, onMessageReceived) => {
    const stompClientRef = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        // Логируем chatId для отладки
        console.log("useWebSocket chatId:", chatId);

        // Проверяем, что chatId существует и не undefined/null
        if (!chatId || chatId === undefined || chatId === null) {
            // Если chatId не определён, отключаем предыдущее соединение (если оно было)
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
                setIsConnected(false);
            }
            return;
        }

        const token = localStorage.getItem("authToken");
        const socket = new SockJS(`http://localhost:8082/ws?token=${token}`);
        const client = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            onConnect: () => {
                console.log("WebSocket connected for chatId:", chatId);
                setIsConnected(true);
                client.subscribe(`/topic/chat/${chatId}`, (message) => {
                    console.log("Received message:", message.body);
                    const receivedMessage = JSON.parse(message.body);
                    onMessageReceived(receivedMessage);
                });
            },
            onStompError: (frame) => {
                console.error("STOMP Error:", frame.headers["message"]);
                setIsConnected(false);
            },
            onWebSocketClose: () => {
                console.log("WebSocket closed");
                setIsConnected(false);
            },
        });

        client.activate();
        stompClientRef.current = client;

        return () => {
            client.deactivate();
            setIsConnected(false);
        };
    }, [chatId, onMessageReceived]);

    const sendMessage = (message) => {
        if (stompClientRef.current?.connected) {
            stompClientRef.current.publish({
                destination: "/app/chat.send",
                body: JSON.stringify(message),
            });
        } else {
            console.warn("Cannot send message: WebSocket is not connected");
        }
    };

    return { sendMessage, isConnected };
};

export default useWebSocket;