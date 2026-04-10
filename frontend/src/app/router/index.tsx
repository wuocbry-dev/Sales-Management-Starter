import { createBrowserRouter } from "react-router-dom";
import PublicLayout from "../layouts/PublicLayout";
import SystemAdminLayout from "../layouts/SystemAdminLayout";
import ProtectedRoute from "./ProtectedRoute";
import LoginPage from "../pages/LoginPage";
import NotFoundPage from "../pages/NotFoundPage";

export const router = createBrowserRouter(
  [
  {
    path: "/",
    element: <PublicLayout />,
    children: [
      { index: true, element: <LoginPage /> },
      { path: "login", element: <LoginPage /> },
    ]
  },
  {
    path: "/admin",
    element: (
      <ProtectedRoute>
        <SystemAdminLayout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <NotFoundPage /> }
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
