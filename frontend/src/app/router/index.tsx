import { createBrowserRouter } from "react-router-dom";
import PublicLayout from "../layouts/PublicLayout";
import DashboardLayout from "../layouts/DashboardLayout";
import ProtectedRoute from "./ProtectedRoute";
import LoginPage from "../../features/auth/pages/LoginPage";
import RegisterPage from "../../features/auth/pages/RegisterPage";
import DashboardPage from "../../features/dashboard/pages/DashboardPage";
import ProductListPage from "../../features/product/pages/ProductListPage";
import ProductCreatePage from "../../features/product/pages/ProductCreatePage";
import CustomerListPage from "../../features/customer/pages/CustomerListPage";
import SalesOrderListPage from "../../features/sales-order/pages/SalesOrderListPage";
import ReportPage from "../../features/report/pages/ReportPage";
import NotFoundPage from "../../pages/NotFoundPage";
import InventoryPage from "../../features/inventory/pages/InventoryPage";
import EmployeeListPage from "../../features/employee/pages/EmployeeListPage";
import PromotionListPage from "../../features/promotion/pages/PromotionListPage";
import PaymentPage from "../../features/payment/pages/PaymentPage";
import ShippingPage from "../../features/shipping/pages/ShippingPage";
import StoreListPage from "../../features/store/pages/StoreListPage";

export const router = createBrowserRouter(
  [
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
      { path: "products/new", element: <ProductCreatePage /> },
      { path: "customers", element: <CustomerListPage /> },
      { path: "orders", element: <SalesOrderListPage /> },
      { path: "reports", element: <ReportPage /> },
      { path: "inventory", element: <InventoryPage /> },
      { path: "employees", element: <EmployeeListPage /> },
      { path: "promotions", element: <PromotionListPage /> },
      { path: "payments", element: <PaymentPage /> },
      { path: "shipping", element: <ShippingPage /> },
      { path: "stores", element: <StoreListPage /> }
    ]
  },
  {
    path: "*",
    element: <NotFoundPage />
  }
  ],
  {
    future: {
      v7_startTransition: true
    }
  }
);
