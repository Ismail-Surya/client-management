import type { ReactNode } from "react";
import { useAuth } from "./AuthContext";
import { Navigate } from "react-router-dom";

export function GroupRoute({
  group,
  children,
}: {
  group: string;
  children: ReactNode;
}) {
  const { isAuthenticated, loading, hasGroup } = useAuth();

  if (loading) {
    return <div style = { { padding: 24 } }>Loading ...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (!hasGroup(group)) {
    return <Navigate to="/clients" replace />;
  }

  return <>{children}</>;
}