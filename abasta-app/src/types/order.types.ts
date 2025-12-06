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

export type OrderStatus =
  | 'PENDING'
  | 'SENT'
  | 'CONFIRMED'
  | 'REJECTED'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'DELETED';

export type NotificationMethod = 'EMAIL' | 'WHATSAPP' | 'BOTH';

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

/** Filtros que maneja el FRONT en la barra de búsqueda */
export interface OrderSearchFilters {
  query: string; // searchText
  name: string;
  notes: string;
  status: string;
  supplierUuid: string;
  minAmount: number | null;
  maxAmount: number | null;
  deliveryDateFrom: string | null;
  deliveryDateTo: string | null;
  createdAtFrom: string | null;
  createdAtTo: string | null;
  updatedAtFrom: string | null;
  updatedAtTo: string | null;
}

export interface OrderFilterParams {
  orderUuid?: string;
  supplierUuid?: string;
  userUuid?: string;

  searchText?: string;
  name?: string;
  notes?: string;
  status?: string;

  minAmount?: number;
  maxAmount?: number;

  deliveryDateFrom?: string; 
  deliveryDateTo?: string; 

  createdAtFrom?: string; 
  createdAtTo?: string; 
  updatedAtFrom?: string; 
  updatedAtTo?: string;      

  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}

export interface PaginatedResponse<T> {
  content: T[];
  pageable: {
    page: number;
    size: number;
    sort: string;
    totalPages: number;
    totalElements: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
  };
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
}
