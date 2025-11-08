import api from './api';
import type {
  CreateSupplierResponse,
  SupplierFormData,
} from '../types/supplier.types';

export const supplierService = {
  createSupplier: async (
    data: SupplierFormData
  ): Promise<CreateSupplierResponse> => {
    const response = await api.post<CreateSupplierResponse>('/suppliers', data);
    return response;
  },
};
