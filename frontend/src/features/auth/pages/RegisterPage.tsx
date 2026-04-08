import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerApi } from "../api/authApi";
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
  const { setAccessToken, refreshMe } = useAuth();
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await registerApi(form);
      if (!res.success || !res.data?.accessToken) {
        setError(res.message || "Đăng ký thất bại.");
        return;
      }

      setAccessToken(res.data.accessToken);
      await refreshMe();
      navigate("/app");
    } catch (err: unknown) {
      const ax = err as { response?: { data?: { message?: string } } };
      setError(ax.response?.data?.message || "Đăng ký thất bại.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-5">
      <div>
        <h2 className="text-2xl font-black text-slate-900 tracking-tight">Đăng ký</h2>
        <p className="text-sm text-slate-500 mt-1">Tạo tài khoản chủ cửa hàng và cửa hàng mặc định.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Full name</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={form.fullName}
            onChange={(e) => setForm({ ...form, fullName: e.target.value })}
            autoComplete="name"
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Username</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={form.username}
            onChange={(e) => setForm({ ...form, username: e.target.value })}
            autoComplete="username"
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Password</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            type="password"
            value={form.password}
            onChange={(e) => setForm({ ...form, password: e.target.value })}
            autoComplete="new-password"
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Store name</label>
          <input
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={form.storeName}
            onChange={(e) => setForm({ ...form, storeName: e.target.value })}
          />
        </div>

        <div>
          <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Business type</label>
          <select
            className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 text-sm outline-none focus:ring-2 focus:ring-primary/20"
            value={form.businessType}
            onChange={(e) => setForm({ ...form, businessType: e.target.value })}
          >
            <option value="RETAIL_FASHION">RETAIL_FASHION</option>
            <option value="RETAIL_GENERAL">RETAIL_GENERAL</option>
            <option value="FNB">FNB</option>
            <option value="PHARMACY">PHARMACY</option>
          </select>
        </div>

        {error && (
          <div className="p-3 rounded-xl bg-red-50 text-red-700 border border-red-100 text-sm font-semibold">
            {error}
          </div>
        )}

        <button
          className="w-full bg-primary hover:bg-primary-hover text-white py-3 rounded-xl font-black text-sm shadow-md shadow-blue-600/20 disabled:opacity-60 disabled:cursor-not-allowed"
          type="submit"
          disabled={loading}
        >
          {loading ? "Creating..." : "Create account"}
        </button>
      </form>
    </div>
  );
}

export default RegisterPage;
