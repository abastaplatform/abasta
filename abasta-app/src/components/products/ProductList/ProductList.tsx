import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

import { productService } from '../../../services/productService';
import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import DeleteModal from '../../common/DeleteModal/DeleteModal';

import './ProductList.scss';

import type {
  AdvancedProductSearchParams,
  BasicProductSearchParams,
  PaginatedResponse,
  PaginationParams,
  Product,
} from '../../../types/product.types';

import SearchBar from './SearchBar/SearchBar';
import ProductTable from './ProductTable/ProductTable';
import ProductCard from './ProductCard/ProductCard';

const ProductList = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [filters, setFilters] = useState({
    query: '',
    name: '',
    category: '',
    minPrice: '',
    maxPrice: '',
    unit: '',
  });

  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [productToDelete, setProductToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);

  const [isDeleting, setIsDeleting] = useState(false);

  // 🟢 Missatge d’èxit després de crear/editar
  useEffect(() => {
    if (location.state?.successMessage) {
      setSuccessMessage(location.state.successMessage);
      navigate(location.pathname, { replace: true, state: {} });
      setTimeout(() => setSuccessMessage(''), 3000);
    }
  }, [location]);

  // 🔄 Carregar productes cada vegada que es canvia la pàgina
  useEffect(() => {
    loadProducts();
  }, [currentPage, itemsPerPage]);

  const hasFilters = () =>
    !!(
      filters.query ||
      filters.name ||
      filters.category ||
      filters.minPrice ||
      filters.maxPrice ||
      filters.unit
    );

  // 📌 Carregar productes (llistat + filtres)
  const loadProducts = async () => {
    setIsLoading(true);
    setError('');

    try {
      let response;

      // 🔍 Si hi ha filtres activats → fer cerca
      if (hasFilters()) {
        await performSearch(filters, isAdvancedSearch);
        return;
      }

      // 📌 Llistat normal de TOTS els productes
      const params: PaginationParams = {
        page: currentPage,
        size: itemsPerPage,
        sortBy: 'name',
        sortDir: 'asc',
      };

      response = await productService.getAllProducts(params);

      if (response.success && response.data) {
        const p = response.data;
        setProducts(p.content);
        setTotalElements(p.pageable.totalElements);
        setTotalPages(p.pageable.totalPages);
      }
    } catch {
      setError('Error al carregar productes');
    } finally {
      setIsLoading(false);
    }
  };

  // 🔍 Cerca (bàsica o avançada)
  const performSearch = async (data: typeof filters, advanced: boolean) => {
    try {
      let response;

      if (advanced) {
        const params: AdvancedProductSearchParams = {
          name: data.name || undefined,
          category: data.category || undefined,
          minPrice: data.minPrice ? Number(data.minPrice) : undefined,
          maxPrice: data.maxPrice ? Number(data.maxPrice) : undefined,
          unit: data.unit || undefined,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };

        response = await productService.filterProducts(params);
        setIsAdvancedSearch(true);
      } else {
        const params: BasicProductSearchParams = {
          searchText: data.query,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };

        response = await productService.searchProducts(params);
        setIsAdvancedSearch(false);
      }

      if (response.success && response.data) {
        const p = response.data as PaginatedResponse<Product>;
        setProducts(p.content);
        setTotalElements(p.pageable.totalElements);
        setTotalPages(p.pageable.totalPages);
      }
    } catch {
      setError('Error al cercar productes');
    }
  };

  // 🔍 Acció del botó "Cercar"
  const handleSearch = async (data: typeof filters, advanced: boolean) => {
    setFilters(data);
    setCurrentPage(0);
    setIsLoading(true);
    await performSearch(data, advanced);
    setIsLoading(false);
  };

  // 🔁 Netejar filtres
  const handleClearSearch = () => {
    const empty = {
      query: '',
      name: '',
      category: '',
      minPrice: '',
      maxPrice: '',
      unit: '',
    };

    setFilters(empty);
    setCurrentPage(0);
    setIsAdvancedSearch(false);
    loadProducts();
  };

  // 🗑️ Eliminar producte
  const handleDeleteClick = (uuid: string, name: string) => {
    setProductToDelete({ uuid, name });
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!productToDelete) return;

    setIsDeleting(true);

    try {
      await productService.deleteProduct(productToDelete.uuid);
      setSuccessMessage(`Producte "${productToDelete.name}" eliminat`);
      await loadProducts();
    } catch {
      setError('Error al eliminar producte');
    } finally {
      setShowDeleteModal(false);
      setProductToDelete(null);
      setIsDeleting(false);
    }
  };

  return (
    <div className="product-list-container form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title="Productes"
          breadcrumbItems={[{ label: 'Productes', active: true }]}
          actions={<Button title="Nou Producte" onClick={() => navigate('/products/new')} />}
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}

        <SearchBar onSearch={handleSearch} onClear={handleClearSearch} />
<button onClick={() => navigate(`/products/edit/efed51aa-c1bf-4379-a200-cf201094a175`)}>
  Editar
</button>
<button onClick={() => navigate(`/products/efed51aa-c1bf-4379-a200-cf201094a175`)}>
  Detall
</button>
        {isLoading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary"></div>
          </div>
        ) : (
          <>
            <ProductTable products={products} onDelete={handleDeleteClick} />
            <ProductCard products={products} onDelete={handleDeleteClick} />
          </>
        )}

        {!isLoading && products.length > 0 && (
          <Pagination
            currentPage={currentPage + 1}
            totalPages={totalPages}
            onPageChange={(p) => setCurrentPage(p - 1)}
            itemsPerPage={itemsPerPage}
            totalItems={totalElements}
            onItemsPerPageChange={(n) => {
              setItemsPerPage(n);
              setCurrentPage(0);
            }}
          />
        )}

        <DeleteModal
          show={showDeleteModal}
          entityType="producte"
          itemName={productToDelete?.name || ''}
          onClose={() => setShowDeleteModal(false)}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default ProductList;
