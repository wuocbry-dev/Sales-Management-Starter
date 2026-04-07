import { createBrowserRouter } from "react-router-dom";
import PublicLayout from "../layouts/PublicLayout";
import DashboardLayout from "../layouts/DashboardLayout";
import ProtectedRoute from "./ProtectedRoute";
import LoginPage from "../../features/auth/pages/LoginPage";
import RegisterPage from "../../features/auth/pages/RegisterPage";
import DashboardPage from "../../features/dashboard/pages/DashboardPage";
import ProductListPage from "../../features/product/pages/ProductListPage";
import CustomerListPage from "../../features/customer/pages/CustomerListPage";
import SalesOrderListPage from "../../features/sales-order/pages/SalesOrderListPage";
import ReportPage from "../../features/report/pages/ReportPage";
import NotFoundPage from "../../pages/NotFoundPage";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <PublicLayout />,
    children: [
      { index: true, element: <LoginPage /> },
      { path: "login", element: <LoginPage /> },
      { path: "register", element: <RegisterPage /> }
    ]
  },
  {
    path: "/app",
    element: (
      <ProtectedRoute>
        <DashboardLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <DashboardPage /> },
      { path: "products", element: <ProductListPage /> },
      { path: "customers", element: <CustomerListPage /> },
      { path: "orders", element: <SalesOrderListPage /> },
      { path: "reports", element: <ReportPage /> }
    ]
  },
  {
    path: "*",
    element: <NotFoundPage />
  }
]);
