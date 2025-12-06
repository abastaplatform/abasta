import { useEffect, useState, useCallback } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { supplierService } from '../services/supplierService';
import { orderService } from '../services/orderService';
import { useSendOrder, generateWhatsappMessage } from './useSendOrder';

import type {
  CreateOrderRequest,
  OrderItem,
  OrderResponseData,
} from '../types/order.types';
import type { Product } from '../types/product.types';

export const useOrderCreate = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const supplierUuidFromUrl = searchParams.get('supplier');
  const duplicateFromUuid = searchParams.get('duplicate');

  const [selectedSupplierUuid, setSelectedSupplierUuid] = useState<string | null>(
    supplierUuidFromUrl
  );
  const [supplierName, setSupplierName] = useState<string | null>(null);
  const [supplierEmail, setSupplierEmail] = useState<string | null>(null);
  const [supplierPhone, setSupplierPhone] = useState<string | null>(null);

  const [orderUuid, setOrderUuid] = useState<string>('');
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [orderName, setOrderName] = useState<string>('');
  const [notes, setNotes] = useState<string>('');

  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [showSendModal, setShowSendModal] = useState(false);

  const [showChangeSupplierModal, setShowChangeSupplierModal] = useState(false);
  const [pendingSupplierUuid, setPendingSupplierUuid] = useState<string | null>(
    null
  );

  const { sendOrder } = useSendOrder();

  const isSupplierFromUrl = !!supplierUuidFromUrl;

  useEffect(() => {
    if (!duplicateFromUuid) return;

    const loadOrderToDuplicate = async () => {
      setError('');

      try {
        const res = await orderService.getOrder(duplicateFromUuid);

        if (!res.success || !res.data) {
          setError(
            res.message || 'No s’ha pogut carregar la comanda a duplicar'
          );
          return;
        }

        const order: OrderResponseData = res.data;

        setSelectedSupplierUuid(order.supplierUuid);
        setOrderName(`Còpia de ${order.name}`);
        setNotes(order.notes || '');

        const mappedItems: OrderItem[] = order.items.map(item => ({
          productUuid: item.productUuid,
          quantity: item.quantity,
          price: item.unitPrice,
          total: item.subtotal ?? item.unitPrice * item.quantity,
          notes: item.notes,
        }));

        setOrderItems(mappedItems);
        setOrderUuid(''); 
      } catch {
        setError('Error carregant la comanda a duplicar');
      }
    };

    loadOrderToDuplicate();
  }, [duplicateFromUuid]);

  useEffect(() => {
    const loadSupplierInfo = async () => {
      if (!selectedSupplierUuid) {
        setSupplierName(null);
        setSupplierEmail(null);
        setSupplierPhone(null);
        return;
      }

      try {
        const response =
          await supplierService.getSupplierByUuid(selectedSupplierUuid);
        setSupplierName(response.data?.name || null);
        setSupplierEmail(response.data?.email || null);
        setSupplierPhone(response.data?.phone || null);
      } catch (err) {
        console.error('Error carregant el nom del proveïdor:', err);
        setSupplierName(null);
        setSupplierEmail(null);
        setSupplierPhone(null);
      }
    };

    loadSupplierInfo();
  }, [selectedSupplierUuid]);

  useEffect(() => {
    if (supplierName && !orderName) {
      setOrderName(`Comanda ${supplierName}`);
    }
  }, [supplierName, orderName]);

  const handleSupplierChange = (uuid: string) => {
    setOrderName('');
    if (uuid !== selectedSupplierUuid && orderItems.length > 0) {
      setPendingSupplierUuid(uuid);
      setShowChangeSupplierModal(true);
    } else {
      setSelectedSupplierUuid(uuid);
      setOrderItems([]);
    }
  };

  const handleConfirmSupplierChange = () => {
    if (pendingSupplierUuid) {
      setSelectedSupplierUuid(pendingSupplierUuid);
      setPendingSupplierUuid(null);
    } else {
      setSelectedSupplierUuid(null);
    }
    setOrderItems([]);
    setOrderName('');
    setShowChangeSupplierModal(false);
  };

  const handleCancelSupplierChange = () => {
    setPendingSupplierUuid(null);
    setShowChangeSupplierModal(false);
  };

  const handleProductClick = (product: Product) => {
    if (!selectedSupplierUuid) {
      setError("Selecciona un proveïdor abans d'afegir productes a la comanda");
      return;
    }

    const existingItem = orderItems.find(
      item => item.productUuid === product.uuid
    );
    if (existingItem) return;

    const newItem: OrderItem = {
      productUuid: product.uuid,
      productName: product.name,
      quantity: 1,
      price: product.price,
      total: product.price,
    };

    setOrderItems(prev => [...prev, newItem]);
    setError('');
  };

  const handleUpdateItem = (
    productUuid: string,
    updates: Partial<OrderItem>
  ) => {
    setOrderItems(prev =>
      prev.map(item => {
        if (item.productUuid === productUuid) {
          const newQuantity = updates.quantity ?? item.quantity;
          const price = item.price;

          return {
            ...item,
            ...updates,
            quantity: newQuantity,
            price,
            total: price * newQuantity,
          };
        }
        return item;
      })
    );
  };

  const handleRemoveItem = (productUuid: string) => {
    setOrderItems(prev =>
      prev.filter(item => item.productUuid !== productUuid)
    );
  };

  const saveOrder = useCallback(
    async (opts?: { silent?: boolean }): Promise<boolean> => {
      if (!selectedSupplierUuid) {
        setError('Selecciona un proveïdor');
        return false;
      }

      if (orderItems.length === 0) {
        setError('Afegeix productes a la comanda');
        return false;
      }

      setError('');
      if (!opts?.silent) setSuccessMessage('');

      try {
        const payload: CreateOrderRequest = {
          name: orderName,
          supplierUuid: selectedSupplierUuid || '',
          items: orderItems.map(item => ({
            productUuid: item.productUuid,
            quantity: item.quantity,
            notes: item.notes,
          })),
          notes,
          notificationMethod: 'BOTH',
        };

        let res;
        if (!orderUuid) {
          res = await orderService.createOrder(payload);
        } else {
          res = await orderService.updateOrder(orderUuid, payload);
        }

        if (!res.success || !res.data) {
          setError(res.message || 'Error guardant la comanda');
          return false;
        }

        setOrderUuid(res.data.uuid);
        if (!opts?.silent) {
          setSuccessMessage(res.message || 'Comanda guardada correctament');
        }
        return true;
      } catch {
        setError('Error guardant la comanda');
        return false;
      }
    },
    [selectedSupplierUuid, orderItems, orderName, notes, orderUuid]
  );

  const handleSave = async () => {
    await saveOrder();
  };

  const handlePrepareToSend = async () => {
    const ok = await saveOrder({ silent: true });
    if (!ok) return;
    setShowSendModal(true);
  };

  const handleSendOrder = async (method: 'email' | 'whatsapp') => {
    setSuccessMessage('');

    if (!selectedSupplierUuid || !orderUuid) return;

    if (method === 'whatsapp') {
      const text = encodeURIComponent(
        generateWhatsappMessage({ items: orderItems, notes })
      );
      const phone = supplierPhone?.replace(/\D/g, '');

      if (!phone) {
        setError('El proveïdor no té un número de telèfon vàlid');
        return;
      }

      window.open(`https://wa.me/${phone}?text=${text}`, '_blank');
      setSuccessMessage('WhatsApp obert correctament');
      return;
    }

    setIsSending(true);
    setError('');

    const result = await sendOrder(orderUuid);

    setIsSending(false);
    setShowSendModal(false);

    if (result?.status === 'SENT') {
      navigate('/orders', {
        state: { successMessage: 'Comanda enviada correctament!' },
        replace: true,
      });
    }
  };

  const totalAmount = orderItems.reduce((sum, item) => sum + item.total, 0);

  return {
    selectedSupplierUuid,
    supplierName,
    supplierEmail,
    supplierPhone,
    orderUuid,
    orderItems,
    orderName,
    notes,
    error,
    successMessage,
    isSending,
    showSendModal,
    showChangeSupplierModal,
    isSupplierFromUrl,
    totalAmount,

    setOrderName,
    setNotes,
    setShowSendModal,
    setError,

    handleSupplierChange,
    handleConfirmSupplierChange,
    handleCancelSupplierChange,
    handleProductClick,
    handleUpdateItem,
    handleRemoveItem,
    handleSave,
    handlePrepareToSend,
    handleSendOrder,
  };
};

export type UseOrderCreateReturn = ReturnType<typeof useOrderCreate>;
