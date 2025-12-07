// src/hooks/useOrderService.ts
import { useState, useCallback, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderService } from '../services/orderService';
import { supplierService } from '../services/supplierService';
import { useSendOrder, generateWhatsappMessage } from './useSendOrder';

import type {
  OrderResponseData,
  OrderFilterParams,
  PaginatedResponse,
  OrderSearchFilters,
  OrderItem,
  OrderStatus,
  CreateOrderRequest,
} from '../types/order.types';
import type { Product } from '../types/product.types';

interface DeleteResult {
  success: boolean;
  message?: string;
}

export const useOrderService = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [supplierNames, setSupplierNames] = useState<Record<string, string>>(
    {}
  );

  const buildFilterParams = (
    filters: OrderSearchFilters,
    page: number,
    size: number,
    isAdvanced: boolean,
    sortBy?: string,
    sortDir?: 'asc' | 'desc'
  ): OrderFilterParams => {
    const base: OrderFilterParams = {
      page,
      size,
      sortBy,
      sortDir,
    };

    base.searchText = filters.query || undefined;
    base.supplierUuid = filters.supplierUuid || undefined;

    if (isAdvanced) {
      base.name = filters.name || undefined;
      base.notes = filters.notes || undefined;
      base.status = filters.status || undefined;
      base.minAmount =
        filters.minAmount != null ? Number(filters.minAmount) : undefined;
      base.maxAmount =
        filters.maxAmount != null ? Number(filters.maxAmount) : undefined;
      base.deliveryDateFrom = filters.deliveryDateFrom || undefined;
      base.deliveryDateTo = filters.deliveryDateTo || undefined;

      base.createdAtFrom = filters.createdAtFrom || undefined;
      base.createdAtTo = filters.createdAtTo || undefined;
      base.updatedAtFrom = filters.updatedAtFrom || undefined;
      base.updatedAtTo = filters.updatedAtTo || undefined;
    }

    return base;
  };

  const fetchOrders = useCallback(
    async (
      params: OrderFilterParams
    ): Promise<PaginatedResponse<OrderResponseData> | null> => {
      setIsLoading(true);
      setError('');

      try {
        const res = await orderService.filterOrders(params);

        if (!res.success || !res.data) {
          setError(res.message || 'Error al carregar les comandes');
          return null;
        }

        const paginated = res.data as PaginatedResponse<OrderResponseData>;
        const orders = paginated.content;

        const uuids = Array.from(
          new Set(
            orders
              .map(o => o.supplierUuid)
              .filter((uuid): uuid is string => !!uuid)
          )
        );

        const missing = uuids.filter(uuid => !supplierNames[uuid]);

        if (missing.length > 0) {
          const updates: Record<string, string> = {};

          await Promise.all(
            missing.map(async uuid => {
              try {
                const supRes = await supplierService.getSupplierByUuid(uuid);
                if (supRes.data?.name) {
                  updates[uuid] = supRes.data.name;
                }
              } catch (e) {
                console.error('Error carregant proveïdor', uuid, e);
              }
            })
          );

          if (Object.keys(updates).length > 0) {
            setSupplierNames(prev => ({ ...prev, ...updates }));
          }
        }

        return paginated;
      } catch (err) {
        const msg =
          err instanceof Error
            ? err.message
            : 'Error desconegut al carregar les comandes';
        setError(msg);
        return null;
      } finally {
        setIsLoading(false);
      }
    },
    [supplierNames]
  );

  const searchOrders = useCallback(
    async (args: {
      filters: OrderSearchFilters;
      page: number;
      size: number;
      isAdvanced: boolean;
      sortBy?: string;
      sortDir?: 'asc' | 'desc';
    }): Promise<PaginatedResponse<OrderResponseData> | null> => {
      const params = buildFilterParams(
        args.filters,
        args.page,
        args.size,
        args.isAdvanced,
        args.sortBy,
        args.sortDir
      );

      return await fetchOrders(params);
    },
    [fetchOrders]
  );

  const deleteOrder = useCallback(
    async (uuid: string): Promise<DeleteResult> => {
      setIsLoading(true);
      setError('');

      try {
        const res = await orderService.deleteOrder(uuid);

        if (!res.success) {
          const msg = res.message || 'No s’ha pogut eliminar la comanda';
          setError(msg);
          return { success: false, message: msg };
        }

        return { success: true, message: res.message };
      } catch (err) {
        const msg =
          err instanceof Error
            ? err.message
            : 'Error desconegut en eliminar la comanda';
        setError(msg);
        return { success: false, message: msg };
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  return {
    isLoading,
    error,
    fetchOrders,
    searchOrders,
    deleteOrder,
    supplierNames,
  };
};

export type FormMode = 'edit' | 'detail';

export const useOrder = (uuid: string | undefined, mode: FormMode) => {
  const navigate = useNavigate();
  const { sendOrder } = useSendOrder();

  const isReadMode = mode === 'detail';
  const isEditMode = mode === 'edit';

  const [orderStatus, setOrderStatus] = useState<OrderStatus>('PENDING');

  const [selectedSupplierUuid, setSelectedSupplierUuid] = useState<string | null>(
    null
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
  const [loadError, setLoadError] = useState('');

  const [showSendModal, setShowSendModal] = useState(false);

  const [showChangeSupplierModal, setShowChangeSupplierModal] = useState(false);
  const [pendingSupplierUuid, setPendingSupplierUuid] = useState<string | null>(
    null
  );

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const [isSending, setIsSending] = useState(false);

  const canSend = orderStatus === 'PENDING';
  const canEdit = isEditMode && canSend;

  const breadcrumbItems = useMemo(
    () => [
      { label: 'Comandes', path: '/orders' },
      {
        label: isEditMode ? 'Editar comanda' : 'Detall de la comanda',
        active: true,
      },
    ],
    [isEditMode]
  );

  const pageTitle = useMemo(
    () => (isEditMode ? 'Editar comanda' : 'Detall de la comanda'),
    [isEditMode]
  );

  useEffect(() => {
    if (!uuid) return;

    const loadOrder = async () => {
      setError('');
      setLoadError('');

      try {
        const res = await orderService.getOrder(uuid);

        if (!res.success || !res.data) {
          setLoadError(res.message || 'No s’ha pogut carregar la comanda');
          return;
        }

        const order: OrderResponseData = res.data;

        setOrderUuid(order.uuid);
        setOrderName(order.name);
        setNotes(order.notes || '');
        setSelectedSupplierUuid(order.supplierUuid);
        setOrderStatus(order.status);

        const mappedItems: OrderItem[] = order.items.map((item: any) => ({
          productUuid: item.productUuid,
          productName: item.productName || '',
          quantity: item.quantity,
          price: item.unitPrice,
          total: item.subtotal ?? item.unitPrice * item.quantity,
          notes: item.notes,
        }));

        setOrderItems(mappedItems);
      } catch (err) {
        setLoadError('Error al carregar la comanda');
      }
    };

    loadOrder();
  }, [uuid]);

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
    if (!canEdit) return;

    if (!selectedSupplierUuid) {
      setError("Selecciona un proveïdor abans d'afegir productes a la comanda");
      return;
    }

    const existingItem = orderItems.find(
      item => item.productUuid === product.uuid
    );

    if (existingItem) {
      return;
    }

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
    if (!canEdit) return;

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
    if (!canEdit) return;

    setOrderItems(prev =>
      prev.filter(item => item.productUuid !== productUuid)
    );
  };

  const handlePrepareToSend = () => {
    if (!orderUuid) {
      setError('No s’ha pogut carregar la comanda');
      return;
    }

    if (!selectedSupplierUuid) {
      setError('Selecciona un proveïdor');
      return;
    }

    if (orderItems.length === 0) {
      setError('Afegeix productes a la comanda');
      return;
    }

    if (!canSend) {
      setError('Només es poden enviar comandes en estat PENDING');
      return;
    }

    setError('');
    setShowSendModal(true);
  };

  const handleSendOrder = async (method: 'email' | 'whatsapp') => {
    setSuccessMessage('');

    if (!selectedSupplierUuid || !orderUuid) return;

    if (method === 'whatsapp') {
      const text = encodeURIComponent(
        generateWhatsappMessage({ items: orderItems })
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

  const handleSave = async () => {
    if (!orderUuid) {
      setError('No s’ha pogut carregar la comanda');
      return;
    }

    if (!selectedSupplierUuid) {
      setError('Selecciona un proveïdor');
      return;
    }

    if (orderItems.length === 0) {
      setError('Afegeix productes a la comanda');
      return;
    }

    if (!canSend) {
      setError('Només es poden guardar comandes en estat PENDING');
      return;
    }

    setError('');
    setSuccessMessage('');

    try {
      const payload: CreateOrderRequest = {
        name: orderName,
        supplierUuid: selectedSupplierUuid,
        items: orderItems.map(item => ({
          productUuid: item.productUuid,
          quantity: item.quantity,
          notes: item.notes,
        })),
        notes,
        notificationMethod: 'EMAIL',
      };

      const res = await orderService.updateOrder(orderUuid, payload);

      if (!res.success) {
        setError(res.message || 'Error actualitzant la comanda');
        return;
      }

      if (res.data) {
        setOrderStatus(res.data.status);
      }

      setSuccessMessage(res.message || 'Comanda actualitzada correctament');
    } catch (err) {
      setError('Error actualitzant la comanda');
    }
  };

  const handleCancel = () => {
    navigate('/orders');
  };

  const handleDeleteConfirm = async () => {
    if (!orderUuid) return;

    setIsDeleting(true);
    setLoadError('');

    try {
      await orderService.deleteOrder(orderUuid);

      setShowDeleteModal(false);
      navigate('/orders', {
        state: {
          successMessage: `Comanda "${orderName}" eliminada correctament`,
        },
      });
    } catch (err) {
      setLoadError('Error eliminant la comanda');
    } finally {
      setIsDeleting(false);
    }
  };

  const totalAmount = useMemo(
    () => orderItems.reduce((sum, item) => sum + item.total, 0),
    [orderItems]
  );

  return {
    isReadMode,
    isEditMode,
    canEdit,
    canSend,

    orderUuid,
    orderName,
    setOrderName,
    notes,
    setNotes,
    orderItems,

    selectedSupplierUuid,
    setSelectedSupplierUuid,
    supplierName,
    supplierEmail,
    supplierPhone,

    error,
    setError,
    successMessage,
    loadError,
    isSending,
    isDeleting,

    showSendModal,
    setShowSendModal,
    showChangeSupplierModal,
    setShowChangeSupplierModal,
    pendingSupplierUuid,
    setPendingSupplierUuid,
    showDeleteModal,
    setShowDeleteModal,

    handleConfirmSupplierChange,
    handleCancelSupplierChange,
    handleProductClick,
    handleUpdateItem,
    handleRemoveItem,
    handlePrepareToSend,
    handleSendOrder,
    handleSave,
    handleCancel,
    handleDeleteConfirm,

    totalAmount,
    breadcrumbItems,
    pageTitle,
  };
};
