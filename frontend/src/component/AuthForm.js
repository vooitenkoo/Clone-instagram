import { useState } from "react";
import { useForm } from "react-hook-form";
import useAuthStore from "../store/authStore";

const AuthForm = () => {
    const { register, handleSubmit, formState: { errors }, reset } = useForm();
    const { login, error } = useAuthStore();
    const [isLogin, setIsLogin] = useState(true);

    const onSubmit = async (data) => {
        if (isLogin) {
            await login(data.username, data.password);
        } else {
            // Регистрация пока не реализована, добавим позже
            console.log("Register:", data);
        }
        reset();
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-lg w-full max-w-sm">
                <h2 className="text-2xl font-bold mb-6 text-center">
                    {isLogin ? "Login" : "Register"}
                </h2>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                    <div>
                        <input
                            type="text"
                            placeholder="Username"
                            {...register("username", { required: "Username is required" })}
                            className="w-full p-2 border rounded-lg"
                        />
                        {errors.username && (
                            <p className="text-red-500 text-sm">{errors.username.message}</p>
                        )}
                    </div>
                    <div>
                        <input
                            type="password"
                            placeholder="Password"
                            {...register("password", { required: "Password is required" })}
                            className="w-full p-2 border rounded-lg"
                        />
                        {errors.password && (
                            <p className="text-red-500 text-sm">{errors.password.message}</p>
                        )}
                    </div>
                    {!isLogin && (
                        <>
                            <div>
                                <input
                                    type="text"
                                    placeholder="Name"
                                    {...register("name", { required: "Name is required" })}
                                    className="w-full p-2 border rounded-lg"
                                />
                                {errors.name && (
                                    <p className="text-red-500 text-sm">{errors.name.message}</p>
                                )}
                            </div>
                            <div>
                                <input
                                    type="email"
                                    placeholder="Email"
                                    {...register("email", { required: "Email is required" })}
                                    className="w-full p-2 border rounded-lg"
                                />
                                {errors.email && (
                                    <p className="text-red-500 text-sm">{errors.email.message}</p>
                                )}
                            </div>
                        </>
                    )}
                    {error && <p className="text-red-500 text-sm">{error}</p>}
                    <button
                        type="submit"
                        className="w-full bg-blue-500 text-white p-2 rounded-lg"
                    >
                        {isLogin ? "Login" : "Register"}
                    </button>
                </form>
                <p className="mt-4 text-center">
                    {isLogin ? "Need an account?" : "Already have an account?"}{" "}
                    <span
                        onClick={() => setIsLogin(!isLogin)}
                        className="text-blue-500 cursor-pointer"
                    >
            {isLogin ? "Register" : "Login"}
          </span>
                </p>
            </div>
        </div>
    );
};

export default AuthForm;