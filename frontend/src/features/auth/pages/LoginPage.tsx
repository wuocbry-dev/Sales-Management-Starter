import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginApi } from "../api/authApi";
import { useAuth } from "../hooks/useAuth";

type AxiosLikeError = {
  code?: string;
  message?: string;
  response?: { status?: number; data?: { message?: string } };
};

function LoginPage() {
  const navigate = useNavigate();
  const { setAccessToken, refreshMe } = useAuth();
  const [usernameOrEmail, setUsernameOrEmail] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await loginApi({ usernameOrEmail, password });
      if (!res.success || !res.data?.accessToken) {
        setError(res.message || "Đăng nhập thất bại.");
        return;
      }

      setAccessToken(res.data.accessToken);
      await refreshMe();
      navigate("/app");
    } catch (err: unknown) {
      const ax = err as AxiosLikeError;
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
        <p className="text-sm text-slate-500 mt-1">
          Dùng <span className="font-semibold">admin/admin123</span>,{" "}
          <span className="font-semibold">manager01/manager123</span>, hoặc{" "}
          <span className="font-semibold">cashier01/cashier123</span>.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">
            Username / Email
          </label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={usernameOrEmail}
            onChange={(e) => setUsernameOrEmail(e.target.value)}
            autoComplete="username"
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Password</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
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
