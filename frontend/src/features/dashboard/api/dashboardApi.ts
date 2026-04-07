import { axiosClient } from "../../../services/api/axiosClient";

export const getDashboardSummary = async () => {
  const response = await axiosClient.get("/dashboard/summary");
  return response.data;
};
