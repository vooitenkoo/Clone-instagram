import { BrowserRouter } from "react-router-dom";
import AppRoutes from "./routes";
import Navbar from "./component/Navbar";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useAuth } from "./hooks/useAuth";
import ErrorBoundary from "./component/ErrorBoundary";
import Loader from "./component/Loader";
import { useState } from "react";

const queryClient = new QueryClient();

const App = () => {
    const [theme, setTheme] = useState("light");

    const toggleTheme = () => {
        setTheme(theme === "light" ? "dark" : "light");
    };

    return (
        <ErrorBoundary>
            <QueryClientProvider client={queryClient}>
                <BrowserRouter>
                    <div className={theme}>
                        <button
                            onClick={toggleTheme}
                            className="fixed top-4 right-4 p-2 bg-insta-blue text-white rounded-full shadow-lg z-50 transition hover:bg-blue-700"
                        >
                            {theme === "light" ? "Dark" : "Light"}
                        </button>
                        <AppContent />
                    </div>
                </BrowserRouter>
            </QueryClientProvider>
        </ErrorBoundary>
    );
};

const AppContent = () => {
    const { isAuthenticated, isLoading, error } = useAuth();

    if (isLoading) {
        return <Loader />;
    }

    if (error) {
        console.error("App: Auth error:", error);
        return (
            <div className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black">
                <p className="text-red-500 text-lg font-semibold">Error: {error.message}</p>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-insta-gray dark:bg-black">
            <AppRoutes />
            {isAuthenticated && <Navbar />}
        </div>
    );
};

export default App;