import { Routes, Route, Navigate } from "react-router-dom";
import Feed from "./pages/Feed";
import Profile from "./pages/Profile";
import Direct from "./pages/Direct";
import Search from "./pages/Search";
import Login from "./pages/Login";
import Register from "./pages/Register";
import PostCreate from "./pages/PostCreate";
import Notifications from "./pages/Notifications";
import EditProfile from "./pages/EditProfile";
import { useAuth } from "./hooks/useAuth";

const PrivateRoute = ({ children }) => {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? children : <Navigate to="/login" />;
};

const PublicRoute = ({ children }) => {
    const { isAuthenticated } = useAuth();
    return isAuthenticated ? <Navigate to="/" /> : children;
};

const AppRoutes = () => (
    <Routes>
        <Route
            path="/"
            element={
                <PrivateRoute>
                    <Feed />
                </PrivateRoute>
            }
        />
        <Route
            path="/profile/:username?"
            element={
                <PrivateRoute>
                    <Profile />
                </PrivateRoute>
            }
        />
        <Route
            path="/direct"
            element={
                <PrivateRoute>
                    <Direct />
                </PrivateRoute>
            }
        />
        <Route
            path="/search"
            element={
                <PrivateRoute>
                    <Search />
                </PrivateRoute>
            }
        />
        <Route
            path="/post"
            element={
                <PrivateRoute>
                    <PostCreate />
                </PrivateRoute>
            }
        />
        <Route
            path="/notifications"
            element={
                <PrivateRoute>
                    <Notifications />
                </PrivateRoute>
            }
        />
        <Route
            path="/edit-profile"
            element={
                <PrivateRoute>
                    <EditProfile />
                </PrivateRoute>
            }
        />
        <Route
            path="/login"
            element={
                <PublicRoute>
                    <Login />
                </PublicRoute>
            }
        />
        <Route
            path="/register"
            element={
                <PublicRoute>
                    <Register />
                </PublicRoute>
            }
        />
        <Route path="*" element={<Navigate to="/" />} />
    </Routes>
);

export default AppRoutes;