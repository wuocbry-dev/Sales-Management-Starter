import { axiosClient } from "../../../services/api/axiosClient";

export const getProducts = async () => {
  const response = await axiosClient.get("/products");
  return response.data;
};

export const createProduct = async (payload: {
  code: string;
  name: string;
  category: string;
  price: number;
  stock: number;
}) => {
  const response = await axiosClient.post("/products", payload);
  return response.data;
};
