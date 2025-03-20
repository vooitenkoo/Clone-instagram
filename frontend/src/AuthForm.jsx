import React, { useState } from "react";

const AuthForm = ({ onAuth }) => {
    const [formType, setFormType] = useState("login");
    const [name, setName] = useState("");  // Для регистрации
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = `http://localhost:8082/api/auth/${formType}`;
        const body =
            formType === "register"
                ? { name, username, email, password }
                : { username, password };

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body),
            });

            const data = await response.json();

            if (data.token) {
                localStorage.setItem("authToken", data.token);
                onAuth(data.token);
            } else {
                alert(`Error: ${data.message || "No token received"}`);
            }
        } catch (error) {
            console.error("Request error:", error);
            alert("Connection error.");
        }
    };

    return (
        <div className="auth-container">
            <h2>{formType === "login" ? "Login" : "Register"}</h2>
            <form onSubmit={handleSubmit}>
                {formType === "register" && (
                    <input
                        type="text"
                        placeholder="Name"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        required
                    />
                )}
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                />
                {formType === "register" && (
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                )}
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <button type="submit">
                    {formType === "login" ? "Login" : "Register"}
                </button>
            </form>
            <p onClick={() => setFormType(formType === "login" ? "register" : "login")}>
                {formType === "login"
                    ? "Need an account? Register"
                    : "Already have an account? Login"}
            </p>
        </div>
    );
};

export default AuthForm;
