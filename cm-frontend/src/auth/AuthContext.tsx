import type { AuthResponse, User } from "../types/types";
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { authStorage } from "./authStorage";
import { api } from "../api/api";
import type { ReactNode } from "react";

interface AuthContextValue {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  hasGroup: (group: string) => boolean;
  login: (authResponse: AuthResponse) => void;
  logout: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  // On mount, restore user from localStorage if tokens are present.
  // (We store only the tokens persistently; the user is fetched on reload
  //  via /auth/refresh - but for now, we also cache the user in localStorage.)

  useEffect(() => {
    const refresh = authStorage.getRefresh();

    if (!refresh) return;

    // If we cached the user, restore it immediately for a fast first paint.

    const cachedUser = localStorage.getItem("cm.user");

    if (cachedUser) {
      try {
        setUser(JSON.parse(cachedUser) as User);
      } catch {
        // corrupted cache - ignore
      }
    }
    // In a follow-up feature, we can add a silent /me call here to
    // verify the tokens are still good.
    setLoading(false);
  }, []);

  const login = useCallback((authResponse: AuthResponse) => {
    authStorage.set(authResponse.accessToken, authResponse.refreshToken);
    localStorage.setItem("cm.user", JSON.stringify(authResponse.user));
    setUser(authResponse.user);
  }, []);

  const logout = useCallback(async () => {
    const refreshToken = authStorage.getRefresh();

    try {
      if (refreshToken) {
        // Call backend to revoke refresh token. If this fails, still clear local state.
        await api.post("/auth/logout", { refreshToken });
      }
    } catch {
      // swallow - logging out locally is more important than informing the server
    } finally {
      authStorage.clear();
      localStorage.removeItem("cm.user");
      setUser(null);
      window.location.href = "/login";
    }
  }, []);

  const hasGroup = useCallback(
    (group: string) => user?.groups.includes(group) ?? false,
    [user],
  );

  const value = useMemo <AuthContextValue> (
    () => ({
        user,
        isAuthenticated: !!user,
        loading,
        hasGroup,
        login,
        logout,
    }),
    [ user, loading, hasGroup, login, logout ]
  );

  return <AuthContext.Provider value = { value }>{ children }</AuthContext.Provider>;

}

export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error ("useAuth must be used within <AuthProvider>");
    }
    return ctx;
}