import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginApi } from "../api/authApi";

function LoginPage() {
  const navigate = useNavigate();
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const result = await loginApi({ username, password });
      const data = result.data;

      localStorage.setItem("access_token", data.accessToken);
      localStorage.setItem("user_info", JSON.stringify(data));
      navigate("/app");
    } catch (err: unknown) {
      const ax = err as { code?: string; message?: string; response?: { status?: number; data?: { message?: string } } };
      const noResponse = !ax.response;
      const refused =
        ax.code === "ERR_NETWORK" ||
        ax.message === "Network Error" ||
        ax.response?.status === 502 ||
        ax.response?.status === 503;

      if (noResponse || refused) {
        setError(
          "Không kết nối được API. Hãy chạy backend Spring Boot trên cổng 8080 (ví dụ: trong thư mục backend chạy `mvn spring-boot:run` hoặc `./mvnw spring-boot:run`), rồi thử lại."
        );
      } else {
        setError(ax.response?.data?.message || "Đăng nhập thất bại.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-5">
      <div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">Đăng nhập</h2>
        <p className="text-sm text-slate-500 mt-1">Dùng `admin/admin123` hoặc `manager/manager123`.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Username</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Password</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        {error && (
          <div className="p-3 rounded-xl bg-red-50 text-red-700 border border-red-100 text-sm font-semibold">
            {error}
          </div>
        )}

        <button
          type="submit"
          className="w-full bg-primary hover:bg-primary-hover text-white py-3 rounded-xl font-black text-sm shadow-md shadow-blue-600/20 disabled:opacity-60 disabled:cursor-not-allowed"
          disabled={loading}
        >
          {loading ? "Signing in..." : "Login"}
        </button>
      </form>
    </div>
  );
}

export default LoginPage;
