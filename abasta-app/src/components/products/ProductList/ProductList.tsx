import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { productService } from '../../../services/productService';
import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import type { PaginationParams, Product } from '../../../types/product.types';

const ProductList = () => {
  const navigate = useNavigate();

  const [searchParams] = useSearchParams();
  const supplierUuid = searchParams.get('supplier');

  const [products, setProducts] = useState<Product[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    loadProducts();
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

      const response = supplierUuid
        ? await productService.getProductBySupplier(
            supplierUuid,
            paginationParams
          )
        : await productService.getProducts(paginationParams);
      console.log('Product response:', response);
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
          title="Productes"
          breadcrumbItems={breadcrumbItems}
          actions={
            <Button
              title="Nou Producte"
              onClick={() => navigate('/products/new')}
            />
          }
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}

        {isLoading && (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Carregant...</span>
            </div>
          </div>
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
      </div>
    </div>
  );
};

export default ProductList;
