import api from './api';
import type {
  Order,
  OrderResponseData,
  ApiResponse,
  CreateOrderRequest,
  OrderFilterParams,
  PaginatedResponse,
} from '../types/order.types';

export const orderService = {
  createOrder: async (
    orderData: CreateOrderRequest
  ): Promise<ApiResponse<Order>> => {
    return await api.post('/orders/create', orderData);
  },

  sendOrder: async (uuid: string): Promise<ApiResponse<Order>> => {
    return await api.post(`/orders/${uuid}/send`, {});
  },

  updateOrder: async (
    uuid: string,
    orderData: CreateOrderRequest
  ): Promise<ApiResponse<OrderResponseData>> => {
    return await api.put(`/orders/update/${uuid}`, orderData);
  },

  filterOrders: async (
    params: OrderFilterParams
  ): Promise<ApiResponse<PaginatedResponse<OrderResponseData>>> => {
    const queryParams = new URLSearchParams();

    if (params.orderUuid) queryParams.append('orderUuid', params.orderUuid);
    if (params.supplierUuid)
      queryParams.append('supplierUuid', params.supplierUuid);
    if (params.userUuid) queryParams.append('userUuid', params.userUuid);

    if (params.searchText) queryParams.append('searchText', params.searchText);
    if (params.name) queryParams.append('name', params.name);
    if (params.notes) queryParams.append('notes', params.notes);
    if (params.status) queryParams.append('status', params.status);

    if (params.minAmount != null)
      queryParams.append('minAmount', params.minAmount.toString());
    if (params.maxAmount != null)
      queryParams.append('maxAmount', params.maxAmount.toString());

    if (params.deliveryDateFrom)
      queryParams.append('deliveryDateFrom', params.deliveryDateFrom);
    if (params.deliveryDateTo)
      queryParams.append('deliveryDateTo', params.deliveryDateTo);

    if (params.createdAtFrom)
      queryParams.append('createdAtFrom', `${params.createdAtFrom} 00:00:00`);
    if (params.createdAtTo)
      queryParams.append('createdAtTo', `${params.createdAtTo} 23:59:59`);

    if (params.updatedAtFrom)
      queryParams.append('updatedAtFrom', `${params.updatedAtFrom} 00:00:00`);
    if (params.updatedAtTo)
      queryParams.append('updatedAtTo', `${params.updatedAtTo} 23:59:59`);

    if (params.page !== undefined)
      queryParams.append('page', params.page.toString());
    if (params.size !== undefined)
      queryParams.append('size', params.size.toString());
    if (params.sortBy) queryParams.append('sortBy', params.sortBy);
    if (params.sortDir) queryParams.append('sortDir', params.sortDir);

    return await api.get(`/orders/filter?${queryParams.toString()}`);
  },

  deleteOrder: async (
    uuid: string
  ): Promise<ApiResponse<OrderResponseData>> => {
    return await api.patch(`/orders/delete/${uuid}`);
  },

  async getOrder(uuid: string): Promise<ApiResponse<OrderResponseData>> {
    return await api.get(`/orders/${uuid}`);
  },
};
