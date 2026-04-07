import { axiosClient } from "../../../services/api/axiosClient";

export type LoginPayload = {
  username: string;
  password: string;
};

export const loginApi = async (payload: LoginPayload) => {
  const response = await axiosClient.post("/auth/login", payload);
  return response.data;
};
