// import React, { useState } from "react";
//
// const AuthForm = ({ onAuth }) => {
//     const [formType, setFormType] = useState("login");
//     const [name, setName] = useState("");
//     const [email, setEmail] = useState("");
//     const [username, setUsername] = useState("");
//     const [password, setPassword] = useState("");
//     const [error, setError] = useState("");
//
//     const handleSubmit = async (e) => {
//         e.preventDefault();
//         const url = `http://localhost:8082/api/auth/${formType}`;
//         const body =
//             formType === "register"
//                 ? { name, email, username, password }
//                 : { username, password };
//
//         try {
//             const response = await fetch(url, {
//                 method: "POST",
//                 headers: { "Content-Type": "application/json" },
//                 body: JSON.stringify(body),
//             });
//
//             const text = await response.text();
//
//             let data;
//             try {
//                 data = JSON.parse(text);
//             } catch (e) {
//                 console.error("Failed to parse response JSON:", text);
//                 setError("Unexpected server response");
//                 return;
//             }
//
//             if (!response.ok) {
//                 if (data.error?.includes("username")) {
//                     setError("Username is already taken. Try another one.");
//                 } else {
//                     setError("Something went wrong.");
//                 }
//                 return;
//             }
//
//             if (formType === "register") {
//                 setError(""); // очистить ошибку
//                 setFormType("login"); // показываем форму логина
//                 return;
//             }
//
//             if (data.accessToken) {
//                 localStorage.setItem("authToken", data.accessToken);
//                 localStorage.setItem("refreshToken", data.refreshToken);
//                 onAuth(data.accessToken);
//             } else {
//                 setError("No token received");
//             }
//         } catch (error) {
//             console.error("Request error:", error);
//             setError("Connection error");
//         }
//     };
//
//     return (
//         <div className="auth-container">
//             <div className="auth-card">
//                 <h2 className="auth-title">{formType === "login" ? "Login" : "Register"}</h2>
//                 <form onSubmit={handleSubmit} className="auth-form">
//                     {formType === "register" && (
//                         <>
//                             <input
//                                 type="text"
//                                 placeholder="Name"
//                                 value={name}
//                                 onChange={(e) => setName(e.target.value)}
//                                 required
//                                 className="auth-input"
//                             />
//                             <input
//                                 type="email"
//                                 placeholder="Email"
//                                 value={email}
//                                 onChange={(e) => setEmail(e.target.value)}
//                                 required
//                                 className="auth-input"
//                             />
//                         </>
//                     )}
//                     <input
//                         type="text"
//                         placeholder="Username"
//                         value={username}
//                         onChange={(e) => setUsername(e.target.value)}
//                         required
//                         className="auth-input"
//                     />
//                     <input
//                         type="password"
//                         placeholder="Password"
//                         value={password}
//                         onChange={(e) => setPassword(e.target.value)}
//                         required
//                         className="auth-input"
//                     />
//                     {error && <div className="auth-error">{error}</div>}
//                     <button type="submit" className="auth-button">
//                         {formType === "login" ? "Login" : "Register"}
//                     </button>
//                 </form>
//                 <p className="auth-switch">
//                     {formType === "login"
//                         ? "Need an account?"
//                         : "Already have an account?"}{" "}
//                     <span
//                         onClick={() => {
//                             setFormType(formType === "login" ? "register" : "login");
//                             setError(""); // сбрасываем ошибки при переключении
//                         }}
//                         className="auth-link"
//                     >
//                         {formType === "login" ? "Register" : "Login"}
//                     </span>
//                 </p>
//             </div>
//         </div>
//     );
// };
//
// export default AuthForm;
