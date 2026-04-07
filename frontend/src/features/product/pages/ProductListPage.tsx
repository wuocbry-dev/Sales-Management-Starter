import { FormEvent, useEffect, useState } from "react";
import { createProduct, getProducts } from "../api/productApi";

type Product = {
  id: number;
  code: string;
  name: string;
  category: string;
  price: number;
  stock: number;
  status: string;
};

const initialForm = {
  code: "",
  name: "",
  category: "",
  price: 0,
  stock: 0
};

function ProductListPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [form, setForm] = useState(initialForm);

  const fetchProducts = async () => {
    const response = await getProducts();
    setProducts(response.data);
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  const handleCreate = async (event: FormEvent) => {
    event.preventDefault();
    await createProduct(form);
    setForm(initialForm);
    fetchProducts();
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight text-slate-900">Sản phẩm</h2>
          <p className="text-sm text-slate-500 mt-1">Quản lý danh mục hàng hóa và tồn kho hệ thống</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="flex items-center gap-2 px-4 py-2 border border-slate-200 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors">
            <span className="material-symbols-outlined text-lg">upload</span>
            Nhập Excel/CSV
          </button>
          <button className="flex items-center gap-2 px-4 py-2 border border-slate-200 rounded-lg text-sm font-semibold text-slate-700 hover:bg-slate-50 transition-colors">
            <span className="material-symbols-outlined text-lg">download</span>
            Xuất file
          </button>
          <button className="flex items-center gap-2 px-6 py-2 bg-primary hover:bg-primary-hover text-white rounded-lg text-sm font-black shadow-md shadow-blue-600/20 transition-all active:scale-[0.98]">
            <span className="material-symbols-outlined text-lg">add</span>
            Thêm sản phẩm
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-1 bg-white rounded-xl border border-slate-200 shadow-sm p-6">
          <h3 className="text-sm font-black text-slate-900 mb-4">Tạo sản phẩm (demo API)</h3>
          <form onSubmit={handleCreate} className="space-y-3">
            <div>
              <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Code</label>
              <input
                className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                value={form.code}
                onChange={(e) => setForm({ ...form, code: e.target.value })}
              />
            </div>
            <div>
              <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Name</label>
              <input
                className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            </div>
            <div>
              <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Category</label>
              <input
                className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
              />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Price</label>
                <input
                  type="number"
                  className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                  value={form.price}
                  onChange={(e) => setForm({ ...form, price: Number(e.target.value) })}
                />
              </div>
              <div>
                <label className="text-xs font-black text-slate-500 uppercase tracking-wider">Stock</label>
                <input
                  type="number"
                  className="mt-1 w-full bg-slate-50 border border-slate-200 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                  value={form.stock}
                  onChange={(e) => setForm({ ...form, stock: Number(e.target.value) })}
                />
              </div>
            </div>
            <button className="w-full bg-primary hover:bg-primary-hover text-white py-2.5 rounded-lg font-black text-sm shadow-md shadow-blue-600/20 transition-all">
              Tạo sản phẩm
            </button>
          </form>
        </div>

        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
          <div className="p-4 border-b border-slate-100 flex flex-wrap gap-3 items-center justify-between">
            <div className="relative w-full md:w-80">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-lg">
                search
              </span>
              <input
                className="w-full bg-slate-50 border border-slate-200 rounded-lg pl-10 pr-4 py-2 text-sm focus:ring-2 focus:ring-primary/20 outline-none"
                placeholder="Tìm tên, SKU, mã vạch..."
              />
            </div>
            <p className="text-xs text-slate-400 font-semibold italic">
              UI theo `doc_fe/s_n_ph_m_vn_premium_saas` (data vẫn từ API demo)
            </p>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse min-w-[760px]">
              <thead>
                <tr className="bg-slate-50 border-b border-slate-200">
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider">ID</th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider">Code</th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider">Tên</th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider">Danh mục</th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider text-right">Giá</th>
                  <th className="p-4 text-xs font-black text-slate-500 uppercase tracking-wider text-center">Tồn</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {products.map((product) => (
                  <tr key={product.id} className="hover:bg-primary/5 transition-colors">
                    <td className="p-4 text-sm font-semibold text-slate-700">{product.id}</td>
                    <td className="p-4 text-sm text-slate-600">{product.code}</td>
                    <td className="p-4 text-sm font-black text-slate-900">{product.name}</td>
                    <td className="p-4 text-sm text-slate-600">{product.category}</td>
                    <td className="p-4 text-sm font-black text-slate-900 text-right">
                      {product.price.toLocaleString("vi-VN")}
                    </td>
                    <td className="p-4 text-center">
                      <span className="text-sm font-black text-primary">{product.stock}</span>
                    </td>
                  </tr>
                ))}
                {products.length === 0 && (
                  <tr>
                    <td className="p-6 text-center text-slate-400 font-semibold" colSpan={6}>
                      Chưa có dữ liệu sản phẩm
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductListPage;
