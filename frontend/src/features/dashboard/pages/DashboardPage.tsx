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
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-900">Tổng quan hệ thống</h2>
          <p className="text-slate-500 text-sm mt-1">Bảng điều khiển tổng quan theo thiết kế `doc_fe`.</p>
        </div>
        <div className="flex items-center gap-2 overflow-x-auto pb-1">
          <div className="bg-white border border-slate-200 p-1 rounded-lg flex">
            <button className="px-4 py-1.5 text-sm font-semibold rounded-md bg-primary/10 text-primary">
              7 ngày
            </button>
            <button className="px-4 py-1.5 text-sm font-semibold rounded-md text-slate-500 hover:text-slate-700">
              30 ngày
            </button>
            <button className="px-4 py-1.5 text-sm font-semibold rounded-md text-slate-500 hover:text-slate-700">
              90 ngày
            </button>
          </div>
          <button className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-50">
            <span className="material-symbols-outlined text-lg">filter_alt</span>
            Chi nhánh
          </button>
          <button className="flex items-center gap-2 px-4 py-2 bg-white border border-slate-200 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-50">
            <span className="material-symbols-outlined text-lg">file_download</span>
            Xuất báo cáo
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white p-5 rounded-lg border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <div className="p-2 bg-primary/10 text-primary rounded-lg">
              <span className="material-symbols-outlined">payments</span>
            </div>
            <div className="flex items-center gap-1 px-2 py-0.5 bg-emerald-50 text-emerald-600 text-xs font-black rounded-full">
              <span className="material-symbols-outlined text-[14px]">trending_up</span>
              Live
            </div>
          </div>
          <p className="text-slate-500 text-sm font-semibold">Doanh thu hôm nay</p>
          <p className="text-2xl font-black mt-1">
            {summary ? `${summary.todayRevenue.toLocaleString("vi-VN")} ₫` : "--"}
          </p>
        </div>

        <div className="bg-white p-5 rounded-lg border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <span className="material-symbols-outlined">shopping_cart</span>
            </div>
          </div>
          <p className="text-slate-500 text-sm font-semibold">Đơn chờ xử lý</p>
          <p className="text-2xl font-black mt-1">{summary?.pendingOrders ?? "--"}</p>
        </div>

        <div className="bg-white p-5 rounded-lg border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <div className="p-2 bg-teal-50 text-emerald-600 rounded-lg">
              <span className="material-symbols-outlined">group</span>
            </div>
          </div>
          <p className="text-slate-500 text-sm font-semibold">Khách hàng</p>
          <p className="text-2xl font-black mt-1">{summary?.totalCustomers ?? "--"}</p>
        </div>

        <div className="bg-white p-5 rounded-lg border border-slate-100 shadow-sm">
          <div className="flex justify-between items-start mb-4">
            <div className="p-2 bg-orange-50 text-orange-600 rounded-lg">
              <span className="material-symbols-outlined">inventory</span>
            </div>
          </div>
          <p className="text-slate-500 text-sm font-semibold">Sắp hết hàng</p>
          <p className="text-2xl font-black mt-1">
            {summary?.lowStockProducts ?? "--"} <span className="text-sm font-semibold text-slate-400">SP</span>
          </p>
        </div>
      </div>

      <div className="grid grid-cols-12 gap-6">
        <div className="col-span-12 lg:col-span-8 bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-lg font-black">Phân tích doanh thu</h3>
              <p className="text-sm text-slate-500">Placeholder chart theo layout `doc_fe`</p>
            </div>
          </div>
          <div className="h-64 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-400 text-sm font-semibold">
            Chart (demo)
          </div>
        </div>

        <div className="col-span-12 lg:col-span-4 space-y-6">
          <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
            <h3 className="text-lg font-black mb-4">Thao tác nhanh</h3>
            <div className="grid grid-cols-1 gap-3">
              <button className="flex items-center gap-3 w-full p-3 bg-primary text-white rounded-lg font-bold text-sm hover:bg-primary-hover transition-all shadow-md shadow-blue-100">
                <span className="material-symbols-outlined">add_shopping_cart</span>
                Tạo đơn hàng mới
              </button>
              <button className="flex items-center gap-3 w-full p-3 bg-white border border-slate-200 text-slate-700 rounded-lg font-semibold text-sm hover:bg-slate-50 transition-all">
                <span className="material-symbols-outlined text-slate-400">add_box</span>
                Thêm sản phẩm
              </button>
              <button className="flex items-center gap-3 w-full p-3 bg-white border border-slate-200 text-slate-700 rounded-lg font-semibold text-sm hover:bg-slate-50 transition-all">
                <span className="material-symbols-outlined text-slate-400">inventory</span>
                Nhập hàng vào kho
              </button>
            </div>
          </div>

          <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
            <div className="p-4 border-b border-slate-100 flex justify-between items-center">
              <h3 className="font-black text-sm">Thông báo</h3>
              <span className="px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-black rounded-full">
                Demo
              </span>
            </div>
            <div className="p-6 text-center text-slate-400 text-sm font-semibold bg-slate-50/40">
              Chưa có thông báo
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DashboardPage;
