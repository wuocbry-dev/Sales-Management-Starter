/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: "#2F6BFF",
          hover: "#1E52D9",
          light: "#EBF1FF"
        },
        accent: {
          DEFAULT: "#14B8A6",
          light: "#F0FDFA"
        },
        surface: "#F8FAFC"
      },
      borderRadius: {
        DEFAULT: "0.5rem",
        lg: "0.75rem",
        xl: "1rem",
        "2xl": "1.5rem"
      },
      fontFamily: {
        sans: ["Be Vietnam Pro", "ui-sans-serif", "system-ui", "sans-serif"]
      }
    }
  },
  plugins: []
};

