import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

import './ProductList.scss';

import { productService } from '../../../services/productService';
import type {
  AdvancedSearchParams,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  Product,
  SearchFilters,
} from '../../../types/product.types';

import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import ProductTable from './ProductTable/ProductTable';
import ProductCard from './ProductCard/ProductCard';
import DeleteModal from '../../common/DeleteModal/DeleteModal';
import SearchBar from './SearchBar/SearchBar';
import { supplierService } from '../../../services/supplierService';
import type { CachedSuppliersResult } from '../../../types/supplier.types';

const ProductList = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const supplierUuid = searchParams.get('supplier');

  const [supplierName, setSupplierName] = useState<string | null>(null);

  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
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

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [productToDelete, setProductToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const [suppliersCache, setSuppliersCache] = useState<
    Map<string, CachedSuppliersResult>
  >(new Map());
  const [suppliersCacheInitialized, setSuppliersCacheInitialized] =
    useState(false);

  useEffect(() => {
    const loadSupplierName = async () => {
      if (!supplierUuid) {
        setSupplierName(null);
        return;
      }

      try {
        const response = await supplierService.getSupplierByUuid(supplierUuid);
        setSupplierName(response.data?.name || null);
      } catch (err) {
        console.error('Error carregant el nom del proveïdor:', err);
        setSupplierName(null);
      }
    };

    loadSupplierName();
  }, [supplierUuid]);

  useEffect(() => {
    if (supplierUuid) {
      setCurrentFilters(prev => ({ ...prev, supplierUuid }));
    } else {
      setCurrentFilters(prev => ({ ...prev, supplierUuid: '' }));
    }
  }, [supplierUuid]);

  useEffect(() => {
    const loadForPage = async () => {
      if (manualSearchActive) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        await loadProducts();
      }
    };

    loadForPage();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, itemsPerPage]);

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
      if (supplierUuid) {
        response = await productService.getProductBySupplier(
          supplierUuid,
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

    console.log('entra', filtersParam);
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
      supplierUuid: '',
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

  const handleDeleteClick = (productUuid: string, productName: string) => {
    setProductToDelete({ uuid: productUuid, name: productName });
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!productToDelete) return;

    setIsDeleting(true);
    setError('');

    try {
      await productService.deleteProduct(productToDelete.uuid);

      setSuccessMessage(
        `Producte "${productToDelete.name}" eliminat correctament`
      );

      setShowDeleteModal(false);
      setProductToDelete(null);

      if (manualSearchActive) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        await loadProducts();
      }

      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al eliminar el producte';
      setError(errorMessage);
      console.error('Delete product error:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
    setProductToDelete(null);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const breadcrumbItems = [{ label: 'Productes', active: true }];

  return (
    <div className="supplier-list-container form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title={`Productes ${supplierName ? `- ${supplierName}` : ''}`}
          breadcrumbItems={breadcrumbItems}
          actions={
            <Button
              title="Nou Producte"
              onClick={() =>
                navigate(
                  `/products/new${supplierUuid ? `?supplier=${supplierUuid}` : ''}`
                )
              }
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
          supplierUuid={supplierUuid}
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
            <ProductTable products={products} onDelete={handleDeleteClick} />
            <ProductCard products={products} onDelete={handleDeleteClick} />
          </>
        )}

        {!isLoading && products.length > 0 && (
          <Pagination
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
          entityType="producte"
          itemName={productToDelete?.name || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default ProductList;
