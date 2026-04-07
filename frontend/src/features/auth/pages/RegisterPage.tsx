import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { meApi, registerApi } from "../api/authApi";
import { useAuth } from "../hooks/useAuth";

const initialForm = {
  fullName: "",
  username: "",
  password: "",
  storeName: "",
  businessType: "RETAIL_FASHION"
};

function RegisterPage() {
  const navigate = useNavigate();
  const { setAccessToken, setUser } = useAuth();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await registerApi(form);
      const data = res.data;
      setAccessToken(data.accessToken);

      const meRes = await meApi();
      if (meRes.success && meRes.data) {
        setUser(meRes.data);
        localStorage.setItem("user_info", JSON.stringify(meRes.data));
      }

      navigate("/app");
    } catch (err: any) {
      setError(err?.response?.data?.message || "Register failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h2>Register</h2>
      <p className="muted">Create an owner account and default store.</p>

      <div className="card">
        <form onSubmit={handleSubmit} className="form-grid">
          <label>
            Full name
            <input
              value={form.fullName}
              onChange={(e) => setForm({ ...form, fullName: e.target.value })}
            />
          </label>

          <label>
            Username
            <input
              value={form.username}
              onChange={(e) => setForm({ ...form, username: e.target.value })}
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
          </label>

          <label>
            Store name
            <input
              value={form.storeName}
              onChange={(e) => setForm({ ...form, storeName: e.target.value })}
            />
          </label>

          <label>
            Business type
            <select
              value={form.businessType}
              onChange={(e) => setForm({ ...form, businessType: e.target.value })}
            >
              <option value="RETAIL_FASHION">RETAIL_FASHION</option>
              <option value="RETAIL_GENERAL">RETAIL_GENERAL</option>
              <option value="FNB">FNB</option>
              <option value="PHARMACY">PHARMACY</option>
            </select>
          </label>

          {error && <div className="alert error">{error}</div>}

          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Creating..." : "Create account"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default RegisterPage;
