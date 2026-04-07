import { Link, NavLink, Outlet, useNavigate } from "react-router-dom";

function DashboardLayout() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("access_token");
    localStorage.removeItem("user_info");
    navigate("/");
  };

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <Link to="/app" className="sidebar-brand">
          Sales Admin
        </Link>

        <nav className="sidebar-nav">
          <NavLink to="/app" end>Dashboard</NavLink>
          <NavLink to="/app/products">Products</NavLink>
          <NavLink to="/app/customers">Customers</NavLink>
          <NavLink to="/app/orders">Orders</NavLink>
          <NavLink to="/app/reports">Reports</NavLink>
        </nav>
      </aside>

      <div className="content-area">
        <header className="topbar">
          <div>
            <h2>Sales Management Dashboard</h2>
            <p className="muted">Starter UI structure for a KiotViet-style project.</p>
          </div>
          <button className="btn btn-outline" onClick={handleLogout}>
            Logout
          </button>
        </header>

        <main className="page-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default DashboardLayout;
