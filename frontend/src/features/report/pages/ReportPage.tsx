function ReportPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
        <div>
          <nav className="flex items-center gap-2 text-xs text-slate-500 mb-2 font-semibold">
            <span>Hệ thống</span>
            <span className="material-symbols-outlined text-[10px]">chevron_right</span>
            <span className="text-primary">Báo cáo tổng quan</span>
          </nav>
          <h2 className="text-3xl font-black text-slate-900 tracking-tight">Báo cáo</h2>
          <p className="text-slate-500 mt-1">Phân tích dữ liệu kinh doanh đa kênh theo thời gian thực</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 text-slate-700 text-sm font-semibold rounded-lg hover:bg-slate-50 transition-all shadow-sm">
            <span className="material-symbols-outlined text-lg">settings_suggest</span>
            Tùy biến
          </button>
          <button className="flex items-center gap-2 px-4 py-2.5 bg-white border border-slate-200 text-slate-700 text-sm font-semibold rounded-lg hover:bg-slate-50 transition-all shadow-sm">
            <span className="material-symbols-outlined text-lg">schedule</span>
            Lên lịch gửi
          </button>
          <button className="flex items-center gap-2 px-5 py-2.5 bg-primary text-white text-sm font-black rounded-lg hover:bg-primary-hover transition-all shadow-lg shadow-blue-600/20">
            <span className="material-symbols-outlined text-lg">file_download</span>
            Xuất báo cáo
          </button>
        </div>
      </div>

      <div className="bg-white p-4 rounded-xl shadow-sm border border-slate-200 flex flex-wrap items-center justify-between gap-4">
        <div className="flex flex-wrap items-center gap-3">
          <button className="flex items-center gap-3 pl-4 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm font-semibold hover:border-primary transition-all">
            <span className="material-symbols-outlined text-slate-400">calendar_month</span>
            <span>01/10/2024 - 31/10/2024</span>
            <span className="material-symbols-outlined text-slate-400">expand_more</span>
          </button>
          <button className="flex items-center gap-3 pl-4 pr-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm font-semibold hover:border-primary transition-all">
            <span className="material-symbols-outlined text-slate-400">store</span>
            <span>Tất cả chi nhánh</span>
            <span className="material-symbols-outlined text-slate-400">expand_more</span>
          </button>
          <div className="hidden md:block h-6 w-px bg-slate-200" />
          <label className="flex items-center gap-2 px-2 py-2 text-sm font-semibold text-slate-700">
            <input type="checkbox" defaultChecked className="accent-primary" />
            So sánh với kỳ trước
          </label>
        </div>
        <div className="flex items-center gap-2 text-slate-400 text-xs font-semibold">
          <span className="material-symbols-outlined text-sm">sync</span>
          Cập nhật lúc: 14:30 Today
        </div>
      </div>

      <div className="grid grid-cols-12 gap-6">
        <div className="col-span-12 lg:col-span-8 bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-lg font-black text-slate-900">Doanh thu theo thời gian</h3>
              <p className="text-sm text-slate-500">Tổng doanh thu thực tế so với mục tiêu (demo)</p>
            </div>
          </div>
          <div className="h-64 rounded-lg border border-slate-100 bg-slate-50 flex items-center justify-center text-slate-400 font-semibold">
            Area chart (demo)
          </div>
        </div>

        <div className="col-span-12 lg:col-span-4 flex flex-col gap-6">
          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
            <h3 className="text-sm font-black text-slate-900 mb-4">Doanh thu theo kênh</h3>
            <div className="space-y-4">
              <div className="space-y-1">
                <div className="flex justify-between text-xs font-semibold">
                  <span className="text-slate-600">Shopee</span>
                  <span className="text-slate-900">₫420M</span>
                </div>
                <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                  <div className="bg-orange-500 h-full" style={{ width: "75%" }} />
                </div>
              </div>
              <div className="space-y-1">
                <div className="flex justify-between text-xs font-semibold">
                  <span className="text-slate-600">POS</span>
                  <span className="text-slate-900">₫480M</span>
                </div>
                <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                  <div className="bg-teal-500 h-full" style={{ width: "85%" }} />
                </div>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl border border-slate-200 shadow-sm">
            <h3 className="text-sm font-black text-slate-900 mb-4">Cơ cấu doanh thu</h3>
            <div className="h-28 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-400 font-semibold">
              Donut chart (demo)
            </div>
          </div>
        </div>
      </div>

      <div>
        <h3 className="text-xl font-black text-slate-900 mb-4">Thư viện báo cáo chi tiết</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[
            { icon: "monetization_on", title: "Doanh thu", desc: "Phân tích dòng tiền, doanh số gộp và thực tế." },
            { icon: "trending_up", title: "Lợi nhuận", desc: "Theo dõi biên lợi nhuận, chi phí vận hành." },
            { icon: "shopping_cart", title: "Đơn hàng", desc: "Thống kê trạng thái đơn, tỷ lệ hủy hoàn." },
            { icon: "inventory", title: "Tồn kho", desc: "Giá trị kho hiện tại, dự báo hết hàng." }
          ].map((c) => (
            <div
              key={c.title}
              className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm hover:shadow-md hover:border-blue-200 transition-all group"
            >
              <div className="w-10 h-10 bg-primary/10 text-primary rounded-lg flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <span className="material-symbols-outlined">{c.icon}</span>
              </div>
              <h4 className="font-black text-slate-900 mb-1">{c.title}</h4>
              <p className="text-xs text-slate-500 mb-4">{c.desc}</p>
              <button className="text-primary text-xs font-black flex items-center gap-1 hover:gap-2 transition-all">
                Xem chi tiết <span className="material-symbols-outlined text-sm">arrow_forward</span>
              </button>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default ReportPage;
