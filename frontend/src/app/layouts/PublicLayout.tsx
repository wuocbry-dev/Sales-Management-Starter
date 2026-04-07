import { Link, Outlet } from "react-router-dom";

function PublicLayout() {
  return (
    <div className="public-layout">
      <div className="auth-panel">
        <div className="brand-block">
          <p className="eyebrow">Spring Boot + React Starter</p>
          <h1>Sales Management System</h1>
          <p className="muted">
            Starter structure for POS, inventory, customer, order, and report modules.
          </p>
          <div className="quick-links">
            <Link to="/">Login</Link>
            <Link to="/register">Register</Link>
          </div>
        </div>

        <div className="auth-card">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default PublicLayout;
