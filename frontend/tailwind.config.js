/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        "insta-blue": "#0095f6",
        "insta-gray": "#fafafa",
        "insta-dark": "#262626",
      },
    },
  },
  plugins: [],
};