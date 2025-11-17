import { useState, useEffect } from 'react';

import authService from '../services/authService';
import type { User } from '../types/user.types';
import type { RegisterCompanyData } from '../types/company.types';
import { AuthContext } from './AuthContext';

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    try {
      const currentUser = authService.getCurrentUser();
      if (currentUser) {
        setUser(currentUser);
      }
    } catch (error) {
      console.error('Error loading user from localStorage:', error);
      authService.logout();
    } finally {
      setIsLoading(false);
    }
  }, []);

  const login = async (email: string, password: string): Promise<void> => {
    const response = await authService.login(email, password);
    setUser(response.data.user);
  };

  useEffect(() => {
    const handleStorageChange = () => {
      const token = localStorage.getItem("token");
      const userlocal = localStorage.getItem("user");

      if (!token || !userlocal) {
        logout();
      }
    };

    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, [user]);

  const register = async (data: RegisterCompanyData): Promise<void> => {
    await authService.register(data);
  };

  const requestPasswordReset = async (email: string): Promise<void> => {
    await authService.requestPasswordReset(email);
  };

  const resetPassword = async (
    token: string,
    newPassword: string
  ): Promise<void> => {
    await authService.resetPassword(token, newPassword);
  };

  const verifyEmail = async (token: string): Promise<void> => {
    await authService.verifyEmail(token);
  };

  const logout = (): void => {
    authService.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        requestPasswordReset,
        resetPassword,
        verifyEmail,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
