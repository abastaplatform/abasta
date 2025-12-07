import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useSearchParams, useLocation } from 'react-router-dom';

import './OrderList.scss';

import type {
  OrderResponseData,
  OrderSearchFilters,
  OrderFilterParams,
} from '../../../types/order.types';

import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import OrdersTable from './OrdersTable/OrdersTable';
import OrderCard from './OrderCard/OrderCard';
import DeleteModal from '../../common/DeleteModal/DeleteModal';
import SearchBar from './SearchBar/SearchBar';
import { supplierService } from '../../../services/supplierService';
import type { CachedSuppliersResult } from '../../../types/supplier.types';
import { useOrderService } from '../../../hooks/useOrderService';

const OrderList = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const location = useLocation();

  const supplierUuidParam = searchParams.get('supplier');

  const [supplierName, setSupplierName] = useState<string | null>(null);

  const [orders, setOrders] = useState<OrderResponseData[]>([]);
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [currentFilters, setCurrentFilters] = useState<OrderSearchFilters>({
    query: '',
    name: '',
    notes: '',
    status: '',
    supplierUuid: supplierUuidParam || '',
    minAmount: null,
    maxAmount: null,
    deliveryDateFrom: null,
    deliveryDateTo: null,
    createdAtFrom: null,
    createdAtTo: null,
    updatedAtFrom: null,
    updatedAtTo: null,
  });

  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);

  const [sortBy, setSortBy] = useState<string>('createdAt');
  const [sortDir, setSortDir] = useState<'asc' | 'desc'>('desc');

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [orderToDelete, setOrderToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const [suppliersCache, setSuppliersCache] = useState<
    Map<string, CachedSuppliersResult>
  >(new Map());
  const [suppliersCacheInitialized, setSuppliersCacheInitialized] =
    useState(false);

  const { isLoading, error, fetchOrders, deleteOrder, supplierNames } =
    useOrderService();

  useEffect(() => {
    const state = location.state as { successMessage?: string } | null;

    if (state?.successMessage) {
      setSuccessMessage(state.successMessage);

      navigate(location.pathname + location.search, {
        replace: true,
        state: undefined,
      });
    }
  }, [location, navigate]);

  useEffect(() => {
    const loadSupplierName = async () => {
      if (!supplierUuidParam) {
        setSupplierName(null);
        return;
      }

      try {
        const response = await supplierService.getSupplierByUuid(
          supplierUuidParam
        );
        setSupplierName(response.data?.name || null);
      } catch (err) {
        console.error("Error carregant el nom del proveïdor:", err);
        setSupplierName(null);
      }
    };

    loadSupplierName();
  }, [supplierUuidParam]);

  useEffect(() => {
    if (supplierUuidParam) {
      setCurrentFilters(prev => ({ ...prev, supplierUuid: supplierUuidParam }));
    } else {
      setCurrentFilters(prev => ({ ...prev, supplierUuid: '' }));
    }
  }, [supplierUuidParam]);

  const buildFilterParams = (
    filters: OrderSearchFilters,
    page: number,
    size: number,
    isAdvanced: boolean
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
    }

    return base;
  };

  const performSearch = useCallback(
    async (filters: OrderSearchFilters, isAdvanced: boolean) => {
      const params = buildFilterParams(
        filters,
        currentPage,
        itemsPerPage,
        isAdvanced
      );

      const paginated = await fetchOrders(params);

      if (paginated) {
        setOrders(paginated.content);
        setTotalElements(paginated.pageable.totalElements);
        setTotalPages(paginated.pageable.totalPages);
      } else {
        setOrders([]);
        setTotalElements(0);
        setTotalPages(0);
      }
    },
    [currentPage, itemsPerPage, fetchOrders, sortBy, sortDir]
  );

  useEffect(() => {
    performSearch(currentFilters, isAdvancedSearch);
  }, [currentPage, itemsPerPage, currentFilters, isAdvancedSearch, performSearch]);

  const handleSearch = (filters: OrderSearchFilters, isAdvanced = false) => {
    setCurrentPage(0);
    setIsAdvancedSearch(isAdvanced);
    setCurrentFilters(filters);
  };

  const handleClearSearch = () => {
    const emptyFilters: OrderSearchFilters = {
      query: '',
      name: '',
      notes: '',
      status: '',
      supplierUuid: supplierUuidParam || '',
      minAmount: null,
      maxAmount: null,
      deliveryDateFrom: null,
      deliveryDateTo: null,
      createdAtFrom: null,
      createdAtTo: null,
      updatedAtFrom: null,
      updatedAtTo: null,
    };

    setIsAdvancedSearch(false);
    setCurrentPage(0);
    setCurrentFilters(emptyFilters);
    setSortBy('createdAt');
    setSortDir('desc');
  };

  const fetchSuppliersWithCache = useCallback(
    async (page: number, query: string) => {
      const cacheKey = `${page}-${query}`;
      const cached = suppliersCache.get(cacheKey);
      if (cached) return cached;

      const result = await supplierService.getSuppliersForAutocomplete(
        page,
        query
      );

      setSuppliersCache(prev => new Map(prev).set(cacheKey, result));
      if (!suppliersCacheInitialized) setSuppliersCacheInitialized(true);

      return result;
    },
    [suppliersCache, suppliersCacheInitialized]
  );

  const handleDeleteClick = (orderUuid: string, orderName: string) => {
    setOrderToDelete({ uuid: orderUuid, name: orderName });
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!orderToDelete) return;

    setIsDeleting(true);

    try {
      const { success, message } = await deleteOrder(orderToDelete.uuid);

      if (!success) {
        setShowDeleteModal(false);
        return;
      }

      const msg =
        message || `Comanda "${orderToDelete.name}" eliminada correctament`;

      setSuccessMessage(msg);
      setShowDeleteModal(false);
      setOrderToDelete(null);

      await performSearch(currentFilters, isAdvancedSearch);

      setTimeout(() => setSuccessMessage(''), 3000);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
    setOrderToDelete(null);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const handleSortChange = (newSortBy: string) => {
    setCurrentPage(0);
    setSortBy(newSortBy);

    setSortDir(prevDir => {
      if (newSortBy !== sortBy) {
        return 'asc';
      }
      return prevDir === 'asc' ? 'desc' : 'asc';
    });
  };

  const breadcrumbItems = [{ label: 'Comandes', active: true }];

  return (
    <div className="order-list-container form-container">
      <div className="container-fluid py-3">
        <PageHeader
          title={`Comandes ${supplierName ? `- ${supplierName}` : ''}`}
          breadcrumbItems={breadcrumbItems}
          actions={
            <Button
              title="Nova comanda"
              onClick={() => navigate('/orders/new')}
            />
          }
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}

        <SearchBar
          onSearch={handleSearch}
          onClear={handleClearSearch}
          fetchSuppliers={fetchSuppliersWithCache}
          supplierName={supplierName}
          supplierUuid={supplierUuidParam}
          placeholder="Cercar per nom, estat, proveïdor..."
        />

        {isLoading && (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Carregant...</span>
            </div>
          </div>
        )}

        {!isLoading && (
          <>
            <div className="d-none d-md-block">
              <OrdersTable
                orders={orders}
                onDelete={handleDeleteClick}
                supplierNames={supplierNames}
                sortBy={sortBy}
                sortDir={sortDir}
                onSortChange={handleSortChange}
              />
            </div>
            
            <div className="d-block d-md-none">
              <OrderCard
                orders={orders}
                onDelete={handleDeleteClick}
                supplierNames={supplierNames}
              />
            </div>
          </>
        )}

        {!isLoading && orders.length > 0 && (
          <Pagination
            type="comanda"
            currentPage={currentPage + 1}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            itemsPerPage={itemsPerPage}
            totalItems={totalElements}
            onItemsPerPageChange={handleItemsPerPageChange}
          />
        )}

        <DeleteModal
          show={showDeleteModal}
          entityType="comanda"
          itemName={orderToDelete?.name || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default OrderList;
