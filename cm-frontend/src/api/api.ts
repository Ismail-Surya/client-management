import axios from "axios";
import type { AxiosError, AxiosRequestConfig, InternalAxiosRequestConfig } from "axios";
import { authStorage } from "../auth/authStorage";
import type { AuthResponse } from "../types/types";

const baseURL = import.meta.env.VITE_API_BASE_URL as string;

export const api = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

// ---- Request interceptor: attach token ----

api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = authStorage.getAccess();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ---- Response interceptor: refresh-and-retry ----

// These are endpoints that must NOT trigger a refresh attempt on 401.
// If /auth/refresh itself returns 401, we're done — redirect to login.
const AUTH_ENDPOINTS = ["/auth/login", "/auth/register", "/auth/refresh"];

let refreshPromise: Promise<string> | null = null;

function isAuthEndpoint(url?: string): boolean {
  if (!url) return false;
  return AUTH_ENDPOINTS.some((p) => url.includes(p));
}

function redirectToLogin(): void {
  authStorage.clear();
  if (window.location.pathname !== "/login") {
    window.location.href = "/login";
  }
}

async function performRefresh(): Promise<string> {
  const refreshToken = authStorage.getRefresh();
  if (!refreshToken) {
    throw new Error("No refresh token available");
  }

  // Use a raw axios call so this request does NOT go through the interceptors
  // (prevents infinite loops if the refresh endpoint itself returns 401).
  const response = await axios.post<AuthResponse>(
    `${baseURL}/auth/refresh`,
    { refreshToken },
    { headers: { "Content-Type": "application/json" } }
  );

  const newAccessToken = response.data.accessToken;
  authStorage.setAccess(newAccessToken);
  return newAccessToken;
}

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as (AxiosRequestConfig & { _retry?: boolean }) | undefined;

    if (!originalRequest || error.response?.status !== 401) {
      return Promise.reject(error);
    }

    // Don't attempt refresh for auth endpoints — a 401 there means
    // bad credentials or an expired/revoked refresh token.
    if (isAuthEndpoint(originalRequest.url)) {
      if (originalRequest.url?.includes("/auth/refresh")) {
        redirectToLogin();
      }
      return Promise.reject(error);
    }

    // If we've already retried this exact request, give up.
    if (originalRequest._retry) {
      redirectToLogin();
      return Promise.reject(error);
    }
    originalRequest._retry = true;

    try {
      // If a refresh is already in progress, wait for it.
      // Otherwise, start one and store the promise.
      if (!refreshPromise) {
        refreshPromise = performRefresh().finally(() => {
          refreshPromise = null;
        });
      }
      const newToken = await refreshPromise;

      // Retry the original request with the new token.
      originalRequest.headers = {
        ...(originalRequest.headers ?? {}),
        Authorization: `Bearer ${newToken}`,
      };
      return api(originalRequest);
    } catch (refreshError) {
      redirectToLogin();
      return Promise.reject(refreshError);
    }
  }
);