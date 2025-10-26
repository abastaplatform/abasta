export interface User {
  uuid?: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'USER';
  companyId: string;
  phone?: string;
  isActive?: boolean;
  emailVerified?: boolean;
  lastLogin?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginCredentials {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}
