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
    try {
      const response = await authService.login(email, password);
      setUser(response.user);
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      console.error('Login error:', error);
      throw new Error(error.message || 'Correu o contrasenya incorrectes');
    }
  };

  const register = async (data: RegisterCompanyData): Promise<void> => {
    try {
      const response = await authService.register(data);
      setUser(response.user);
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
      console.error('Register error:', error);
      throw new Error(error.message || 'Error al registrar-se');
    }
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
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
