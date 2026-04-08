import { Navigate } from "react-router-dom";
import type { ReactNode } from "react";
import { useAuth } from "../../features/auth/hooks/useAuth";

type Props = {
  children: ReactNode;
};

function ProtectedRoute({ children }: Props) {
  const { accessToken, isLoading } = useAuth();
  const token = accessToken || localStorage.getItem("access_token");

  if (isLoading) return null;
  if (!token) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}

export default ProtectedRoute;
