export interface Supplier {
  uuid: string;
  companyUuid: string;
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address?: string;
  notes?: string;
  isActive?: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface SupplierFormData {
  name: string;
  contactName: string;
  email: string;
  phone: string;
  address: string;
  city: string;
  zipCode: string;
  notes: string;
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

export interface SearchFilters {
  query: string;
  name: string;
  contactName: string;
  email: string;
  phone: string;
}

export interface GetSuppliersResponse {
  success: boolean;
  message: string;
  data?: Supplier[];
}
