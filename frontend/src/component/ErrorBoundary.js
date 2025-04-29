import { Component } from "react";

class ErrorBoundary extends Component {
    state = { hasError: false };

    static getDerivedStateFromError(error) {
        return { hasError: true };
    }

    componentDidCatch(error, errorInfo) {
        console.error("ErrorBoundary caught an error:", error, errorInfo);
    }

    render() {
        if (this.state.hasError) {
            return (
                <div className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black">
                    <p className="text-red-500 text-lg font-semibold">Something went wrong. Please try again.</p>
                </div>
            );
        }
        return this.props.children;
    }
}

export default ErrorBoundary;