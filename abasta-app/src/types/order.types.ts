import type { Supplier } from './supplier.types';

export interface OrderItem {
  uuid?: string;
  productUuid: string;
  productName?: string;
  notes?: string;
  quantity: number;
  price: number;
  total: number;
}

export interface OrderItemResponse {
  uuid: string;
  productUuid: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  notes?: string;
}

export interface Order {
  uuid: string;
  name: string;
  status: OrderStatus;
  totalAmount: number;
  notes?: string;
  deliveryDate?: string;
  createdAt: string;
  updatedAt: string;
  supplier?: Supplier;
  supplierUuid: string;
  items?: OrderItem[];
  notificationMethod?: NotificationMethod;
}

export interface OrderResponseData {
  uuid: string;
  name: string;
  supplierUuid: string;
  status: OrderStatus;
  totalAmount: number;
  notes?: string;
  deliveryDate?: string;
  notificationMethod?: NotificationMethod;
  items: OrderItemResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface CreateOrderRequest {
  name: string;
  supplierUuid: string;
  items: {
    productUuid: string;
    quantity: number;
    notes?: string;
  }[];
  notes?: string;
  deliveryDate?: string;
  notificationMethod: NotificationMethod;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
}

export type OrderStatus =
  | 'PENDING'
  | 'SENT'
  | 'CONFIRMED'
  | 'REJECTED'
  | 'COMPLETED'
  | 'CANCELLED';

export type NotificationMethod = 'EMAIL' | 'WHATSAPP' | 'BOTH';
