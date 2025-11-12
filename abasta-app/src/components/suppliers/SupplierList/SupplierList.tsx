import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { supplierService } from '../../../services/supplierService';
import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import DeleteModal from '../../common/DeleteModal/DeleteModal';
import './SupplierList.scss';
import type {
  AdvancedSearchParams,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  SearchFilters,
  Supplier,
} from '../../../types/supplier.types';
import SearchBar from './SearchBar/SearchBar';
import SupplierTable from './SupplierTable/SupplierTable';
import SupplierCard from './SupplierCard/SupplierCard';

const SupplierList = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
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
    contactName: '',
    email: '',
    phone: '',
  });
  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [supplierToDelete, setSupplierToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    if (location.state && location.state.successMessage) {
      const message = location.state.successMessage as string;

      setSuccessMessage(message);

      navigate(location.pathname, { replace: true, state: {} });

      setTimeout(() => {
        setSuccessMessage('');
      }, 3000);
    }
  }, [location.state, location.pathname, navigate]);

  useEffect(() => {
    loadSuppliers();
  }, [currentPage, itemsPerPage]);

  const loadSuppliers = async () => {
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

        const response = await supplierService.getSuppliers(paginationParams);

        setSuppliers(response.data?.content || []);
        setTotalElements(response.data?.pageable.totalElements || 0);
        setTotalPages(response.data?.pageable.totalPages || 0);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al carregar els proveïdors';
      setError(errorMessage);
      console.error('Load suppliers error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const hasActiveFilters = (): boolean => {
    return !!(
      currentFilters.query ||
      currentFilters.name ||
      currentFilters.contactName ||
      currentFilters.email ||
      currentFilters.phone
    );
  };

  const performSearch = async (filters: SearchFilters, isAdvanced: boolean) => {
    try {
      let response;

      if (isAdvanced) {
        const filterParams: AdvancedSearchParams = {
          name: filters.name || undefined,
          contactName: filters.contactName || undefined,
          email: filters.email || undefined,
          phone: filters.phone || undefined,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };
        response = await supplierService.filterSuppliers(filterParams);
        setIsAdvancedSearch(true);
      } else {
        const searchParams: BasicSearchParams = {
          searchText: filters.query,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'name',
          sortDir: 'asc',
        };
        response = await supplierService.searchSuppliers(searchParams);
        setIsAdvancedSearch(false);
      }

      if (response.success && response.data) {
        const paginatedData = response.data as PaginatedResponse<Supplier>;
        setSuppliers(paginatedData.content);
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
      contactName: '',
      email: '',
      phone: '',
    };
    setCurrentFilters(emptyFilters);
    setCurrentPage(0);
    setIsAdvancedSearch(false);
    loadSuppliers();
  };

  const handleDeleteClick = (supplierUuid: string, supplierName: string) => {
    setSupplierToDelete({ uuid: supplierUuid, name: supplierName });
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!supplierToDelete) return;

    setIsDeleting(true);
    setError('');
    try {
      await supplierService.deleteSupplier(supplierToDelete.uuid);
      setSuccessMessage(
        `Proveïdor "${supplierToDelete.name}" eliminat correctament`
      );

      setShowDeleteModal(false);
      setSupplierToDelete(null);

      await loadSuppliers();

      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al eliminar el proveïdor';
      setError(errorMessage);
      console.error('Delete supplier error:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
    setSupplierToDelete(null);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const breadcrumbItems = [{ label: 'Proveïdors', active: true }];

  return (
    <div className="supplier-list-container form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title="Proveïdors"
          breadcrumbItems={breadcrumbItems}
          actions={
            <Button
              title="Nou Proveïdor"
              onClick={() => navigate('/suppliers/new')}
            />
          }
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {error && <Alert variant="danger" message={error} />}

        <SearchBar onSearch={handleSearch} onClear={handleClearSearch} />

        {isLoading && (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Carregant...</span>
            </div>
          </div>
        )}

        {!isLoading && (
          <>
            <SupplierTable suppliers={suppliers} onDelete={handleDeleteClick} />
            <SupplierCard suppliers={suppliers} onDelete={handleDeleteClick} />
          </>
        )}

        {!isLoading && suppliers.length > 0 && (
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
          entityType="proveïdor"
          itemName={supplierToDelete?.name || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default SupplierList;
