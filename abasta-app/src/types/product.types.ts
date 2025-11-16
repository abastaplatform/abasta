export interface Product {
  uuid: string;
  name: string;
  category: string;
  description: string;
  price: number;
  volume: string;
  unit: string;
  imageUrl?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
  supplier: {
    uuid: string;
    name: string;
  };
}

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    sort: string;
    totalPages: number;
    totalElements: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
  };
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}
