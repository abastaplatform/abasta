/* eslint-disable @typescript-eslint/no-unused-vars */
import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import './OrderCreate.scss';

import type {
  AdvancedSearchParams,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  Product,
  SearchFilters,
} from '../../../types/product.types';
import type { CreateOrderRequest, OrderItem } from '../../../types/order.types';
import type { CachedSuppliersResult } from '../../../types/supplier.types';

import PageHeader from '../../common/PageHeader/PageHeader';
import Alert from '../../common/Alert/Alert';
import SendOrderModal from '../SendOrderModal/SendOrderModal';

import { productService } from '../../../services/productService';
import { supplierService } from '../../../services/supplierService';
import SearchBar from '../../products/ProductList/SearchBar/SearchBar';
import ProductTable from '../../products/ProductList/ProductTable/ProductTable';
import OrderSummary from './OrderSummary/OrderSummary';
import Pagination from '../../common/Pagination/Pagination';
import Form from 'react-bootstrap/esm/Form';
import {
  generateWhatsappMessage,
  useSendOrder,
} from '../../../hooks/useSendOrder';
import { orderService } from '../../../services/orderService';
import ProductCard from '../../products/ProductList/ProductCard/ProductCard';
import OrderCreateScrollButton from './OrderCreateScrollButton/OrderCreateScrollButton';
import SupplierAutocomplete from '../../common/SupplierAutocomplete/SupplierAutocomplete';
import ConfirmModal from '../../common/ConfirmModal/ConfirmModal';

const OrderCreate = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const supplierUuidFromUrl = searchParams.get('supplier');

  const [selectedSupplierUuid, setSelectedSupplierUuid] = useState<
    string | null
  >(supplierUuidFromUrl);
  const [supplierName, setSupplierName] = useState<string | null>(null);
  const [supplierEmail, setSupplierEmail] = useState<string | null>(null);
  const [supplierPhone, setSupplierPhone] = useState<string | null>(null);

  const [products, setProducts] = useState<Product[]>([]);
  const [orderUuid, setOrderUuid] = useState<string>('');
  const [orderItems, setOrderItems] = useState<OrderItem[]>([]);
  const [orderName, setOrderName] = useState<string>('');
  const [notes, setNotes] = useState<string>('');

  const { sendOrder } = useSendOrder();

  const [isLoading, setIsLoading] = useState(false);
  const [isSending, setIsSending] = useState(false);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [currentFilters, setCurrentFilters] = useState<SearchFilters>({
    query: '',
    name: '',
    supplierUuid: '',
    category: '',
    minPrice: null,
    maxPrice: null,
    volume: null,
    unit: '',
  });

  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);
  const [manualSearchActive, setManualSearchActive] = useState(false);

  const [showSendModal, setShowSendModal] = useState(false);

  const [showChangeSupplierModal, setShowChangeSupplierModal] = useState(false);
  const [pendingSupplierUuid, setPendingSupplierUuid] = useState<string | null>(
    null
  );

  const [suppliersCache, setSuppliersCache] = useState<
    Map<string, CachedSuppliersResult>
  >(new Map());
  const [suppliersCacheInitialized, setSuppliersCacheInitialized] =
    useState(false);

  const selectedProductUuids = new Set(
    orderItems.map(item => item.productUuid)
  );

  const isSupplierFromUrl = !!supplierUuidFromUrl;

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
    if (selectedSupplierUuid) {
      setCurrentFilters(prev => ({
        ...prev,
        supplierUuid: selectedSupplierUuid,
      }));
    } else {
      setCurrentFilters(prev => ({ ...prev, supplierUuid: '' }));
    }
  }, [selectedSupplierUuid]);

  useEffect(() => {
    if (supplierName) {
      setOrderName(`Comanda ${supplierName}`);
    }
  }, [supplierName]);

  useEffect(() => {
    const loadForPage = async () => {
      if (manualSearchActive) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        await loadProducts();
      }
    };

    loadForPage();
  }, [currentPage, itemsPerPage, selectedSupplierUuid]);

  const loadProducts = async () => {
    setIsLoading(true);
    setError('');

    try {
      const paginationParams: PaginationParams = {
        page: currentPage,
        size: itemsPerPage,
        sortBy: 'name',
        sortDir: 'asc',
      };

      let response;
      if (selectedSupplierUuid) {
        response = await productService.getProductBySupplier(
          selectedSupplierUuid,
          paginationParams
        );
      } else {
        response = await productService.getProducts(paginationParams);
      }

      setProducts(response.data?.content || []);
      setTotalElements(response.data?.pageable.totalElements || 0);
      setTotalPages(response.data?.pageable.totalPages || 0);
    } catch (err) {
      const message =
        err instanceof Error ? err.message : 'Error al carregar els productes.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const performSearch = async (
    filtersParam: SearchFilters,
    isAdvanced: boolean
  ) => {
    setIsLoading(true);
    setError('');

    try {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      let response: any;

      if (isAdvanced) {
        const params: AdvancedSearchParams = {
          name: filtersParam.name || undefined,
          supplierUuid: filtersParam.supplierUuid || undefined,
          category: filtersParam.category || undefined,
          minPrice: filtersParam.minPrice || undefined,
          maxPrice: filtersParam.maxPrice || undefined,
          volume: filtersParam.volume || undefined,
          unit: filtersParam.unit || undefined,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };

        response = await productService.filterProducts(params);
        setIsAdvancedSearch(true);
      } else {
        const params: BasicSearchParams = {
          searchText: filtersParam.query,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };

        response = await productService.searchProducts(params);
        setIsAdvancedSearch(false);
      }

      if (response?.success && response?.data) {
        const paginated = response.data as PaginatedResponse<Product>;
        setProducts(paginated.content);
        setTotalElements(paginated.pageable.totalElements);
        setTotalPages(paginated.pageable.totalPages);
      } else {
        setProducts([]);
        setTotalElements(0);
        setTotalPages(0);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al cercar productes';
      setError(errorMessage);
      console.error('performSearch error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = async (
    filters: SearchFilters,
    isAdvanced: boolean = false
  ) => {
    setCurrentFilters(filters);
    setIsAdvancedSearch(isAdvanced);
    setManualSearchActive(true);
    setCurrentPage(0);
    await performSearch(filters, isAdvanced);
  };

  const handleClearSearch = () => {
    const emptyFilters: SearchFilters = {
      query: '',
      name: '',
      category: '',
      minPrice: null,
      maxPrice: null,
      supplierUuid: selectedSupplierUuid || '',
      volume: null,
      unit: '',
    };

    setCurrentFilters(emptyFilters);
    setIsAdvancedSearch(false);
    setManualSearchActive(false);
    setCurrentPage(0);
    loadProducts();
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

  const handleSupplierChange = (uuid: string) => {
    setOrderName('');
    if (uuid !== selectedSupplierUuid && orderItems.length > 0) {
      setPendingSupplierUuid(uuid);
      setShowChangeSupplierModal(true);
    } else {
      setSelectedSupplierUuid(uuid);
      setOrderItems([]);
      setCurrentPage(0);
      setManualSearchActive(false);
    }
  };

  const handleConfirmSupplierChange = () => {
    if (pendingSupplierUuid) {
      setSelectedSupplierUuid(pendingSupplierUuid);
      setCurrentPage(0);
      setManualSearchActive(false);
      setPendingSupplierUuid(null);
    } else {
      setSelectedSupplierUuid(null);
    }
    setOrderItems([]);
    loadProducts();
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

  const handlePrepareToSend = async () => {
    if (!selectedSupplierUuid) {
      setError('Selecciona un proveïdor');
      return;
    }

    if (orderItems.length === 0) {
      setError('Afegeix productes a la comanda');
      return;
    }

    const orderToCreate: CreateOrderRequest = {
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

    const result = await orderService.createOrder(orderToCreate);

    if (!result.success || !result.data) {
      setError(result.message || 'Error creant la comanda');
      return;
    }

    setOrderUuid(result.data.uuid);

    setError('');
    setShowSendModal(true);
  };

  const handleSendOrder = async (method: 'email' | 'whatsapp') => {
    setSuccessMessage('');

    if (!supplierUuidFromUrl) return;

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

    if (result) {
      navigate('/orders/new', {
        state: { successMessage: 'Comanda enviada correctament!' },
      });
    }
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const handleCancel = () => {
    navigate('/orders/new');
  };

  const breadcrumbItems = [
    { label: 'Comandes', path: '/orders/new' },
    { label: 'Nova comanda', active: true },
  ];

  const columnsForOrder = [
    { key: 'category' as const, label: 'Categoria', show: true },
    {
      key: 'volume' as const,
      label: 'Volum',
      render: (product: Product) => `${product.volume} ${product.unit}`,
      show: true,
    },
    {
      key: 'price' as const,
      label: 'Preu',
      render: (product: Product) => `${product.price}€`,
      show: true,
    },
  ];

  const totalAmount = orderItems.reduce((sum, item) => sum + item.total, 0);

  return (
    <div className="order-create-container form-container">
      <div className="container-fluid py-4">
        <PageHeader title="Nova comanda" breadcrumbItems={breadcrumbItems} />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}

        <div className="row">
          <div className="col-lg-8">
            <div className="card mb-4">
              <div className="card-body">
                <div className="row">
                  <div className="col-md-6 mb-3">
                    <Form.Group>
                      <Form.Label>Nom de la comanda</Form.Label>
                      <Form.Control
                        type="text"
                        value={orderName}
                        onChange={e => setOrderName(e.target.value)}
                        placeholder="Comanda..."
                      />
                    </Form.Group>
                  </div>
                  <div className="col-md-6 mb-3">
                    {isSupplierFromUrl ? (
                      <Form.Group>
                        <Form.Label>Proveïdor</Form.Label>
                        <Form.Control
                          type="text"
                          value={supplierName || '—'}
                          disabled
                        />
                      </Form.Group>
                    ) : (
                      <SupplierAutocomplete
                        value={selectedSupplierUuid || ''}
                        onChange={handleSupplierChange}
                        placeholder="Selecciona un proveïdor"
                        label="Proveïdor"
                        fetchSuppliers={fetchSuppliersWithCache}
                      />
                    )}
                  </div>
                </div>

                {!selectedSupplierUuid && (
                  <Alert
                    variant="info"
                    message="ℹ️ Selecciona un proveïdor per poder afegir productes a la comanda. Pots explorar el catàleg mentre tant."
                  />
                )}

                <SearchBar
                  onSearch={handleSearch}
                  onClear={handleClearSearch}
                  fetchSuppliers={fetchSuppliersWithCache}
                  supplierName={supplierName}
                  supplierUuid={selectedSupplierUuid || undefined}
                  placeholder="Cercar per nom, categoria..."
                />

                {isLoading ? (
                  <div className="text-center py-5">
                    <div className="spinner-border text-primary" role="status">
                      <span className="visually-hidden">Carregant...</span>
                    </div>
                  </div>
                ) : (
                  <>
                    <ProductTable
                      products={products}
                      columns={columnsForOrder}
                      selectable
                      showActions={false}
                      onProductClick={handleProductClick}
                      selectedProducts={selectedProductUuids}
                    />
                    <ProductCard
                      products={products}
                      fields={columnsForOrder.map(col => ({
                        key: col.key,
                        label: col.label,
                        render: col.render,
                        show: col.show,
                      }))}
                      selectable
                      showActions={false}
                      onProductClick={handleProductClick}
                      selectedProducts={selectedProductUuids}
                    />
                  </>
                )}

                {!isLoading && products.length > 0 && (
                  <Pagination
                    type="producte"
                    currentPage={currentPage + 1}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                    itemsPerPage={itemsPerPage}
                    totalItems={totalElements}
                    onItemsPerPageChange={handleItemsPerPageChange}
                  />
                )}

                {!isLoading && products.length === 0 && (
                  <div className="text-center py-4 text-muted">
                    {selectedSupplierUuid
                      ? "No s'han trobat productes per aquest proveïdor"
                      : "No s'han trobat productes"}
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className="col-lg-4">
            <div id="order-summary-section">
              <OrderSummary
                supplierName={supplierName || 'Proveïdor'}
                items={orderItems}
                notes={notes}
                onNotesChange={setNotes}
                onUpdateItem={handleUpdateItem}
                onRemoveItem={handleRemoveItem}
                onSubmit={handlePrepareToSend}
                onCancel={handleCancel}
                isSubmitting={isSending}
              />
            </div>
          </div>
        </div>
      </div>

      <OrderCreateScrollButton
        itemsCount={orderItems.length}
        totalAmount={totalAmount}
        targetId="order-summary-section"
      />

      {supplierName && supplierEmail && supplierPhone && (
        <SendOrderModal
          show={showSendModal}
          providerName={supplierName}
          totalPrice={`${totalAmount.toFixed(2)}€`}
          itemsCount={orderItems.length}
          email={supplierEmail}
          phone={supplierPhone}
          onClose={() => setShowSendModal(false)}
          onSend={handleSendOrder}
        />
      )}

      <ConfirmModal
        show={showChangeSupplierModal}
        title="Canviar proveïdor"
        message="Canviar el proveïdor eliminarà tots els productes seleccionats de la comanda."
        warning="Aquesta acció no es pot desfer"
        confirmText="Canviar proveïdor"
        cancelText="Cancel·lar"
        onClose={handleCancelSupplierChange}
        onConfirm={handleConfirmSupplierChange}
        variant="warning"
      />
    </div>
  );
};

export default OrderCreate;
