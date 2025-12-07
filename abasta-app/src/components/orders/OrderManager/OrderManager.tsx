import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

import './OrderManager.scss';

import type {
  AdvancedSearchParams,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  Product,
  SearchFilters,
} from '../../../types/product.types';
import type { CachedSuppliersResult } from '../../../types/supplier.types';

import PageHeader from '../../common/PageHeader/PageHeader';
import Alert from '../../common/Alert/Alert';
import SendOrderModal from '../SendOrderModal/SendOrderModal';
import DeleteModal from '../../common/DeleteModal/DeleteModal';

import { productService } from '../../../services/productService';
import { supplierService } from '../../../services/supplierService';
import SearchBar from '../../products/ProductList/SearchBar/SearchBar';
import ProductTable from '../../products/ProductList/ProductTable/ProductTable';
import OrderSummary from './OrderSummary/OrderSummary';
import Pagination from '../../common/Pagination/Pagination';
import Form from 'react-bootstrap/esm/Form';
import ProductCard from '../../products/ProductList/ProductCard/ProductCard';
import OrderCreateScrollButton from '../OrderCreate/OrderCreateScrollButton/OrderCreateScrollButton';
import ConfirmModal from '../../common/ConfirmModal/ConfirmModal';

import { useOrder } from '../../../hooks/useOrderService';
import type { FormMode } from '../../../hooks/useOrderService';

interface OrderManagerProps {
  mode: FormMode;
}

const OrderManager: React.FC<OrderManagerProps> = ({ mode }) => {
  const { uuid } = useParams<{ uuid: string }>();
  const navigate = useNavigate();

  const {
    isReadMode,
    canEdit,
    canSend,
    orderName,
    setOrderName,
    notes,
    setNotes,
    orderItems,
    selectedSupplierUuid,
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
    handleConfirmSupplierChange,
    handleCancelSupplierChange,
    showDeleteModal,
    setShowDeleteModal,
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
  } = useOrder(uuid, mode);

  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(false);

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

  const [suppliersCache, setSuppliersCache] = useState<
    Map<string, CachedSuppliersResult>
  >(new Map());
  const [suppliersCacheInitialized, setSuppliersCacheInitialized] =
    useState(false);

  const selectedProductUuids = new Set(
    orderItems.map(item => item.productUuid)
  );

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
    const loadForPage = async () => {
      if (manualSearchActive) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        await loadProducts();
      }
    };

    if (selectedSupplierUuid && canEdit) {
      loadForPage();
    }
  }, [currentPage, itemsPerPage, selectedSupplierUuid, canEdit]);

  const loadProducts = async () => {
    if (!selectedSupplierUuid) return;

    setIsLoading(true);
    setError('');

    try {
      const paginationParams: PaginationParams = {
        page: currentPage,
        size: itemsPerPage,
        sortBy: 'name',
        sortDir: 'asc',
      };

      const response = await productService.getProductBySupplier(
        selectedSupplierUuid,
        paginationParams
      );

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
    if (!selectedSupplierUuid || !canEdit) return;

    setIsLoading(true);
    setError('');

    try {
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
          supplierUuid: selectedSupplierUuid || undefined,
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
    if (!canEdit) return;

    setCurrentFilters(filters);
    setIsAdvancedSearch(isAdvanced);
    setManualSearchActive(true);
    setCurrentPage(0);
    await performSearch(filters, isAdvanced);
  };

  const handleClearSearch = () => {
    if (!canEdit) return;

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

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const columnsForOrderTable = [
    { key: 'name' as const, label: 'Nom', show: true },
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

  const columnsForOrderCard = [
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

  return (
    <div className="order-create-container form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title={pageTitle}
          breadcrumbItems={breadcrumbItems}
          actions={
            isReadMode ? (
              ''
            ) : (
              <div className="d-flex gap-2">
                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => navigate(`/orders/${uuid}`)}
                >
                  Veure detall
                </button>
                <button
                  type="button"
                  className="btn btn-outline-danger"
                  onClick={() => setShowDeleteModal(true)}
                >
                  Eliminar
                </button>
              </div>
            )
          }
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}
        {loadError && <Alert variant="danger" message={loadError} />}

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
                        disabled={!canEdit}
                      />
                    </Form.Group>
                  </div>
                  <div className="col-md-6 mb-3">
                    <Form.Group>
                      <Form.Label>Proveïdor</Form.Label>
                      <Form.Control
                        type="text"
                        value={supplierName || '—'}
                        disabled
                      />
                    </Form.Group>
                  </div>
                </div>

                {!selectedSupplierUuid && (
                  <Alert
                    variant="info"
                    message="Selecciona un proveïdor per poder afegir productes a la comanda."
                  />
                )}

                {canEdit && selectedSupplierUuid && (
                  <>
                    <SearchBar
                      key={selectedSupplierUuid || 'no-supplier'}
                      onSearch={handleSearch}
                      onClear={handleClearSearch}
                      fetchSuppliers={fetchSuppliersWithCache}
                      supplierName={supplierName}
                      supplierUuid={selectedSupplierUuid || undefined}
                      placeholder="Cercar per nom, categoria..."
                    />

                    {isLoading ? (
                      <div className="text-center py-5">
                        <div
                          className="spinner-border text-primary"
                          role="status"
                        >
                          <span className="visually-hidden">Carregant...</span>
                        </div>
                      </div>
                    ) : (
                      <>
                        <ProductTable
                          products={products}
                          columns={columnsForOrderTable}
                          selectable
                          showActions={false}
                          onProductClick={handleProductClick}
                          selectedProducts={selectedProductUuids}
                        />
                        <ProductCard
                          products={products}
                          fields={columnsForOrderCard.map(col => ({
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

                    {!isLoading &&
                      selectedSupplierUuid &&
                      products.length > 0 && (
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

                    {!isLoading &&
                      selectedSupplierUuid &&
                      products.length === 0 && (
                        <div className="text-center py-4 text-muted">
                          No s'han trobat productes per aquest proveïdor
                        </div>
                      )}
                  </>
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
                canSend={canSend}
                onSave={canEdit ? handleSave : undefined}
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

      <DeleteModal
        show={showDeleteModal}
        entityType="comanda"
        itemName={orderName}
        onClose={() => setShowDeleteModal(false)}
        onConfirm={handleDeleteConfirm}
        isDeleting={isDeleting}
      />
    </div>
  );
};

export default OrderManager;
