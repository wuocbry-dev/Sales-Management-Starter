import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/useAuth";

type Props = {
  permission: string;
  children: ReactNode;
};

export function RequirePermission({ permission, children }: Props) {
  const { isLoading, user, hasPermission } = useAuth();

  if (isLoading) return null;
  if (!user) return <Navigate to="/" replace />;

  if (!hasPermission(permission)) {
    return (
      <div className="rounded-2xl border border-slate-200 bg-white p-6">
        <div className="text-sm font-black text-slate-900">Không đủ quyền</div>
        <div className="text-sm text-slate-600 mt-1">
          Bạn thiếu permission <span className="font-mono font-semibold">{permission}</span>.
        </div>
      </div>
    );
  }

  return <>{children}</>;
}

