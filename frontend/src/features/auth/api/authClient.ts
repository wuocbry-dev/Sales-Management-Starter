import axios from "axios";

function resolveAuthBaseUrl() {
  const configured = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api/v1";
  // If project uses /api/v1 for business APIs, auth is /api
  const replaced = configured.replace(/\/api\/v1\/?$/i, "/api");
  return replaced.endsWith("/api") ? replaced : "http://localhost:8080/api";
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

