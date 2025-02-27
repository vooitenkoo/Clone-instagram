import React, { useState } from "react";
import AuthForm from "./AuthForm";
import "./App.css";

function App() {
  const [token, setToken] = useState(localStorage.getItem("authToken"));

  const handleAuth = (newToken) => {
    localStorage.setItem("authToken", newToken);
    setToken(newToken);
  };

  const handleSignOut = () => {
    localStorage.removeItem("authToken"); // Удаляем токен
    setToken(null); // Обновляем состояние
  };

  return (
      <div className="App">
        {!token ? (
            <AuthForm onAuth={handleAuth} />
        ) : (
            <div>
              <h1>Welcome! You are logged in.</h1>
              <button onClick={handleSignOut}>Sign Out</button>
            </div>
        )}
      </div>
  );
}

export default App;
