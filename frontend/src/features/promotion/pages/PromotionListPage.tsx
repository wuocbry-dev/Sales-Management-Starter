export default function PromotionListPage() {
  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight text-slate-900">Khuyến mãi</h2>
        <p className="text-sm text-slate-500 mt-1">Tạo và quản lý chương trình giảm giá, voucher, combo.</p>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6">
        <div className="flex items-center gap-2 text-slate-600">
          <span className="material-symbols-outlined">sell</span>
          <span className="text-sm font-semibold">Màn này sẽ được dựng theo `doc_fe/khuy_n_m_i_vn_premium_saas`.</span>
        </div>
      </div>
    </div>
  );
}

