import api from './api';
import type {
  CreateSupplierResponse,
  SupplierFormData,
  SupplierApiData,
  Supplier,
  BasicSearchParams,
  ApiResponse,
  PaginatedResponse,
  AdvancedSearchParams,
  PaginationParams,
} from '../types/supplier.types';

const transformFormDataToApiData = (
  formData: SupplierFormData
): SupplierApiData => {
  const fullAddress = `${formData.address}, ${formData.city}, ${formData.zipCode}`;

  return {
    name: formData.name,
    contactName: formData.contactName,
    email: formData.email,
    phone: formData.phone,
    address: fullAddress,
    notes: formData.notes || undefined,
  };
};

// eslint-disable-next-line @typescript-eslint/no-unused-vars
const transformApiDataToFormData = (supplier: Supplier): SupplierFormData => {
  const addressParts = supplier.address
    ? supplier.address.split(',').map(part => part.trim())
    : '';

  return {
    name: supplier.name,
    contactName: supplier.contactName,
    email: supplier.email,
    phone: supplier.phone,
    address: addressParts[0] || '',
    city: addressParts[1] || '',
    zipCode: addressParts[2] || '',
    notes: supplier.notes || '',
  };
};

export const supplierService = {
  createSupplier: async (
    data: SupplierFormData
  ): Promise<CreateSupplierResponse> => {
    const apiData = transformFormDataToApiData(data);
    return await api.post<CreateSupplierResponse>('/suppliers', apiData);
  },

  getSuppliers: async (
    params: PaginationParams
  ): Promise<ApiResponse<PaginatedResponse<Supplier>>> => {
    const queryString = new URLSearchParams({
      page: params.page.toString(),
      size: params.size.toString(),
      sortBy: params.sortBy || 'name',
      sortDir: params.sortDir || 'asc',
    }).toString();
    return await api.get<ApiResponse<PaginatedResponse<Supplier>>>(
      `/suppliers?${queryString}`
    );
  },

  getSupplierByUuid: async (uuid: string): Promise<ApiResponse<Supplier>> => {
    return await api.get<ApiResponse<Supplier>>(`/suppliers/${uuid}`);
  },

  updateSupplier: async (
    uuid: string,
    data: SupplierFormData
  ): Promise<ApiResponse<SupplierFormData>> => {
    const apiData = transformFormDataToApiData(data);
    const response = await api.put<ApiResponse<Supplier>>(
      `/suppliers/${uuid}`,
      apiData
    );

    if (response.data) {
      return {
        ...response,
        data: transformApiDataToFormData(response.data),
      };
    }
    return response as ApiResponse<SupplierFormData>;
  },

  deleteSupplier: async (uuid: string): Promise<void> => {
    await api.patch(`/suppliers/${uuid}/status?isActive=false`);
  },

  searchSuppliers: async (
    params: BasicSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Supplier>>> => {
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

    return await api.get(`/suppliers/search?${queryParams.toString()}`);
  },

  filterSuppliers: async (
    params: AdvancedSearchParams
  ): Promise<ApiResponse<PaginatedResponse<Supplier>>> => {
    const queryParams = new URLSearchParams();

    if (params.name) {
      queryParams.append('name', params.name);
    }
    if (params.contactName) {
      queryParams.append('contactName', params.contactName);
    }
    if (params.email) {
      queryParams.append('email', params.email);
    }
    if (params.phone) {
      queryParams.append('phone', params.phone);
    }
    if (params.page !== undefined) {
      queryParams.append('page', params.page.toString());
    }
    if (params.size !== undefined) {
      queryParams.append('size', params.size.toString());
    }

    return await api.get(`/suppliers/filter?${queryParams.toString()}`);
  },
};
