import type { User } from '../types/user.types';
import api from './api';

interface LoginResponse {
  type: string;
  data: {
    token: string;
    user: User;
  };
}

interface RegisterRequest {
  companyName: string;
  taxId: string;
  adminFirstName: string;
  adminLastName: string;
  adminEmail: string;
  adminPassword: string;
}

interface RegisterResponse {
  success: boolean;
  message: string;
  data?: {
    company?: {
      id: number;
      uuid: string;
      name: string;
      taxId: string;
      status: string;
      createdAt: string;
      updatedAt: string;
    };
  };
}

class AuthService {
  async login(email: string, password: string): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/login', {
      email,
      password,
    });

    localStorage.setItem('token', response.data.token);
    localStorage.setItem('user', JSON.stringify(response.data.user));

    return response;
  }

  async register(data: RegisterRequest): Promise<RegisterResponse> {
    const response = await api.post<RegisterResponse>(
      '/companies/register',
      data
    );
    return response;
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getCurrentUser(): User | null {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;

    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  async verifyEmail(token: string): Promise<void> {
    return api.post('/auth/verify-email', { token }).then(() => {});
  }
}

export default new AuthService();
