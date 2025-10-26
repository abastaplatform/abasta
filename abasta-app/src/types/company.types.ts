export interface Company {
  id: string;
  uuid?: string;
  name: string;
  taxId: string;
  email: string;
  phone?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  status?: 'ACTIVE' | 'INACTIVE' | 'PENDING';
  createdAt?: string;
  updatedAt?: string;
}

export interface RegisterCompanyData {
  companyName: string;
  taxId: string;
  adminFirstName: string;
  adminLastName: string;
  adminEmail: string;
  adminPassword: string;
}
