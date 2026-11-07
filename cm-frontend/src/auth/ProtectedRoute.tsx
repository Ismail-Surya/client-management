import type { ReactNode } from "react";
import { useAuth } from "./AuthContext";
import { Navigate, useLocation } from "react-router-dom";

export function ProtectedRoute({ children } : { children: ReactNode }) {

    const { isAuthenticated, loading } = useAuth();
    const location = useLocation();

    if (loading) {
        return <div style = { { padding: 24 } }>Loading ...</div>;
    }

    if (!isAuthenticated) {
        // Preserve the intended destination in state so the login page
        // can redirect back after a successful login

        return <Navigate to = "/login" state = {{ location }} replace />;
    }

    return <>{ children }</>

}