import { createContext, useEffect, useMemo, useState } from "react";
import type { PropsWithChildren } from "react";
import { meApi } from "../api/authApi";
import type { AuthState, AuthUser } from "./authTypes";

type AuthContextValue = AuthState & {
  setAccessToken: (token: string | null) => void;
  setUser: (user: AuthUser | null) => void;
  logout: () => void;
  refreshMe: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: PropsWithChildren) {
  const [accessToken, setAccessTokenState] = useState<string | null>(
    localStorage.getItem("access_token")
  );
  const [user, setUser] = useState<AuthUser | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  const setAccessToken = (token: string | null) => {
    setAccessTokenState(token);
    if (token) localStorage.setItem("access_token", token);
    else localStorage.removeItem("access_token");
  };

  const logout = () => {
    setAccessToken(null);
    setUser(null);
    localStorage.removeItem("user_info");
  };

  const refreshMe = async () => {
    if (!localStorage.getItem("access_token")) return;
    const res = await meApi();
    if (res.success && res.data) {
      setUser(res.data);
    } else {
      logout();
    }
  };

  useEffect(() => {
    const init = async () => {
      try {
        if (accessToken) {
          await refreshMe();
        }
      } finally {
        setIsLoading(false);
      }
    };
    init();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      accessToken,
      user,
      isLoading,
      setAccessToken,
      setUser,
      logout,
      refreshMe
    }),
    [accessToken, user, isLoading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

