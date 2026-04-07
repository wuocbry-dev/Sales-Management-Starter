export type AuthUser = {
  userId: number;
  username: string;
  fullName: string | null;
  roleCodes: string[];
  storeId: number | null;
  branchId: number | null;
};

export type AuthState = {
  accessToken: string | null;
  user: AuthUser | null;
  isLoading: boolean;
};

