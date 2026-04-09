import { FormEvent, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createProduct } from "../api/productApi";
import { getCategories, type Category } from "../../category/api/categoryApi";

type FormState = {
  name: string;
  code: string;
  slug: string;
  barcode: string;
  description: string;
  category: string;
  cost: number | "";
  price: number | "";
  stock: number | "";
  status: "ACTIVE" | "INACTIVE" | "DRAFT";
  trackInventory: boolean;
};

function slugify(input: string) {
  return input
    .trim()
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/[^a-z0-9]+/g, "-")
    .replace(/(^-|-$)+/g, "");
}

const initialForm: FormState = {
  name: "",
  code: "",
  slug: "",
  barcode: "",
  description: "",
  category: "",
  cost: "",
  price: "",
  stock: "",
  status: "ACTIVE",
  trackInventory: true
};

export default function ProductCreatePage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<FormState>(initialForm);
  const [loading, setLoading] = useState(false);
  const [categories, setCategories] = useState<Category[]>([]);
  const [categoriesLoading, setCategoriesLoading] = useState(false);
  const [error, setError] = useState("");

  const margin = useMemo(() => {
    if (form.price === "" || form.cost === "") return null;
    if (form.price <= 0) return null;
    const m = ((form.price - form.cost) / form.price) * 100;
    return Number.isFinite(m) ? Math.round(m) : null;
  }, [form.price, form.cost]);

  const handleSubmit = async () => {
    setError("");
    setLoading(true);
    try {
      const res = await createProduct({
        code: form.code.trim(),
        name: form.name.trim(),
        category: form.category.trim(),
        price: Number(form.price || 0),
        stock: Number(form.stock || 0)
      });

      if (!res?.success) {
        setError(res?.message || "Tạo sản phẩm thất bại.");
        return;
      }

      navigate("/app/products");
    } catch (err: unknown) {
      const ax = err as { response?: { data?: { message?: string } } };
      setError(ax.response?.data?.message || "Không thể tạo sản phẩm.");
    } finally {
      setLoading(false);
    }
  };

  const onSaveProduct = (e: FormEvent) => {
    e.preventDefault();
    void handleSubmit();
  };

  useEffect(() => {
    let cancelled = false;
    const run = async () => {
      setCategoriesLoading(true);
      try {
        const res = await getCategories();
        if (!cancelled && res.success) setCategories(res.data || []);
      } finally {
        if (!cancelled) setCategoriesLoading(false);
      }
    };
    run();
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <div className="pt-2 pb-8 max-w-7xl mx-auto">
      <div className="flex flex-col lg:flex-row lg:items-end justify-between gap-6 mb-8">
        <div>
          <nav className="flex items-center gap-2 text-xs text-slate-500 mb-2 font-medium">
            <button className="hover:text-blue-600" type="button" onClick={() => navigate("/app")}>
              Dashboard
            </button>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <button className="hover:text-blue-600" type="button" onClick={() => navigate("/app/products")}>
              Sản phẩm
            </button>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span className="text-slate-900">Thêm sản phẩm</span>
          </nav>
          <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">Thêm sản phẩm</h2>
          <p className="text-slate-500 text-sm mt-1">Tạo một sản phẩm mới cho danh mục hàng hóa của cửa hàng.</p>
        </div>

        <div className="flex items-center gap-3 sticky top-20 bg-surface/50 backdrop-blur-sm lg:relative lg:top-0 py-2 rounded-lg">
          <button
            type="button"
            className="px-4 py-2 text-sm font-semibold text-slate-600 hover:bg-slate-100 rounded-lg transition-all active:scale-95"
            onClick={() => navigate("/app/products")}
          >
            Hủy bỏ
          </button>
          <button
            type="button"
            className="px-4 py-2 text-sm font-semibold text-blue-600 bg-white border border-blue-200 shadow-sm hover:bg-blue-50 rounded-lg transition-all active:scale-95"
            onClick={() => setForm((f) => ({ ...f, status: "DRAFT" }))}
          >
            Lưu bản nháp
          </button>
          <button
            type="submit"
            form="product-create-form"
            className="px-6 py-2 text-sm font-bold text-white bg-blue-600 shadow-lg shadow-blue-200 hover:bg-blue-700 rounded-lg transition-all active:scale-95 disabled:opacity-60"
            disabled={loading}
          >
            Lưu sản phẩm
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <form id="product-create-form" onSubmit={onSaveProduct} className="space-y-6">
            <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-6">
                <span className="material-symbols-outlined text-blue-600">info</span>
                <h3 className="font-bold text-slate-800">Thông tin cơ bản</h3>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="md:col-span-2">
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Tên sản phẩm <span className="text-red-500">*</span>
                  </label>
                  <input
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                    placeholder="Nhập tên sản phẩm"
                    value={form.name}
                    onChange={(e) => {
                      const name = e.target.value;
                      setForm((f) => ({
                        ...f,
                        name,
                        slug: f.slug ? f.slug : slugify(name)
                      }));
                    }}
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Mã SKU <span className="text-red-500">*</span>
                  </label>
                  <div className="flex gap-2">
                    <input
                      className="flex-1 px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                      placeholder="VD: PRD-12345"
                      value={form.code}
                      onChange={(e) => setForm((f) => ({ ...f, code: e.target.value }))}
                    />
                    <button
                      type="button"
                      className="px-3 py-2 text-xs font-bold text-blue-600 bg-blue-50 border border-blue-100 rounded-lg hover:bg-blue-100 transition-colors"
                      onClick={() =>
                        setForm((f) => ({
                          ...f,
                          code: f.code || `PRD-${Math.floor(10000 + Math.random() * 90000)}`
                        }))
                      }
                    >
                      Tạo mã
                    </button>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Đường dẫn (Slug)
                  </label>
                  <input
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm bg-slate-50"
                    placeholder="product-name"
                    value={form.slug}
                    onChange={(e) => setForm((f) => ({ ...f, slug: e.target.value }))}
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Mã vạch (Barcode)
                  </label>
                  <div className="relative">
                    <input
                      className="w-full pl-4 pr-10 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                      placeholder="Quét hoặc nhập mã vạch"
                      value={form.barcode}
                      onChange={(e) => setForm((f) => ({ ...f, barcode: e.target.value }))}
                    />
                    <span className="material-symbols-outlined absolute right-3 top-2.5 text-slate-400">
                      barcode_scanner
                    </span>
                  </div>
                </div>

                <div className="md:col-span-2">
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Mô tả sản phẩm
                  </label>
                  <textarea
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm resize-none"
                    placeholder="Mô tả chi tiết về sản phẩm..."
                    rows={4}
                    value={form.description}
                    onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                  />
                </div>
              </div>
            </section>

            <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-6">
                <span className="material-symbols-outlined text-teal-600">category</span>
                <h3 className="font-bold text-slate-800">Phân loại</h3>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Cửa hàng <span className="text-red-500">*</span>
                  </label>
                  <select
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 bg-slate-50 text-slate-500 text-sm"
                    disabled
                  >
                    <option>Theo tài khoản đăng nhập</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Danh mục <span className="text-red-500">*</span>
                  </label>
                  <select
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm disabled:bg-slate-50"
                    value={form.category}
                    onChange={(e) => setForm((f) => ({ ...f, category: e.target.value }))}
                    disabled={categoriesLoading}
                  >
                    <option value="" disabled>
                      {categoriesLoading ? "Đang tải danh mục..." : "Chọn danh mục"}
                    </option>
                    {categories.map((c) => (
                      <option key={c.id} value={c.name}>
                        {c.name}
                      </option>
                    ))}
                  </select>
                  <p className="text-[11px] text-slate-400 mt-1 italic">
                    Danh mục được lấy từ API `GET /api/v1/categories` theo cửa hàng của tài khoản hiện tại.
                  </p>
                </div>
              </div>
            </section>

            <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-6">
                <span className="material-symbols-outlined text-orange-500">payments</span>
                <h3 className="font-bold text-slate-800">Thiết lập giá</h3>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Giá vốn (Cost)
                  </label>
                  <div className="relative">
                    <span className="absolute left-4 top-2.5 text-slate-400 text-sm">₫</span>
                    <input
                      className="w-full pl-8 pr-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                      placeholder="0"
                      type="number"
                      value={form.cost}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, cost: e.target.value === "" ? "" : Number(e.target.value) }))
                      }
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Giá bán (Selling) <span className="text-red-500">*</span>
                  </label>
                  <div className="relative">
                    <span className="absolute left-4 top-2.5 text-slate-400 text-sm">₫</span>
                    <input
                      className="w-full pl-8 pr-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                      placeholder="0"
                      type="number"
                      value={form.price}
                      onChange={(e) =>
                        setForm((f) => ({ ...f, price: e.target.value === "" ? "" : Number(e.target.value) }))
                      }
                    />
                  </div>
                  <div className="mt-2 flex items-center gap-2">
                    <span className="text-[10px] bg-accent/10 text-accent font-bold px-2 py-0.5 rounded-full">
                      Margin: {margin === null ? "---" : `${margin}%`}
                    </span>
                    <span className="text-[10px] text-slate-400 italic">Dựa trên chênh lệch giá vốn</span>
                  </div>
                </div>
              </div>
            </section>

            <section className="bg-white p-6 rounded-lg border border-slate-200 shadow-sm">
              <div className="flex items-center gap-2 mb-6">
                <span className="material-symbols-outlined text-purple-600">inventory</span>
                <h3 className="font-bold text-slate-800">Tồn kho &amp; Trạng thái</h3>
              </div>

              <div className="space-y-6">
                <div className="flex items-center justify-between p-4 bg-slate-50 rounded-lg">
                  <div>
                    <h4 className="text-sm font-bold text-slate-700">Theo dõi tồn kho</h4>
                    <p className="text-xs text-slate-500">Backend hiện luôn bật `trackInventory=true` khi tạo.</p>
                  </div>
                  <label className="relative inline-flex items-center cursor-pointer">
                    <input
                      className="sr-only peer"
                      type="checkbox"
                      checked={form.trackInventory}
                      onChange={(e) => setForm((f) => ({ ...f, trackInventory: e.target.checked }))}
                    />
                    <div className="w-11 h-6 bg-slate-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
                  </label>
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Tồn kho ban đầu
                  </label>
                  <input
                    className="w-full px-4 py-2.5 rounded-lg border border-slate-200 focus:ring-2 focus:ring-blue-100 focus:border-blue-500 transition-all text-sm"
                    placeholder="0"
                    type="number"
                    value={form.stock}
                    onChange={(e) =>
                      setForm((f) => ({ ...f, stock: e.target.value === "" ? "" : Number(e.target.value) }))
                    }
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">
                    Trạng thái sản phẩm <span className="text-red-500">*</span>
                  </label>
                  <div className="grid grid-cols-3 gap-3">
                    {[
                      { key: "ACTIVE", icon: "check_circle", label: "Đang bán" },
                      { key: "INACTIVE", icon: "pause_circle", label: "Ngừng bán" },
                      { key: "DRAFT", icon: "edit_note", label: "Bản nháp" }
                    ].map((opt) => (
                      <label
                        key={opt.key}
                        className={[
                          "flex flex-col items-center gap-2 p-3 border-2 rounded-lg cursor-pointer transition-all",
                          form.status === (opt.key as FormState["status"])
                            ? "border-blue-600 bg-blue-50"
                            : "border-slate-100 bg-white hover:border-slate-300"
                        ].join(" ")}
                      >
                        <input
                          className="sr-only"
                          name="status"
                          type="radio"
                          checked={form.status === (opt.key as FormState["status"])}
                          onChange={() => setForm((f) => ({ ...f, status: opt.key as FormState["status"] }))}
                        />
                        <span
                          className={[
                            "material-symbols-outlined",
                            form.status === (opt.key as FormState["status"]) ? "text-blue-600" : "text-slate-400"
                          ].join(" ")}
                        >
                          {opt.icon}
                        </span>
                        <span
                          className={[
                            "text-xs font-bold",
                            form.status === (opt.key as FormState["status"]) ? "text-blue-600" : "text-slate-600"
                          ].join(" ")}
                        >
                          {opt.label}
                        </span>
                      </label>
                    ))}
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1 italic">
                    Backend hiện luôn tạo `status=ACTIVE`. Nếu muốn đúng theo UI, mình sẽ mở rộng API sau.
                  </p>
                </div>

                {error && (
                  <div className="p-3 rounded-xl bg-red-50 text-red-700 border border-red-100 text-sm font-semibold">
                    {error}
                  </div>
                )}
              </div>
            </section>
          </form>
        </div>

        <div className="lg:col-span-1">
          <div className="sticky top-24 space-y-6">
            <div className="bg-white rounded-lg border border-slate-200 shadow-xl overflow-hidden">
              <div className="bg-slate-50 px-6 py-4 border-b border-slate-100 flex items-center justify-between">
                <h3 className="font-bold text-slate-700 text-sm">Xem trước</h3>
                <span className="px-2 py-1 bg-accent text-white text-[10px] font-black uppercase rounded">Live</span>
              </div>
              <div className="p-6">
                <div className="w-full aspect-square bg-slate-100 rounded-xl mb-6 flex flex-col items-center justify-center border-2 border-dashed border-slate-200">
                  <span className="material-symbols-outlined text-4xl text-slate-300 mb-2">add_photo_alternate</span>
                  <p className="text-xs font-bold text-slate-400">Tải ảnh sản phẩm</p>
                </div>

                <div className="space-y-4">
                  <div>
                    <div className="flex items-center justify-between mb-1">
                      <span className="text-[10px] font-bold text-blue-600 uppercase tracking-widest">
                        {form.category || "Danh mục"}
                      </span>
                      <span className="text-[10px] font-bold text-slate-400">SKU: {form.code || "---"}</span>
                    </div>
                    <h4 className="text-xl font-extrabold text-slate-800 leading-tight">
                      {form.name || "Tên sản phẩm..."}
                    </h4>
                  </div>

                  <div className="flex items-end justify-between border-t border-slate-100 pt-4">
                    <div>
                      <p className="text-[10px] text-slate-400 font-bold uppercase mb-1">Giá bán dự kiến</p>
                      <p className="text-2xl font-black text-slate-900 leading-none">
                        ₫{form.price === "" ? "0" : Number(form.price).toLocaleString("vi-VN")}
                      </p>
                    </div>
                    <div className="flex flex-col items-end">
                      <span className="flex items-center gap-1 text-xs font-bold text-teal-600">
                        <span className="w-2 h-2 bg-teal-500 rounded-full"></span>
                        Hoạt động
                      </span>
                    </div>
                  </div>

                  <div className="grid grid-cols-2 gap-3 pt-4 border-t border-slate-100">
                    <div className="bg-slate-50 p-2 rounded-lg text-center">
                      <p className="text-[9px] font-bold text-slate-400 uppercase">Tồn kho</p>
                      <p className="text-sm font-bold text-slate-700">{form.stock === "" ? "0" : form.stock}</p>
                    </div>
                    <div className="bg-slate-50 p-2 rounded-lg text-center">
                      <p className="text-[9px] font-bold text-slate-400 uppercase">Lợi nhuận</p>
                      <p className="text-sm font-bold text-teal-600">{margin === null ? "---" : `${margin}%`}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="bg-blue-600/5 p-4 rounded-xl border border-blue-100">
              <div className="flex items-start gap-3">
                <span className="material-symbols-outlined text-blue-600 text-lg">lightbulb</span>
                <div>
                  <p className="text-xs font-bold text-blue-800 mb-1">Mẹo bán hàng</p>
                  <p className="text-[11px] text-blue-600 leading-relaxed">
                    Sản phẩm có mô tả trên 200 từ thường có tỷ lệ chuyển đổi cao hơn 15%.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

