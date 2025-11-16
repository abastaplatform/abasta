import api from './api';
import type {
  AdvancedSearchParams,
  ApiResponse,
  BasicSearchParams,
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

  deleteProduct: async (uuid: string): Promise<void> => {
    await api.patch(`/products/deactivate/${uuid}`);
  },

  searchProducts: async (
    params: BasicSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> => {
    const queryParams = new URLSearchParams();

    if (params.searchText) {
      queryParams.append('searchText', params.searchText);
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/products/search?${queryParams.toString()}`);
  },

  filterProducts: async (
    params: AdvancedSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> => {
    const queryParams = new URLSearchParams();

    if (params.name) {
      queryParams.append('name', params.name);
    }
    if (params.category) {
      queryParams.append('category', params.category);
    }
    if (params.supplierUuid) {
      queryParams.append('supplierUuid', params.supplierUuid);
    }
    if (params.minPrice) {
      queryParams.append('phone', params.minPrice.toString());
    }
    if (params.maxPrice) {
      queryParams.append('phone', params.maxPrice.toString());
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/products/filter?${queryParams.toString()}`);
  },
};
