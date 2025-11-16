import type { Product, ProductFormData } from '../types/product.types';

export const productToFormData = (product: Product): ProductFormData => ({
  uuid: product.uuid,
  supplierUuid: product.supplier?.uuid ?? '',
  category: product.category ?? '',
  name: product.name ?? '',
  description: product.description ?? '',
  price: product.price != null ? String(product.price) : '',
  volume: product.volume != null ? String(product.volume) : '',
  unit: product.unit ?? '',
  imageUrl: product.imageUrl ?? '',
});
