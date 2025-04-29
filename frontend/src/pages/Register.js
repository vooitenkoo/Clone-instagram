import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { register } from "../api";
import { motion } from "framer-motion";

const Register = () => {
    const { register: formRegister, handleSubmit, formState: { errors } } = useForm();
    const navigate = useNavigate();

    const onSubmit = async (data) => {
        try {
            await register(data);
            navigate("/login");
        } catch (err) {
            console.error("Register error:", err);
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
                            {...formRegister("username", { required: "Username is required" })}
                            placeholder="Username"
                            className="insta-input w-full"
                        />
                        {errors.username && (
                            <p className="text-red-500 text-sm mt-1">{errors.username.message}</p>
                        )}
                    </div>
                    <div>
                        <input
                            type="email"
                            {...formRegister("email", { required: "Email is required" })}
                            placeholder="Email"
                            className="insta-input w-full"
                        />
                        {errors.email && (
                            <p className="text-red-500 text-sm mt-1">{errors.email.message}</p>
                        )}
                    </div>
                    <div>
                        <input
                            type="password"
                            {...formRegister("password", { required: "Password is required" })}
                            placeholder="Password"
                            className="insta-input w-full"
                        />
                        {errors.password && (
                            <p className="text-red-500 text-sm mt-1">{errors.password.message}</p>
                        )}
                    </div>
                    <button type="submit" className="insta-button w-full">
                        Sign Up
                    </button>
                </form>
                <p className="text-center text-sm text-insta-dark dark:text-gray-300 mt-4">
                    Already have an account?{" "}
                    <a href="/login" className="text-insta-blue font-semibold hover:underline">
                        Log in
                    </a>
                </p>
            </div>
        </motion.div>
    );
};

export default Register;