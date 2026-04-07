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
    } catch (err: any) {
      setError(err?.response?.data?.message || "Login failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <p className="muted">Use admin/admin123 or manager/manager123</p>

      <form onSubmit={handleSubmit} className="form-grid">
        <label>
          Username
          <input value={username} onChange={(e) => setUsername(e.target.value)} />
        </label>

        <label>
          Password
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </label>

        {error && <div className="alert error">{error}</div>}

        <button type="submit" className="btn" disabled={loading}>
          {loading ? "Signing in..." : "Login"}
        </button>
      </form>
    </div>
  );
}

export default LoginPage;
