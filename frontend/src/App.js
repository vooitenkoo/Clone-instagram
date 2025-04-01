import React, { useState, useEffect, useCallback } from "react";
import AuthForm from "./AuthForm";
import Chat from "./component/Chat";
import "./App.css";
import axios from "axios";

function App() {
  const [token, setToken] = useState(localStorage.getItem("authToken"));
  const [currentUser, setCurrentUser] = useState(null);
  const [chats, setChats] = useState([]);
  const [selectedChat, setSelectedChat] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [showChats, setShowChats] = useState(false);

  const parseJwt = (token) => {
    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      return JSON.parse(atob(base64));
    } catch (e) {
      console.error("Error parsing token:", e);
      return null;
    }
  };

  useEffect(() => {
    if (token) fetchData(token);
    else setIsLoading(false);
  }, [token]);

  const fetchData = async (token) => {
    setIsLoading(true);
    try {
      const userData = parseJwt(token);
      if (!userData || !userData.sub) throw new Error("Invalid token");

      const [userResponse, chatsResponse] = await Promise.all([
        fetchCurrentUser(token),
        fetchChats(token),
      ]);
      setCurrentUser(userResponse);
      setChats(chatsResponse);
    } catch (error) {
      console.error("Error fetching data:", error);
      handleSignOut();
    } finally {
      setIsLoading(false);
    }
  };

  const fetchCurrentUser = async (token) => {
    const response = await axios.get("http://localhost:8082/users/me", {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  };

  const fetchChats = async (token) => {
    const response = await axios.get("http://localhost:8082/chats", {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  };

  const updateChats = useCallback(async () => {
    if (!token) return;
    try {
      const chatsResponse = await fetchChats(token);
      setChats(chatsResponse);
    } catch (error) {
      console.error("Error updating chats:", error);
    }
  }, [token]);

  const handleAuth = (newToken) => {
    const userData = parseJwt(newToken);
    if (userData && userData.sub) {
      localStorage.setItem("username", userData.sub);
      localStorage.setItem("authToken", newToken);
      setToken(newToken);
    } else {
      console.error("Invalid token!");
    }
  };

  const handleSignOut = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
    setToken(null);
    setCurrentUser(null);
    setChats([]);
    setSelectedChat(null);
    setShowChats(false);
  };

  const selectChat = async (chat) => {
    if (isLoading || !currentUser?.id) return;

    setSelectedChat(chat);
    try {
      const response = await axios.get(`http://localhost:8082/chats/${chat.id}/users`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const users = response.data;
      const recipient = users.find((user) => user.id !== currentUser.id);
      if (recipient) {
        setSelectedChat({ ...chat, recipientId: recipient.id, username: recipient.username });
      } else {
        setSelectedChat(null);
      }
    } catch (error) {
      console.error("Error fetching chat users:", error);
      setSelectedChat(null);
    }
  };

  return (
      <div className="App">
        {!token ? (
            <AuthForm onAuth={handleAuth} />
        ) : isLoading ? (
            <div>Loading user data...</div>
        ) : (
            <div>
              <h1>Welcome, {currentUser?.username || "User"}!</h1>
              <button onClick={handleSignOut}>Sign Out</button>
              {!showChats && (
                  <button onClick={() => setShowChats(true)}>Open Chats</button>
              )}
              {showChats && (
                  <div>
                    <h2>Your Chats</h2>
                    {chats.length > 0 ? (
                        <ul>
                          {chats.map((chat) => (
                              <li key={chat.id}>
                                Chat with {chat.username || "Unknown"} (Unread: {chat.unreadMessagesCount || 0})
                                <button onClick={() => selectChat(chat)} disabled={isLoading}>
                                  Open Chat
                                </button>
                              </li>
                          ))}
                        </ul>
                    ) : (
                        <div>No chats available.</div>
                    )}
                  </div>
              )}
              {selectedChat && currentUser?.id && selectedChat.recipientId && (
                  <div>
                    <h1>Chat with {selectedChat.username || "Unknown"}</h1>
                    <Chat
                        chatId={selectedChat.id}
                        senderId={currentUser.id}
                        recipientId={selectedChat.recipientId}
                        onMessagesRead={updateChats}
                    />
                  </div>
              )}
            </div>
        )}
      </div>
  );
}

export default App;