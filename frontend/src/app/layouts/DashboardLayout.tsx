import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../../features/auth/hooks/useAuth";

function DashboardLayout() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="min-h-screen flex bg-slate-50">
      <aside className="h-screen w-64 border-r border-slate-200 bg-white flex flex-col py-6 sticky top-0">
        <div className="px-6 mb-8">
          <Link to="/app" className="flex items-center gap-3">
            <div className="w-9 h-9 bg-primary rounded-lg flex items-center justify-center text-white">
              <span className="material-symbols-outlined" style={{ fontVariationSettings: '"FILL" 1' }}>
                analytics
              </span>
            </div>
            <div className="leading-tight">
              <div className="text-lg font-black text-primary">VN Sales Pro</div>
              <div className="text-[10px] uppercase tracking-widest text-slate-400 font-bold">
                Premium Dashboard
              </div>
            </div>
          </Link>
        </div>

        <nav className="flex-1 px-3 space-y-1 overflow-y-auto">
          <NavLink
            to="/app"
            end
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">dashboard</span>
            <span>Tổng quan</span>
          </NavLink>

          <NavLink
            to="/app/orders"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">receipt_long</span>
            <span>Đơn hàng</span>
          </NavLink>

          <NavLink
            to="/app/products"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">inventory_2</span>
            <span>Sản phẩm</span>
          </NavLink>

          <NavLink
            to="/app/inventory"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">warehouse</span>
            <span>Kho hàng</span>
          </NavLink>

          <NavLink
            to="/app/customers"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">group</span>
            <span>Khách hàng</span>
          </NavLink>

          <NavLink
            to="/app/employees"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">badge</span>
            <span>Nhân viên</span>
          </NavLink>

          <NavLink
            to="/app/promotions"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">sell</span>
            <span>Khuyến mãi</span>
          </NavLink>

          <NavLink
            to="/app/payments"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">payments</span>
            <span>Thanh toán</span>
          </NavLink>

          <NavLink
            to="/app/shipping"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">local_shipping</span>
            <span>Vận chuyển</span>
          </NavLink>

          <NavLink
            to="/app/reports"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">analytics</span>
            <span>Báo cáo</span>
          </NavLink>

          <NavLink
            to="/app/stores"
            className={({ isActive }) =>
              [
                "flex items-center gap-3 px-4 py-3 rounded-lg text-sm font-semibold transition-colors",
                isActive ? "text-primary bg-primary/10" : "text-slate-600 hover:bg-slate-50"
              ].join(" ")
            }
          >
            <span className="material-symbols-outlined">storefront</span>
            <span>Cửa hàng</span>
          </NavLink>
        </nav>

        <div className="px-4 mt-auto space-y-3">
          <button className="w-full bg-primary hover:bg-primary-hover text-white py-2.5 rounded-lg font-bold text-sm transition-all shadow-md shadow-blue-200">
            Tạo đơn
          </button>
          <button
            className="w-full border border-slate-200 bg-white hover:bg-slate-50 text-slate-700 py-2.5 rounded-lg font-semibold text-sm transition-all"
            onClick={handleLogout}
          >
            Đăng xuất
          </button>
        </div>
      </aside>

      <div className="flex-1 min-w-0 flex flex-col">
        <header className="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-slate-200 shadow-sm h-16 flex items-center justify-between px-6">
          <div className="flex items-center flex-1 max-w-xl">
            <div className="relative w-full">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
                search
              </span>
              <input
                className="w-full pl-10 pr-4 py-2 bg-slate-100 border-none rounded-lg text-sm focus:ring-2 focus:ring-primary/20 focus:bg-white transition-all outline-none"
                placeholder="Tìm kiếm đơn hàng, khách hàng, sản phẩm..."
                type="text"
              />
            </div>
          </div>

          <div className="flex items-center gap-4">
            <div className="hidden md:flex items-center gap-1 bg-slate-100 px-3 py-1.5 rounded-lg text-slate-600 text-sm font-semibold">
              <span className="material-symbols-outlined text-sm">location_on</span>
              Quận 1
            </div>
            <button className="p-2 text-slate-500 hover:bg-slate-100 rounded-lg transition-colors relative">
              <span className="material-symbols-outlined">notifications</span>
              <span className="absolute top-2 right-2 w-2 h-2 bg-red-500 border-2 border-white rounded-full"></span>
            </button>
            <div className="h-8 w-px bg-slate-200 mx-1" />
            <div className="flex items-center gap-3">
              <div className="text-right hidden sm:block">
                <p className="text-sm font-bold leading-tight">{user?.fullName || user?.username || "—"}</p>
                <p className="text-[11px] text-slate-500 font-semibold">
                  {(user?.roleCodes?.[0] || "USER").toString()}
                </p>
              </div>
              <div className="w-9 h-9 rounded-full bg-slate-200 border border-white shadow-sm" />
            </div>
          </div>
        </header>

        <main className="p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
