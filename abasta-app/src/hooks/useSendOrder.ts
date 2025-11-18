import { useState } from 'react';
import { orderService } from '../services/orderService';
import type { Order } from '../types/order.types';

export const useSendOrder = () => {
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState('');

  const sendOrder = async (uuid: string): Promise<Order | null> => {
    setIsSending(true);
    setError('');

    try {
      const res = await orderService.sendOrder(uuid);

      if (!res.success) {
        setError(res.message || 'Error enviant la comanda');
        return null;
      }

      return res.data ?? null;
    } catch {
      setError("No s'ha pogut enviar la comanda");
      return null;
    } finally {
      setIsSending(false);
    }
  };

  return { sendOrder, isSending, error };
};

export function generateWhatsappMessage(order: Partial<Order>): string {
  const header = `Hola, us fem arribar la comanda: \n`;

  const items = order.items
    ? order.items
        .map(
          item =>
            `• ${item.productName} - Quantitat: ${item.quantity}` +
            (item.notes ? ` *Notes:* ${item.notes}\n` : '\n')
        )
        .join('')
    : '';

  const clientInfo = order.notes ? `*Notes:* ${order.notes}\n` : '';

  return [header, items, clientInfo].join('\n');
}
