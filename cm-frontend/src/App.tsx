import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import { ProtectedRoute } from "./auth/ProtectedRoute";
import { GroupRoute } from "./auth/GroupRoute";

import { LoginPage } from "./pages/LoginPage";
import { RegisterPage } from "./pages/RegisterPage";
import { ClientListPage } from "./pages/ClientListPage";
import { ClientDetailsPage } from "./pages/ClientDetailsPage";
import { ClientCreatePage } from "./pages/ClientCreatePage";
import { ClientUpdatePage } from "./pages/ClientUpdatePage";
import { ClientDeletePage } from "./pages/ClientDeletePage";
import { NotFoundPage } from "./pages/NotFoundPage";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected: any authenticated user */}
          <Route
            path="/clients"
            element={
              <ProtectedRoute>
                <ClientListPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/clients/:id"
            element={
              <ProtectedRoute>
                <ClientDetailsPage />
              </ProtectedRoute>
            }
          />

          {/* Protected + gated by group */}
          <Route
            path="/clients/create"
            element={
              <GroupRoute group="create">
                <ClientCreatePage />
              </GroupRoute>
            }
          />
          <Route
            path="/clients/:id/edit"
            element={
              <GroupRoute group="update">
                <ClientUpdatePage />
              </GroupRoute>
            }
          />
          <Route
            path="/clients/:id/delete"
            element={
              <GroupRoute group="delete">
                <ClientDeletePage />
              </GroupRoute>
            }
          />

          {/* Defaults */}
          <Route path="/" element={<Navigate to="/clients" replace />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}