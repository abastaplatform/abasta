export interface Supplier {
  uuid: string;
  companyUuid?: string;
  companyName?: string;
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address?: string;
  notes?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface SupplierFormData {
  uuid?: string;
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  zipCode: string;
  notes?: string;
  companyUuid?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SupplierFormErrors {
  name?: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
}

export interface SupplierApiData {
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address: string;
  notes?: string;
}

export interface CreateSupplierResponse {
  success: boolean;
  data?: Supplier;
  message: string;
}

export interface BasicSearchParams {
  searchText?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface AdvancedSearchParams {
  name?: string;
  contactName?: string;
  email?: string;
  phone?: string;
  address?: string;
  isActive?: boolean;
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
  contactName: string;
  email: string;
  phone: string;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface CachedSuppliersResult {
  suppliers: Supplier[];
  hasMore: boolean;
}
