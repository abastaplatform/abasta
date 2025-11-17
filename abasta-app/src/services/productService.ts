import api from './api';
import type {
  ProductFormData,
  Product,
  BasicProductSearchParams,
  AdvancedProductSearchParams,
  PaginatedResponse,
  ApiResponse,
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
    async getAllProducts(
    params: BasicProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== '') {
        query.append(key, String(value));
      }
    });

    return await api.get(`/products?${query.toString()}`);
  },
  

  async getProductByUuid(uuid: string): Promise<ApiResponse<Product>> {
    return await api.get<ApiResponse<Product>>(`/products/${uuid}`);
  },
  async searchProducts(
    params: BasicProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    if (params.searchText) query.append('searchText', params.searchText);
    if (params.page != null) query.append('page', String(params.page));
    if (params.size != null) query.append('size', String(params.size));
    query.append('sortBy', params.sortBy || 'name');
    query.append('sortDir', params.sortDir || 'asc');

    return await api.get(`/products/search?${query.toString()}`);
  },

  async searchProductsBySupplier(
    supplierUuid: string,
    params: BasicProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    if (params.searchText) query.append('searchText', params.searchText);
    if (params.page != null) query.append('page', String(params.page));
    if (params.size != null) query.append('size', String(params.size));
    query.append('sortBy', params.sortBy || 'name');
    query.append('sortDir', params.sortDir || 'asc');

    return await api.get(
      `/products/search/supplier/${supplierUuid}?${query.toString()}`
    );
  },

  async filterProductsBySupplier(
    supplierUuid: string,
    params: AdvancedProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== '') {
        query.append(key, String(value));
      }
    });

    return await api.get(
      `/products/filter/supplier/${supplierUuid}?${query.toString()}`
    );
  },
  async filterProducts(
    params: AdvancedProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== '') {
        query.append(key, String(value));
      }
    });

    return await api.get(`/products/filter?${query.toString()}`);
  },
  async listBySupplier(
    supplierUuid: string,
    params: BasicProductSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Product>>> {
    const query = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined && value !== '') {
        query.append(key, String(value));
        }
    });

    return await api.get(
        `/product/supplier/${supplierUuid}?${query.toString()}`
    );
  },

  async deleteProduct(uuid: string): Promise<ApiResponse<Product>> {
    try {
      const res = await api.patch<ApiResponse<Product>>(
        `/products/deactivate/${uuid}`
      );

      return res;
    } catch (error: any) {
      return {
        success: false,
        message:
          error.response?.data?.message ||
          "Error inesperat en eliminar el producte",
        data: undefined,
      };
    }
  },

  async uploadImage(uuid: string, file: File) {
    const formData = new FormData();
    formData.append('image', file);

        return await api.upload<ApiResponse<string>>(
            `/products/upload/${uuid}`,
            formData
        );
  },

  async uploadTempImage(file: File) {
    const formData = new FormData();
    formData.append('image', file);

    return await api.upload<ApiResponse<string>>(
        '/products/upload-temp',
        formData
    );
  },
};