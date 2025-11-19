import api from './api';
import type {
  AdvancedSearchParams,
  ApiResponse,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  Product,
  ProductFormData,
} from '../types/product.types';

const formDataToApiData = (data: ProductFormData) => ({
  supplierUuid: data.supplierUuid,
  category: data.category || null,
  name: data.name,
  description: data.description || null,
  price: data.price ? Number(data.price) : 0,
  volume: data.volume ? Number(data.volume) : null,
  unit: data.unit || null,
  imageUrl: data.imageUrl || null,
});

const apiDataToFormData = (product: Product): ProductFormData => ({
  uuid: product.uuid,
  supplierUuid: product.supplier?.uuid || '',
  category: product.category || '',
  name: product.name,
  description: product.description || '',
  price: String(product.price),
  volume: product.volume != null ? String(product.volume) : '',
  unit: product.unit || '',
  imageUrl: product.imageUrl || '',
});

export const productService = {
  async createProduct(data: ProductFormData): Promise<ApiResponse<Product>> {
    return await api.post<ApiResponse<Product>>(
      '/products/create',
      formDataToApiData(data)
    );
  },

  async updateProduct(
    uuid: string,
    data: ProductFormData
  ): Promise<ApiResponse<ProductFormData>> {
    const apiData = formDataToApiData(data);

    const response = await api.put<ApiResponse<Product>>(
      `/products/${uuid}`,
      apiData
    );

    return {
      success: response.success,
      message: response.message,
      data: response.data ? apiDataToFormData(response.data) : undefined,
    };
  },

  async getProductByUuid(uuid: string): Promise<ApiResponse<Product>> {
    return await api.get<ApiResponse<Product>>(`/products/${uuid}`);
  },

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
      `/products/search?supplierUuid=${supplierUuid}&${queryString}`
    );
  },

  deleteProduct: async (uuid: string): Promise<void> => {
    await api.patch(`/products/deactivate/${uuid}`);
  },

  searchProducts: async (
    params: BasicSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> => {
    const queryParams = new URLSearchParams();

    if (params.supplierUuid !== undefined) {
      queryParams.append('supplierUuid', params.supplierUuid);
    }
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
    if (params.volume) {
      queryParams.append('volume', params.volume.toString());
    }
    if (params.unit) {
      queryParams.append('unit', params.unit);
    }
    if (params.minPrice) {
      queryParams.append('minPrice', params.minPrice.toString());
    }
    if (params.maxPrice) {
      queryParams.append('maxPrice', params.maxPrice.toString());
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/products/filter?${queryParams.toString()}`);
  },

  uploadImage: async (uuid: string, file: File) => {
    const formData = new FormData();
    formData.append('image', file);

    return await api.upload<ApiResponse<string>>(
      `/products/upload/${uuid}`,
      formData
    );
  },

  uploadTempImage: async (file: File) => {
    const formData = new FormData();
    formData.append('image', file);

    return await api.upload<ApiResponse<string>>(
      '/products/upload-temp',
      formData
    );
  },
};
