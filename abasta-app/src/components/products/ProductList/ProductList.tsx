import { useEffect, useState } from 'react';
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
    minPrice: 0,
    maxPrice: 0,
    volume: 0,
    unit: '',
  });
  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [productToDelete, setProductToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

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
    loadProducts();
  }, [currentPage, itemsPerPage]);

  useEffect(() => {
    if (supplierUuid) {
      setCurrentFilters({ ...currentFilters, supplierUuid });
    }
  }, [supplierUuid]);

  const loadProducts = async () => {
    setIsLoading(true);
    setError('');
    try {
      if (hasActiveFilters()) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        const paginationParams: PaginationParams = {
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };
        const response = supplierUuid
          ? await productService.getProductBySupplier(
              supplierUuid,
              paginationParams
            )
          : await productService.getProducts(paginationParams);
        setProducts(response.data?.content || []);
        setTotalElements(response.data?.pageable.totalElements || 0);
        setTotalPages(response.data?.pageable.totalPages || 0);
      }
    } catch (err) {
      const message =
        err instanceof Error ? err.message : 'Error al carregar els productes.';
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  const hasActiveFilters = (): boolean => {
    return !!(
      currentFilters.query ||
      currentFilters.name ||
      currentFilters.supplierUuid ||
      currentFilters.category ||
      currentFilters.minPrice ||
      currentFilters.maxPrice ||
      currentFilters.volume ||
      currentFilters.unit
    );
  };

  const performSearch = async (filters: SearchFilters, isAdvanced: boolean) => {
    try {
      let response;

      if (isAdvanced) {
        const filterParams: AdvancedSearchParams = {
          name: filters.name || undefined,
          supplierUuid: filters.supplierUuid || undefined,
          category: filters.category || undefined,
          minPrice: filters.minPrice || undefined,
          maxPrice: filters.maxPrice || undefined,
          volume: filters.volume || undefined,
          unit: filters.unit || undefined,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };
        response = await productService.filterProducts(filterParams);
        setIsAdvancedSearch(true);
      } else {
        const searchParams: BasicSearchParams = {
          searchText: filters.query,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };
        response = await productService.searchProducts(searchParams);
        setIsAdvancedSearch(false);
      }

      if (response.success && response.data) {
        const paginatedData = response.data as PaginatedResponse<Product>;
        setProducts(paginatedData.content);
        setTotalElements(paginatedData.pageable.totalElements);
        setTotalPages(paginatedData.pageable.totalPages);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al cercar proveïdors';
      setError(errorMessage);
      console.error('Search suppliers error:', err);
    }
  };

  const handleSearch = async (
    filters: SearchFilters,
    isAdvanced: boolean = false
  ) => {
    setCurrentFilters(filters);
    setCurrentPage(0);
    setIsLoading(true);
    await performSearch(filters, isAdvanced);
    setIsLoading(false);
  };

  const handleClearSearch = () => {
    const emptyFilters: SearchFilters = {
      query: '',
      name: '',
      category: '',
      minPrice: 0,
      maxPrice: 0,
      supplierUuid: '',
      volume: 0,
      unit: '',
    };
    setCurrentFilters(emptyFilters);
    setCurrentPage(0);
    setIsAdvancedSearch(false);
    loadProducts();
  };

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

      await loadProducts();

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
                  `/products/new ${supplierUuid ? `?supplier=${supplierUuid}` : ''}`
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
          supplierName={'test'}
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
