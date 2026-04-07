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
    <div>
      <div className="page-header">
        <h3>Products</h3>
        <p className="muted">Demo product list connected to Spring Boot.</p>
      </div>

      <div className="grid-2">
        <div className="card">
          <h4>Create product</h4>
          <form onSubmit={handleCreate} className="form-grid">
            <label>
              Code
              <input
                value={form.code}
                onChange={(e) => setForm({ ...form, code: e.target.value })}
              />
            </label>

            <label>
              Name
              <input
                value={form.name}
                onChange={(e) => setForm({ ...form, name: e.target.value })}
              />
            </label>

            <label>
              Category
              <input
                value={form.category}
                onChange={(e) => setForm({ ...form, category: e.target.value })}
              />
            </label>

            <label>
              Price
              <input
                type="number"
                value={form.price}
                onChange={(e) => setForm({ ...form, price: Number(e.target.value) })}
              />
            </label>

            <label>
              Stock
              <input
                type="number"
                value={form.stock}
                onChange={(e) => setForm({ ...form, stock: Number(e.target.value) })}
              />
            </label>

            <button className="btn" type="submit">Create product</button>
          </form>
        </div>

        <div className="card">
          <h4>Product list</h4>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Code</th>
                  <th>Name</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Stock</th>
                </tr>
              </thead>
              <tbody>
                {products.map((product) => (
                  <tr key={product.id}>
                    <td>{product.id}</td>
                    <td>{product.code}</td>
                    <td>{product.name}</td>
                    <td>{product.category}</td>
                    <td>{product.price.toLocaleString("vi-VN")}</td>
                    <td>{product.stock}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductListPage;
