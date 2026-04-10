import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <div className="min-h-[60vh] grid place-items-center">
      <div className="max-w-lg w-full rounded-2xl border border-slate-200 bg-white p-8">
        <div className="text-2xl font-black text-slate-900">404</div>
        <div className="text-sm text-slate-600 mt-2">Không tìm thấy trang.</div>
        <div className="mt-6">
          <Link
            to="/admin/users"
            className="inline-flex items-center justify-center px-4 py-2 rounded-xl bg-primary text-white text-sm font-black hover:bg-primary-hover"
          >
            Về trang admin
          </Link>
        </div>
      </div>
    </div>
  );
}

