import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

const navClass = ({ isActive }: { isActive: boolean }) =>
  [
    "flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-semibold transition-colors",
    isActive ? "text-primary bg-primary/10" : "text-slate-700 hover:bg-slate-50"
  ].join(" ");

export default function SystemAdminLayout() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();

  return (
    <div className="min-h-screen flex bg-slate-50">
      <aside className="h-screen w-72 border-r border-slate-200 bg-white flex flex-col py-6 sticky top-0">
        <div className="px-6 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-primary rounded-xl flex items-center justify-center text-white">
              <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
                admin_panel_settings
              </span>
            </div>
            <div className="leading-tight">
              <div className="text-lg font-black text-slate-900">SYSTEM_ADMIN</div>
              <div className="text-[11px] tracking-wider uppercase text-slate-500 font-bold">
                Platform Console
              </div>
            </div>
          </div>
        </div>

        <nav className="flex-1 px-3 space-y-1 overflow-y-auto">
          <NavLink to="/admin/users" className={navClass}>
            <span className="material-symbols-outlined">group</span>
            <span>Users</span>
          </NavLink>
          <NavLink to="/admin/roles" className={navClass}>
            <span className="material-symbols-outlined">security</span>
            <span>Roles</span>
          </NavLink>
          <NavLink to="/admin/permissions" className={navClass}>
            <span className="material-symbols-outlined">key</span>
            <span>Permissions</span>
          </NavLink>
          <NavLink to="/admin/audit-logs" className={navClass}>
            <span className="material-symbols-outlined">history</span>
            <span>Audit logs</span>
          </NavLink>
        </nav>

        <div className="px-4 mt-auto space-y-3">
          <div className="rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
            <div className="text-sm font-black leading-tight text-slate-900">
              {user?.fullName || user?.username || "—"}
            </div>
            <div className="text-[11px] text-slate-600 font-semibold mt-0.5">
              {(user?.roleCodes?.[0] || "SYSTEM_ADMIN").toString()}
            </div>
          </div>
          <button
            className="w-full border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 py-2.5 rounded-xl font-semibold text-sm transition-all"
            onClick={() => {
              logout();
              navigate("/");
            }}
          >
            Đăng xuất
          </button>
        </div>
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <header className="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-slate-200 h-16 flex items-center justify-between px-6">
          <div className="text-sm font-black text-slate-900">Quản trị hệ thống</div>
          <div className="flex items-center gap-2">
            <button
              className="px-3 py-2 rounded-xl bg-primary text-white text-sm font-black hover:bg-primary-hover shadow-sm shadow-blue-200"
              onClick={() => navigate("/admin/users/new")}
            >
              Tạo user
            </button>
          </div>
        </header>
        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

