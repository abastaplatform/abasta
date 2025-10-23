import { createContext, useState, useContext } from 'react';

interface AuthContextType {
  user: any | null; // TODO define a proper user type
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: any) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: React.ReactNode }) => {
  const [user, setUser] = useState<any | null>(null); // TODO define a proper user type

  const login = async (email: string, password: string) => {
    await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate API call
    setUser({ email }); // Set user data after login
  };

  const register = async (data: any) => {
    await new Promise(resolve => setTimeout(resolve, 1000)); // Simulate API call
    setUser({ email: data.email }); // Set user data after registration
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading: false,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
