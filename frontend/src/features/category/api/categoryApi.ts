import { axiosClient } from "../../../services/api/axiosClient";

export type Category = {
  id: number;
  name: string;
};

export const getCategories = async () => {
  const response = await axiosClient.get("/categories");
  return response.data as {
    success: boolean;
    message: string;
    data: Category[];
  };
};

