import axios from "axios";

/**
 * Mặc định dùng đường dẫn tương đối `/api/v1` để Vite dev proxy (`/api` → backend :8080) hoạt động.
 * Triển khai production khác host: đặt `VITE_API_BASE_URL` (vd. https://api.example.com/api/v1).
 */
const baseURL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, "") || "/api/v1";

export const axiosClient = axios.create({
  baseURL,
  headers: {
    "Content-Type": "application/json"
  }
});

axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("access_token");
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
