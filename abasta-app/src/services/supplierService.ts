import api from './api';
import type {
  CreateSupplierResponse,
  SupplierFormData,
  SupplierApiData,
  Supplier,
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
};
