function CustomerListPage() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">Khách hàng</h2>
          <p className="text-sm text-slate-500 mt-1">Quản lý và chăm sóc mối quan hệ khách hàng.</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="px-4 py-2 bg-white border border-slate-200 text-slate-700 rounded-lg text-sm font-semibold hover:bg-slate-50 transition-colors flex items-center gap-2">
            <span className="material-symbols-outlined text-lg">upload</span>
            Nhập danh sách
          </button>
          <button className="px-4 py-2 bg-white border border-slate-200 text-slate-700 rounded-lg text-sm font-semibold hover:bg-slate-50 transition-colors flex items-center gap-2">
            <span className="material-symbols-outlined text-lg">download</span>
            Xuất file
          </button>
          <button className="px-4 py-2 bg-primary text-white rounded-lg text-sm font-bold hover:bg-primary-hover transition-colors shadow-md shadow-blue-600/20 flex items-center gap-2">
            <span className="material-symbols-outlined text-lg">add</span>
            Thêm khách hàng
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="bg-white p-5 rounded-lg shadow-sm border border-slate-100">
          <div className="flex items-start justify-between mb-3">
            <div className="p-2 bg-blue-50 text-blue-600 rounded-lg">
              <span className="material-symbols-outlined">groups</span>
            </div>
            <span className="px-2 py-0.5 bg-emerald-50 text-emerald-600 text-[11px] font-black rounded-full">
              +8.2%
            </span>
          </div>
          <p className="text-slate-500 text-xs font-semibold mb-1">Tổng khách hàng</p>
          <h3 className="text-2xl font-black text-slate-900">12.450</h3>
        </div>
        <div className="bg-white p-5 rounded-lg shadow-sm border border-slate-100">
          <div className="flex items-start justify-between mb-3">
            <div className="p-2 bg-teal-50 text-teal-600 rounded-lg">
              <span className="material-symbols-outlined">person_add</span>
            </div>
            <span className="px-2 py-0.5 bg-emerald-50 text-emerald-600 text-[11px] font-black rounded-full">
              +5.1%
            </span>
          </div>
          <p className="text-slate-500 text-xs font-semibold mb-1">Khách mới 30 ngày</p>
          <h3 className="text-2xl font-black text-slate-900">850</h3>
        </div>
        <div className="bg-white p-5 rounded-lg shadow-sm border border-slate-100">
          <div className="flex items-start justify-between mb-3">
            <div className="p-2 bg-amber-50 text-amber-600 rounded-lg">
              <span className="material-symbols-outlined">cached</span>
            </div>
            <span className="px-2 py-0.5 bg-emerald-50 text-emerald-600 text-[11px] font-black rounded-full">
              +2.4%
            </span>
          </div>
          <p className="text-slate-500 text-xs font-semibold mb-1">Tỷ lệ quay lại</p>
          <h3 className="text-2xl font-black text-slate-900">68%</h3>
        </div>
        <div className="bg-white p-5 rounded-lg shadow-sm border border-slate-100">
          <div className="flex items-start justify-between mb-3">
            <div className="p-2 bg-purple-50 text-purple-600 rounded-lg">
              <span className="material-symbols-outlined">diamond</span>
            </div>
            <span className="text-emerald-500 text-[11px] font-black">Tháng này</span>
          </div>
          <p className="text-slate-500 text-xs font-semibold mb-1">Doanh thu khách VIP</p>
          <h3 className="text-2xl font-black text-slate-900">₫428.450.000</h3>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="p-4 border-b border-slate-100 flex flex-wrap gap-4 items-center justify-between">
          <div className="flex flex-wrap gap-3">
            <select className="text-sm border-slate-200 rounded-lg py-1.5 focus:ring-primary/20">
              <option>Tất cả Hạng</option>
              <option>VIP</option>
              <option>Thân thiết</option>
            </select>
            <select className="text-sm border-slate-200 rounded-lg py-1.5 focus:ring-primary/20">
              <option>Tất cả Nguồn</option>
              <option>POS</option>
              <option>Online</option>
            </select>
            <select className="text-sm border-slate-200 rounded-lg py-1.5 focus:ring-primary/20">
              <option>Chi nhánh: Tất cả</option>
              <option>Quận 1</option>
              <option>Quận 3</option>
            </select>
          </div>
          <p className="text-xs text-slate-400 font-semibold italic">Demo table theo `doc_fe/kh_ch_h_ng_vn_premium_saas`</p>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-slate-50 text-[11px] uppercase tracking-wider text-slate-500 font-black">
                <th className="px-6 py-4">Khách hàng</th>
                <th className="px-6 py-4">Số điện thoại</th>
                <th className="px-6 py-4">Hạng</th>
                <th className="px-6 py-4">Điểm</th>
                <th className="px-6 py-4">Tổng chi tiêu</th>
                <th className="px-6 py-4">Đơn gần nhất</th>
                <th className="px-6 py-4">Hoạt động</th>
                <th className="px-6 py-4 text-right"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              <tr className="hover:bg-slate-50 transition-colors">
                <td className="px-6 py-4">
                  <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-700 font-black text-sm">
                      NV
                    </div>
                    <div>
                      <p className="text-sm font-black text-slate-900">Nguyễn Văn Anh</p>
                      <p className="text-[11px] text-slate-500">ID: KH1209</p>
                    </div>
                  </div>
                </td>
                <td className="px-6 py-4 text-sm text-slate-600">0901 234 567</td>
                <td className="px-6 py-4">
                  <span className="px-2 py-1 bg-amber-100 text-amber-700 text-[10px] font-black rounded uppercase">
                    VIP
                  </span>
                </td>
                <td className="px-6 py-4 text-sm font-bold text-slate-700">2.450</td>
                <td className="px-6 py-4 text-sm font-black text-slate-900">₫45.200.000</td>
                <td className="px-6 py-4 text-sm text-slate-600">12/03/2024</td>
                <td className="px-6 py-4">
                  <span className="px-2 py-1 bg-teal-50 text-teal-600 text-[10px] font-black rounded-full">
                    Đặt hàng
                  </span>
                </td>
                <td className="px-6 py-4 text-right">
                  <div className="flex items-center justify-end gap-2">
                    <button className="p-1.5 text-slate-400 hover:text-primary hover:bg-primary/10 rounded">
                      <span className="material-symbols-outlined text-lg">visibility</span>
                    </button>
                    <button className="p-1.5 text-slate-400 hover:text-primary hover:bg-primary/10 rounded">
                      <span className="material-symbols-outlined text-lg">edit</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default CustomerListPage;
