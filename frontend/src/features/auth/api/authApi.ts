import { authClient } from "./authClient";

export type LoginPayload = {
  usernameOrEmail: string;
  password: string;
};

export const loginApi = async (payload: LoginPayload) => {
  const response = await authClient.post("/auth/login", {
    username: payload.usernameOrEmail,
    password: payload.password
  });
  return response.data as {
    success: boolean;
    message: string;
    data: {
      accessToken: string;
      tokenType: string;
      username: string;
      role: string | null;
      fullName: string;
    };
  };
};

export type RegisterPayload = {
  fullName: string;
  username: string;
  password: string;
  storeName: string;
  businessType: string;
};

export const registerApi = async (payload: RegisterPayload) => {
  const response = await authClient.post("/auth/register", payload);
  return response.data as {
    success: boolean;
    message: string;
    data: {
      accessToken: string;
      tokenType: string;
      username: string;
      role: string | null;
      fullName: string;
    };
  };
};

export type MeResponse = {
  userId: number;
  username: string;
  fullName: string | null;
  roleCodes: string[];
  storeId: number | null;
  branchId: number | null;
};

export const meApi = async () => {
  const response = await authClient.get("/auth/me");
  return response.data as {
    success: boolean;
    message: string;
    data: MeResponse | null;
  };
};
