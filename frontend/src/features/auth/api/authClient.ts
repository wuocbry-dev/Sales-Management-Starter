import axios from "axios";

function resolveAuthBaseUrl() {
  const configured = import.meta.env.VITE_API_BASE_URL || "/api/v1";
  // Backend supports both `/api/v1/auth/*` and legacy `/api/auth/*`
  const replaced = configured.replace(/\/api\/v1\/?$/i, "/api/v1");
  return replaced;
}

export const authClient = axios.create({
  baseURL: resolveAuthBaseUrl(),
  headers: {
    "Content-Type": "application/json"
  }
});

authClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("access_token");
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

