import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import useAuthStore from "../store/authStore";
import { motion } from "framer-motion";

const Login = () => {
    const { register, handleSubmit, formState: { errors } } = useForm();
    const { login, error, isLoading } = useAuthStore();
    const navigate = useNavigate();

    const onSubmit = async (data) => {
        try {
            await login(data.username, data.password);
            navigate("/");
        } catch (err) {
            console.error("Login error:", err);
        }
    };

    return (
        <motion.div
            className="min-h-screen flex items-center justify-center bg-insta-gray dark:bg-black px-4"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 0.5 }}
        >
            <div className="insta-card w-full max-w-sm p-6">
                <h1 className="text-3xl font-bold text-center text-insta-dark dark:text-white mb-6">Instagram</h1>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div>
                        <input
                            type="text"
                            {...register("username", { required: "Username is required" })}
                            placeholder="Username"
                            className="insta-input w-full"
                        />
                        {errors.username && (
                            <p className="text-red-500 text-sm mt-1">{errors.username.message}</p>
                        )}
                    </div>
                    <div>
                        <input
                            type="password"
                            {...register("password", { required: "Password is required" })}
                            placeholder="Password"
                            className="insta-input w-full"
                        />
                        {errors.password && (
                            <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>
                        )}
                    </div>
                    {error && <p className="text-red-500 text-sm text-center">{error}</p>}
                    <button
                        type="submit"
                        className="insta-button w-full disabled:opacity-50"
                        disabled={isLoading}
                    >
                        {isLoading ? "Logging in..." : "Log In"}
                    </button>
                </form>
                <p className="text-center text-sm text-insta-dark dark:text-gray-300 mt-4">
                    Don't have an account?{" "}
                    <a href="/register" className="text-insta-blue font-semibold hover:underline">
                        Sign up
                    </a>
                </p>
            </div>
        </motion.div>
    );
};

export default Login;