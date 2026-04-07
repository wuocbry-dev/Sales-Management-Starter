export default function ShippingPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight text-slate-900">Vận chuyển</h2>
        <p className="text-sm text-slate-500 mt-1">Quản lý đơn giao hàng, đối tác vận chuyển và tracking.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <div className="flex items-center gap-2 text-slate-600">
          <span className="material-symbols-outlined">local_shipping</span>
          <span className="text-sm font-semibold">Màn này sẽ được dựng theo `doc_fe/v_n_chuy_n_vn_premium_saas`.</span>
        </div>
      </div>
    </div>
  );
}

