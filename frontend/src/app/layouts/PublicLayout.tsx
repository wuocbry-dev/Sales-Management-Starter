import { Link, Outlet } from "react-router-dom";

function PublicLayout() {
  return (
    <div className="min-h-screen grid place-items-center px-6 py-10 bg-gradient-to-br from-slate-950 to-slate-800">
      <div className="w-full max-w-5xl bg-white rounded-3xl p-6 shadow-2xl shadow-black/30">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div className="p-6 bg-slate-50 rounded-2xl border border-slate-100">
            <p className="text-xs font-black uppercase tracking-widest text-primary">Spring Boot + React Starter</p>
            <h1 className="text-3xl font-black text-slate-900 mt-2">Sales Management System</h1>
            <p className="text-sm text-slate-500 mt-2">
              Starter structure cho POS, kho, khách hàng, đơn hàng và báo cáo.
            </p>

            <div className="flex gap-2 mt-5">
              <Link
                to="/"
                className="px-4 py-2 rounded-xl bg-white border border-slate-200 text-sm font-bold text-slate-700 hover:bg-slate-50"
              >
                Login
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 rounded-xl bg-primary text-white text-sm font-black hover:bg-primary-hover shadow-md shadow-blue-600/20"
              >
                Register
              </Link>
            </div>
          </div>

          <div className="p-6 rounded-2xl border border-slate-200 bg-white">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  );
}

export default PublicLayout;
