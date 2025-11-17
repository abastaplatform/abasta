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
export interface BasicSearchParams {
  searchText?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface ProductFormData {
  uuid?: string;
  supplierUuid: string;
  category: string;
  name: string;
  description: string;
  price: string;
  volume: string;
  unit: string;
  imageUrl?: string;
}

export interface ProductApiData {
  supplierUuid: string;
  category?: string;
  name: string;
  description?: string;
  price: number;
  volume?: number;
  unit?: string;
  imageUrl?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export interface CreateProductResponse {
  success: boolean;
  data?: Product;
  message: string;
}

export interface BasicProductSearchParams {
  searchText?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface AdvancedSearchParams {
  name?: string;
  supplierUuid?: string;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  volume?: number;
  unit?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
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

export interface SearchFilters {
  query: string;
  name: string;
  category: string;
  minPrice: number | null;
  maxPrice: number | null;
  supplierUuid: string;
  volume: number | null;
  unit: string;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}
