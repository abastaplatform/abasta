import api from './api';
import type {
  ApiResponse,
  PaginatedResponse,
  PaginationParams,
  Product,
} from '../types/product.types';

export const productService = {
  getProducts: async (
    params: PaginationParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> => {
    const queryString = new URLSearchParams({
      page: params.page.toString(),
      size: params.size.toString(),
      sortBy: params.sortBy || 'name',
      sortDir: params.sortDir || 'asc',
    }).toString();
    return await api.get<ApiResponse<PaginatedResponse<Product>>>(
      `/products?${queryString}`
    );
  },

  getProductBySupplier: async (
    supplierUuid: string,
    params: PaginationParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> => {
    const queryString = new URLSearchParams({
      page: params.page.toString(),
      size: params.size.toString(),
      sortBy: params.sortBy || 'name',
      sortDir: params.sortDir || 'asc',
    }).toString();
    return await api.get<ApiResponse<PaginatedResponse<Product>>>(
      `/products/search/supplier/${supplierUuid}?${queryString}`
    );
  },
};
