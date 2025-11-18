import api from './api';
import type {
  Order,
  ApiResponse,
  CreateOrderRequest,
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

  async getOrder(uuid: string): Promise<ApiResponse<Order>> {
    // Simulació
    const simulacio: Order = {
      uuid,
      name: 'Comanda Demo 001',
      status: 'PENDING',
      totalAmount: 260.0,
      notes: 'Deixar al magatzem del darrere',
      deliveryDate: '2025-02-14',
      createdAt: '2025-01-20T10:24:00',
      updatedAt: '2025-01-20T10:24:00',
      supplierUuid: 'b3a1a2c4-1f92-4e8a-8e91-1d2fa00101',

      items: [
        {
          uuid: 'item-1',
          productUuid: 'prod-123',
          productName: 'Aigua de litre',
          quantity: 10,
          price: 1.2,
          total: 12.0,
          notes: 'provaaaaa',
        },
        {
          uuid: 'item-2',
          productUuid: 'prod-456',
          productName: 'Llet sencera',
          quantity: 6,
          price: 0.95,
          total: 5.7,
          notes: 'provaaaaa2',
        },
        {
          uuid: 'item-4',
          productUuid: 'prod-456222',
          productName: 'Llet sencera',
          quantity: 6,
          price: 0.95,
          total: 8.0,
        },
        {
          uuid: 'item-3',
          productUuid: 'prod-789',
          productName: 'Cacaolat 1L',
          quantity: 8,
          price: 1.5,
          total: 12.0,
        },
        {
          uuid: 'item-4',
          productUuid: 'prod-987',
          productName: 'Nescafé Classic',
          quantity: 2,
          price: 4.5,
          total: 9.0,
          notes: 'provaaaaa3',
        },
      ],
    };

    return new Promise(resolve => {
      setTimeout(() => {
        resolve({
          success: true,
          message: 'Dades de simulacio carregades correctament',
          data: simulacio,
        });
      }, 500);
    });
  },
};
