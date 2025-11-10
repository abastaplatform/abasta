import type { Supplier } from '../types/supplier.types';
import type { SupplierFormData } from '../types/supplier.types';

export const supplierToFormData = (supplier: Supplier): SupplierFormData => {
  const [address = '', city = '', zipCode = ''] = supplier.address
    ? supplier.address.split(',').map(part => part.trim())
    : [];

  return {
    uuid: supplier.uuid,
    name: supplier.name || '',
    contactName: supplier.contactName || '',
    email: supplier.email || '',
    phone: supplier.phone || '',
    address,
    city,
    zipCode,
    notes: supplier.notes || '',
  };
};
