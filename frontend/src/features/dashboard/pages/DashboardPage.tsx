import { useEffect, useState } from "react";
import { getDashboardSummary } from "../api/dashboardApi";

type Summary = {
  totalProducts: number;
  lowStockProducts: number;
  totalCustomers: number;
  todayRevenue: number;
  pendingOrders: number;
};

function DashboardPage() {
  const [summary, setSummary] = useState<Summary | null>(null);

  useEffect(() => {
    getDashboardSummary()
      .then((response) => setSummary(response.data))
      .catch(() => setSummary(null));
  }, []);

  return (
    <div>
      <div className="page-header">
        <h3>Overview</h3>
        <p className="muted">Starter dashboard connected to the Spring Boot API.</p>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <span>Total Products</span>
          <strong>{summary?.totalProducts ?? "--"}</strong>
        </div>
        <div className="stat-card">
          <span>Low Stock</span>
          <strong>{summary?.lowStockProducts ?? "--"}</strong>
        </div>
        <div className="stat-card">
          <span>Total Customers</span>
          <strong>{summary?.totalCustomers ?? "--"}</strong>
        </div>
        <div className="stat-card">
          <span>Today Revenue</span>
          <strong>{summary ? summary.todayRevenue.toLocaleString("vi-VN") + " đ" : "--"}</strong>
        </div>
        <div className="stat-card">
          <span>Pending Orders</span>
          <strong>{summary?.pendingOrders ?? "--"}</strong>
        </div>
      </div>

      <div className="card">
        <h4>Next modules to implement</h4>
        <ul>
          <li>RBAC with real JWT</li>
          <li>Store / branch management</li>
          <li>Customer CRUD</li>
          <li>Inventory transaction logs</li>
          <li>Sales order workflow</li>
          <li>Shipping integration</li>
        </ul>
      </div>
    </div>
  );
}

export default DashboardPage;
