import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getProducts } from "../api/productApi";
import { getCategories, type Category } from "../../category/api/categoryApi";

type Product = {
  id: number;
  code: string;
  name: string;
  category: string;
  price: number;
  stock: number;
  status: string;
};

function ProductListPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);

  const [activeTab, setActiveTab] = useState<"ALL" | "ACTIVE" | "INACTIVE" | "LOW_STOCK">("ACTIVE");
  const [q, setQ] = useState("");
  const [categoryName, setCategoryName] = useState<string>("");
  const [status, setStatus] = useState<"" | "ACTIVE" | "INACTIVE">("ACTIVE");
  const [stockFilter, setStockFilter] = useState<"" | "LOW" | "OUT">("");
  const [sortKey, setSortKey] = useState<"NEWEST" | "PRICE_DESC">("NEWEST");

  const fetchProducts = async () => {
    const response = await getProducts();
    setProducts(response.data);
  };

  useEffect(() => {
    fetchProducts();
    getCategories()
      .then((res) => {
        if (res.success) setCategories(res.data || []);
      })
      .catch(() => setCategories([]));
  }, []);

  const selected = useMemo(
    () => products.find((p) => p.id === selectedProductId) || null,
    [products, selectedProductId]
  );

  const filteredProducts = useMemo(() => {
    let list = [...products];

    const tabStatus = (() => {
      if (activeTab === "ACTIVE") return "ACTIVE";
      if (activeTab === "INACTIVE") return "INACTIVE";
      return "";
    })();

    const effectiveStatus = status || (tabStatus as "" | "ACTIVE" | "INACTIVE");
    if (effectiveStatus) list = list.filter((p) => (p.status || "").toUpperCase() === effectiveStatus);

    if (activeTab === "LOW_STOCK") list = list.filter((p) => p.stock > 0 && p.stock <= 5);

    if (stockFilter === "OUT") list = list.filter((p) => p.stock === 0);
    if (stockFilter === "LOW") list = list.filter((p) => p.stock > 0 && p.stock <= 5);

    if (categoryName) list = list.filter((p) => (p.category || "").toLowerCase() === categoryName.toLowerCase());

    const query = q.trim().toLowerCase();
    if (query) {
      list = list.filter(
        (p) =>
          (p.name || "").toLowerCase().includes(query) ||
          (p.code || "").toLowerCase().includes(query) ||
          (p.category || "").toLowerCase().includes(query)
      );
    }

    if (sortKey === "PRICE_DESC") list.sort((a, b) => (b.price || 0) - (a.price || 0));
    if (sortKey === "NEWEST") list.sort((a, b) => (b.id || 0) - (a.id || 0));

    return list;
  }, [products, activeTab, status, stockFilter, categoryName, q, sortKey]);

  useEffect(() => {
    if (selectedProductId == null && filteredProducts.length > 0) {
      setSelectedProductId(filteredProducts[0].id);
    }
  }, [filteredProducts, selectedProductId]);

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
          <button
            className="flex items-center gap-2 px-6 py-2 bg-primary hover:bg-primary-hover text-white rounded-lg text-sm font-black shadow-md shadow-blue-600/20 transition-all active:scale-[0.98]"
            onClick={() => navigate("/app/products/new")}
          >
            <span className="material-symbols-outlined text-lg">add</span>
            Thêm sản phẩm
          </button>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
        <div className="px-6 border-b border-slate-100 flex items-center gap-8">
          <button
            className={[
              "py-4 text-sm font-semibold transition-colors border-b-2",
              activeTab === "ALL"
                ? "text-primary border-primary"
                : "text-slate-500 hover:text-primary border-transparent"
            ].join(" ")}
            onClick={() => setActiveTab("ALL")}
          >
            Tất cả
          </button>
          <button
            className={[
              "py-4 text-sm font-semibold transition-colors border-b-2",
              activeTab === "ACTIVE"
                ? "text-primary border-primary"
                : "text-slate-500 hover:text-primary border-transparent"
            ].join(" ")}
            onClick={() => setActiveTab("ACTIVE")}
          >
            Đang bán
          </button>
          <button
            className={[
              "py-4 text-sm font-semibold transition-colors border-b-2",
              activeTab === "INACTIVE"
                ? "text-primary border-primary"
                : "text-slate-500 hover:text-primary border-transparent"
            ].join(" ")}
            onClick={() => setActiveTab("INACTIVE")}
          >
            Ngừng bán
          </button>
          <button
            className={[
              "py-4 text-sm font-semibold transition-colors border-b-2 flex items-center gap-2",
              activeTab === "LOW_STOCK"
                ? "text-primary border-primary"
                : "text-slate-500 hover:text-primary border-transparent"
            ].join(" ")}
            onClick={() => setActiveTab("LOW_STOCK")}
          >
            Sắp hết hàng
            <span className="px-1.5 py-0.5 bg-orange-100 text-orange-600 text-[10px] rounded-full font-bold">
              {products.filter((p) => p.stock > 0 && p.stock <= 5).length}
            </span>
          </button>
        </div>

        <div className="p-4 space-y-4">
          <div className="flex flex-wrap items-center gap-3">
            <div className="relative w-full md:w-64 lg:w-72">
              <span className="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-lg">
                search
              </span>
              <input
                className="w-full bg-slate-50 border border-slate-200 rounded-lg pl-10 pr-12 py-2 text-sm focus:ring-2 focus:ring-primary/20 focus:bg-white transition-all outline-none"
                placeholder="Tìm tên, SKU, mã vạch..."
                value={q}
                onChange={(e) => setQ(e.target.value)}
              />
              <span className="absolute right-3 top-1/2 -translate-y-1/2 text-[10px] font-bold text-slate-400 bg-slate-200 px-1.5 py-0.5 rounded">
                F4
              </span>
            </div>

            <select
              className="bg-slate-50 border border-slate-200 rounded-lg py-2 pl-3 pr-8 text-sm focus:ring-primary/20 text-slate-700"
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
            >
              <option value="">Danh mục (Tất cả)</option>
              {categories.map((c) => (
                <option key={c.id} value={c.name}>
                  {c.name}
                </option>
              ))}
            </select>

            <select className="bg-slate-50 border border-slate-200 rounded-lg py-2 pl-3 pr-8 text-sm focus:ring-primary/20 text-slate-700">
              <option>Thương hiệu (Tất cả)</option>
            </select>

            <select
              className="bg-slate-50 border border-slate-200 rounded-lg py-2 pl-3 pr-8 text-sm focus:ring-primary/20 text-slate-700 font-semibold"
              value={status}
              onChange={(e) => setStatus(e.target.value as "" | "ACTIVE" | "INACTIVE")}
            >
              <option value="">Trạng thái (Tất cả)</option>
              <option value="ACTIVE">Đang bán</option>
              <option value="INACTIVE">Ngừng bán</option>
            </select>

            <select
              className="bg-slate-50 border border-slate-200 rounded-lg py-2 pl-3 pr-8 text-sm focus:ring-primary/20 text-slate-700"
              value={stockFilter}
              onChange={(e) => setStockFilter(e.target.value as "" | "LOW" | "OUT")}
            >
              <option value="">Tồn kho (Tất cả)</option>
              <option value="LOW">Sắp hết (≤ 5)</option>
              <option value="OUT">Hết hàng (0)</option>
            </select>

            <div className="hidden md:block w-px h-8 bg-slate-200"></div>

            <div className="flex items-center gap-2 px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg">
              <span className="text-xs text-slate-400 font-semibold whitespace-nowrap">Sắp xếp:</span>
              <select
                className="bg-transparent border-none p-0 text-sm font-black text-slate-700 focus:ring-0 cursor-pointer outline-none"
                value={sortKey}
                onChange={(e) => setSortKey(e.target.value as "NEWEST" | "PRICE_DESC")}
              >
                <option value="NEWEST">Mới nhất</option>
                <option value="PRICE_DESC">Giá: Cao - Thấp</option>
              </select>
            </div>
          </div>

          <div className="flex items-center gap-2 flex-wrap">
            {status && (
              <div className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold">
                {status === "ACTIVE" ? "Đang bán" : "Ngừng bán"}{" "}
                <button className="material-symbols-outlined text-sm" onClick={() => setStatus("")} type="button">
                  close
                </button>
              </div>
            )}
            {categoryName && (
              <div className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold">
                {categoryName}{" "}
                <button
                  className="material-symbols-outlined text-sm"
                  onClick={() => setCategoryName("")}
                  type="button"
                >
                  close
                </button>
              </div>
            )}
            {stockFilter && (
              <div className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold">
                {stockFilter === "LOW" ? "Sắp hết" : "Hết hàng"}{" "}
                <button
                  className="material-symbols-outlined text-sm"
                  onClick={() => setStockFilter("")}
                  type="button"
                >
                  close
                </button>
              </div>
            )}
            {q && (
              <div className="flex items-center gap-1.5 px-3 py-1 bg-blue-50 text-blue-600 rounded-full text-xs font-semibold">
                Từ khóa: {q}{" "}
                <button className="material-symbols-outlined text-sm" onClick={() => setQ("")} type="button">
                  close
                </button>
              </div>
            )}

            {!status && !categoryName && !stockFilter && !q && (
              <span className="text-xs text-slate-400 font-semibold italic">Chưa áp dụng bộ lọc.</span>
            )}

            <button
              className="text-xs font-black text-red-500 ml-2 hover:underline"
              onClick={() => {
                setQ("");
                setCategoryName("");
                setStatus("");
                setStockFilter("");
                setActiveTab("ALL");
              }}
              type="button"
            >
              Xóa lọc
            </button>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-slate-200 shadow-sm grid grid-cols-[1fr_260px]">
        <div className="overflow-hidden">
          <table className="w-full text-left border-collapse table-fixed">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200">
                <th className="px-2 py-3 w-9">
                  <input className="rounded border-slate-300 text-primary focus:ring-primary" type="checkbox" />
                </th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider">Sản phẩm</th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider w-[100px]">SKU</th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider w-[110px]">Danh mục</th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider text-right w-[90px]">Giá bán</th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider text-center w-[60px]">Tồn kho</th>
                <th className="px-2 py-3 text-xs font-black text-slate-500 uppercase tracking-wider w-[80px]">Trạng thái</th>
                <th className="px-2 py-3 w-9"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredProducts.map((product) => {
                const isSelected = product.id === selectedProductId;
                const badge =
                  (product.status || "").toUpperCase() === "ACTIVE"
                    ? "bg-green-100 text-green-700"
                    : "bg-slate-100 text-slate-600";
                return (
                  <tr
                    key={product.id}
                    className={[
                      "hover:bg-blue-50/30 transition-colors group cursor-pointer",
                      isSelected ? "bg-blue-50/30" : ""
                    ].join(" ")}
                    onClick={() => setSelectedProductId(product.id)}
                  >
                    <td className="px-2 py-3">
                      <input className="rounded border-slate-300 text-primary focus:ring-primary" type="checkbox" />
                    </td>
                    <td className="px-2 py-3">
                      <div className="flex items-center gap-2">
                        <div className="w-9 h-9 shrink-0 rounded-lg bg-slate-100 overflow-hidden border border-slate-200 flex items-center justify-center">
                          <span className="text-[10px] font-black text-slate-500">
                            {(product.name || "P").slice(0, 2).toUpperCase()}
                          </span>
                        </div>
                        <div className="min-w-0">
                          <p className="text-sm font-black text-slate-900 truncate">{product.name}</p>
                          <span className="inline-block mt-0.5 px-1 py-0.5 bg-slate-100 text-[10px] text-slate-500 font-black rounded">
                            ID: {product.id}
                          </span>
                        </div>
                      </div>
                    </td>
                    <td className="px-2 py-3 text-sm font-semibold text-slate-600 truncate">{product.code}</td>
                    <td className="px-2 py-3 text-sm text-slate-600 truncate">{product.category}</td>
                    <td className="px-2 py-3 text-sm font-black text-slate-900 text-right whitespace-nowrap">
                      ₫{(product.price || 0).toLocaleString("vi-VN")}
                    </td>
                    <td className="px-2 py-3 text-center">
                      <span
                        className={[
                          "text-sm font-black",
                          product.stock === 0
                            ? "text-red-600"
                            : product.stock <= 5
                              ? "text-orange-600"
                              : "text-primary"
                        ].join(" ")}
                      >
                        {product.stock}
                      </span>
                    </td>
                    <td className="px-2 py-3">
                      <span className={["px-2 py-1 text-[11px] font-black rounded-full uppercase whitespace-nowrap", badge].join(" ")}>
                        {(product.status || "").toUpperCase() === "ACTIVE" ? "Đang bán" : "Ngừng bán"}
                      </span>
                    </td>
                    <td className="px-2 py-3 text-right">
                      <button className="p-1 hover:bg-white rounded border border-transparent hover:border-slate-200 transition-all">
                        <span className="material-symbols-outlined text-slate-400">more_horiz</span>
                      </button>
                    </td>
                  </tr>
                );
              })}

              {filteredProducts.length === 0 && (
                <tr>
                  <td className="p-10 text-center text-slate-400 font-semibold" colSpan={8}>
                    Không có sản phẩm phù hợp bộ lọc.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="border-l border-slate-200 bg-slate-50/50 p-4 space-y-4">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-black text-slate-900">Chi tiết tồn kho</h3>
            <span className="text-[10px] font-black bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full uppercase">
              {selected ? `Đang xem ${selected.id}` : "Chưa chọn"}
            </span>
          </div>

          {selected ? (
            <div className="space-y-4">
              <div className="bg-white p-3 rounded-lg border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-tighter">Kho hiện tại</span>
                  <span className="text-sm font-black text-slate-900">{selected.stock}</span>
                </div>
                <div className="w-full bg-slate-100 h-1.5 rounded-full overflow-hidden">
                  <div
                    className="bg-primary h-full"
                    style={{ width: `${Math.min(100, Math.max(5, selected.stock * 10))}%` }}
                  ></div>
                </div>
              </div>

              <div className="bg-white p-3 rounded-lg border border-slate-200 shadow-sm">
                <div className="flex justify-between items-center mb-2">
                  <span className="text-xs font-semibold text-slate-500 uppercase tracking-tighter">SKU</span>
                  <span className="text-sm font-black text-slate-900">{selected.code}</span>
                </div>
                <div className="text-xs text-slate-500">
                  Danh mục: <span className="font-semibold text-slate-700">{selected.category}</span>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-sm text-slate-400 font-semibold">Chọn 1 sản phẩm để xem nhanh.</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductListPage;
