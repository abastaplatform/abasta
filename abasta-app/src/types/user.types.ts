export interface User {
  uuid?: string;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'USER';
  companyUuid?: string;
  companyName?: string;
  password?: string;
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

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface UserFormData {
  uuid?: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  role: string;
  isActive: boolean;
  password?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateUserResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  timestamp: string;
}
