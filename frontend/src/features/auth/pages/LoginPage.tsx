import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginApi, meApi } from "../api/authApi";
import { useAuth } from "../hooks/useAuth";

function LoginPage() {
  const navigate = useNavigate();
  const { setAccessToken, setUser } = useAuth();
  const [usernameOrEmail, setUsernameOrEmail] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const result = await loginApi({ usernameOrEmail, password });
      const data = result.data;

      setAccessToken(data.accessToken);

      // Fetch profile right after login
      const meRes = await meApi();
      if (meRes.success && meRes.data) {
        setUser(meRes.data);
        localStorage.setItem("user_info", JSON.stringify(meRes.data));
      }

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
      <p className="muted">Use admin/admin123, manager01/manager123, or cashier01/cashier123</p>

      <form onSubmit={handleSubmit} className="form-grid">
        <label>
          Username / Email
          <input value={usernameOrEmail} onChange={(e) => setUsernameOrEmail(e.target.value)} />
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
