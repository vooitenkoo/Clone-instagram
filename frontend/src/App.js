import React, { useState, useEffect } from "react";
import AuthForm from "./AuthForm";
import Chat from "./component/Chat";
import "./App.css";
import '@ant-design/v5-patch-for-react-19';

function App() {
  const [token, setToken] = useState(localStorage.getItem("authToken"));
  const [currentUser, setCurrentUser] = useState(null);

  // Функция для парсинга JWT
  const parseJwt = (token) => {
    try {
      console.log("Received token:", token); // ✅ Проверяем, что приходит токен
      const base64Url = token.split(".")[1];
      const base64 = decodeURIComponent(
          atob(base64Url)
              .split("")
              .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
              .join("")
      );
      const parsedData = JSON.parse(base64);
      console.log("Parsed token data:", parsedData); // ✅ Проверяем, что в токене
      return parsedData;
    } catch (e) {
      console.error("Error parsing token:", e);
      return null;
    }
  };


  useEffect(() => {
    if (token) {
      setCurrentUser(parseJwt(token));  // Получаем данные пользователя из токена
    }
  }, [token]);

  const handleAuth = (newToken) => {
    console.log("New Token Received:", newToken);
    const userData = parseJwt(newToken);
    console.log("User Data Extracted:", userData);

    if (userData && userData.sub) {
      localStorage.setItem("username", userData.sub); // ✅ Используем `sub`
    } else {
      console.error("Username not found in token!");
    }

    localStorage.setItem("authToken", newToken);
    setToken(newToken);
    setCurrentUser(userData);
  };


  const handleSignOut = () => {
    localStorage.removeItem("authToken");
    setToken(null);
    setCurrentUser(null);
  };

  return (
      <div className="App">
        {!token ? (
            <AuthForm onAuth={handleAuth} />
        ) : (
            <div>
              <h1>Welcome, {currentUser?.name}!</h1>
              <button onClick={handleSignOut}>Sign Out</button>>
              <div>
                <h1>Test Chat</h1>
                <Chat chatId={1} />  {/* Жестко передаем chatId = 1 */}
              </div>
            </div>
        )}
      </div>
  );
}

export default App;
