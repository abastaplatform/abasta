import env from '../config/env';
import { ApiError, type ApiErrorResponse } from '../types/error.types';

class ApiService {
  private baseUrl: string;
  private defaultHeaders: HeadersInit;

  constructor() {
    this.baseUrl = env.apiUrl;
    this.defaultHeaders = {
      'Content-Type': 'application/json',
    };
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    const token = localStorage.getItem('token');

    const headers = {
      ...this.defaultHeaders,
      ...options.headers,
      ...(token && { Authorization: `Bearer ${token}` }),
    };

    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        ...options,
        headers,
      });

      if (response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
        throw new ApiError('Sessió expirada. Torna a iniciar sessió.', 401);
      }

      if (!response.ok) {
        const errorData: ApiErrorResponse = await response
          .json()
          .catch(() => ({}));

        const errorMessage =
          errorData.message ||
          errorData.error ||
          this.getGenericErrorMessage(response.status);

        throw new ApiError(errorMessage, response.status, errorData);
      }

      return response.json();
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }

      if (error instanceof TypeError) {
        throw new ApiError(
          'No es pot connectar amb el servidor. Comprova la teva connexió a internet.',
          0
        );
      }

      throw new ApiError(
        "S'ha produït un error inesperat. Torna-ho a provar més tard.",
        500
      );
    }
  }

  private getGenericErrorMessage(status: number): string {
    switch (status) {
      case 400:
        return 'Dades no vàlides. Comprova els camps del formulari.';
      case 403:
        return 'No tens permís per realitzar aquesta acció.';
      case 404:
        return 'El recurs sol·licitat no existeix.';
      case 422:
        return 'Dades de validació incorrectes.';
      case 500:
        return 'Error del servidor. Torna-ho a provar més tard.';
      case 503:
        return 'El servei no està disponible temporalment.';
      default:
        return `Error inesperat (${status}). Torna-ho a provar.`;
    }
  }

  get<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint);
  }

  post<T>(endpoint: string, data: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  put<T>(endpoint: string, data: unknown): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  patch<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'PATCH',
    });
  }

  delete<T>(endpoint: string): Promise<T> {
    return this.request<T>(endpoint, {
      method: 'DELETE',
    });
  }
  upload<T>(endpoint: string, formData: FormData): Promise<T> {
  return this.request<T>(endpoint, {
    method: 'POST',
    body: formData,
    headers: {}
  });
}
async getBlob(endpoint: string): Promise<Blob> {
    const token = localStorage.getItem('token');

    const headers: HeadersInit = {
      ...this.defaultHeaders,
      ...(token && { Authorization: `Bearer ${token}` }),
    };

    try {
      const response = await fetch(`${this.baseUrl}${endpoint}`, {
        method: 'GET',
        headers,
      });

      if (response.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
        throw new ApiError('Sessió expirada. Torna a iniciar sessió.', 401);
      }

      if (!response.ok) {
        const errorData: ApiErrorResponse = await response
          .json()
          .catch(() => ({} as ApiErrorResponse));

        const errorMessage =
          errorData.message ||
          errorData.error ||
          this.getGenericErrorMessage(response.status);

        throw new ApiError(errorMessage, response.status, errorData);
      }

      return await response.blob();
    } catch (error) {
      if (error instanceof ApiError) {
        throw error;
      }

      if (error instanceof TypeError) {
        throw new ApiError(
          'No es pot connectar amb el servidor. Comprova la teva connexió a internet.',
          0
        );
      }

      throw new ApiError(
        "S'ha produït un error inesperat. Torna-ho a provar més tard.",
        500
      );
    }
  }
}

export default new ApiService();
