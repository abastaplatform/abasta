import { ApiError } from '../types/error.types';
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
    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        email,
        password,
      });

      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.user));

      return response;
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.statusCode === 401) {
          throw new Error(
            'Correu o contrasenya incorrectes. Torna-ho a provar.'
          );
        }
        if (error.statusCode === 403) {
          throw new Error(
            'Aquest compte està desactivat. Contacta amb suport.'
          );
        }
        throw new Error(
          error.message || 'Error al iniciar sessió. Torna-ho a provar.'
        );
      }
      throw new Error('Error al iniciar sessió. Torna-ho a provar.');
    }
  }

  async register(data: RegisterRequest): Promise<RegisterResponse> {
    try {
      const response = await api.post<RegisterResponse>(
        '/companies/register',
        data
      );
      return response;
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.statusCode === 409) {
          throw new Error(
            'Aquest registre ja existeix. Comprova les dades introduïdes.'
          );
        }
        if (error.statusCode === 400) {
          throw new Error(
            'Correu o contrasenya incorrectes. Torna-ho a provar.'
          );
        }
        throw new Error(error.message);
      }

      throw new Error('Error al registrar-se. Torna-ho a provar.');
    }
  }

  async requestPasswordReset(email: string): Promise<void> {
    try {
      await api.post('/auth/forgot-password', { email });
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.statusCode === 404) {
          throw new Error(
            "No s'ha trobat cap usuari amb aquest correu electrònic."
          );
        }

        throw new Error(error.message);
      }
      throw new Error(
        'Error al sol·licitar el restabliment de contrasenya. Torna-ho a provar.'
      );
    }
  }

  async resetPassword(token: string, newPassword: string): Promise<void> {
    try {
      await api.post('/auth/reset-password', { token, newPassword });
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.statusCode === 400) {
          throw new Error(
            'El token de restabliment és invàlid o ha expirat. Demana un nou enllaç de restabliment.'
          );
        }

        throw new Error(error.message);
      }
      throw new Error('Error al restablir la contrasenya. Torna-ho a provar.');
    }
  }

  async verifyEmail(token: string): Promise<void> {
    try {
      await api.post('/auth/verify-email', { token });
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.statusCode === 400) {
          throw new Error(
            'El token de verificació és invàlid o ha expirat. Demana un nou correu de verificació.'
          );
        }

        throw new Error(error.message);
      }
      throw new Error(
        'Error al verificar el correu electrònic. Torna-ho a provar.'
      );
    }
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
}

export default new AuthService();
