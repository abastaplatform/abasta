export interface ApiErrorResponse {
  message?: string;
  error?: string;
  statusCode?: number;
  details?: unknown;
}

export class ApiError extends Error {
  statusCode: number;
  data?: ApiErrorResponse;

  constructor(message: string, statusCode: number, data?: ApiErrorResponse) {
    super(message);
    this.name = 'ApiError';
    this.statusCode = statusCode;
    this.data = data;
  }
}
