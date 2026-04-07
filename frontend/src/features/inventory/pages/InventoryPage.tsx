export default function InventoryPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight text-slate-900">Kho hàng</h2>
        <p className="text-sm text-slate-500 mt-1">Quản lý tồn kho, nhập/xuất và điều chuyển giữa các kho.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <div className="flex items-center gap-2 text-slate-600">
          <span className="material-symbols-outlined">warehouse</span>
          <span className="text-sm font-semibold">Màn này sẽ được dựng theo `doc_fe/kho_h_ng_vn_premium_saas`.</span>
        </div>
      </div>
    </div>
  );
}

