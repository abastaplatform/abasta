import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { supplierService } from '../../../services/supplierService';
import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import Pagination from '../../common/Pagination/Pagination';
import DeleteModal from '../../common/DeleteModal/DeleteModal';
import './SupplierList.scss';
import type { SearchFilters, Supplier } from '../../../types/supplier.types';
import SearchBar from './SearchBar/SearchBar';
import SupplierTable from './SupplierTable/SupplierTable';
import SupplierCard from './SupplierCard/SupplierCard';

const SupplierList = () => {
  const navigate = useNavigate();

  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [filteredSuppliers, setFilteredSuppliers] = useState<Supplier[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(20);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [supplierToDelete, setSupplierToDelete] = useState<{
    uuid: string;
    name: string;
  } | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    loadSuppliers();
  }, []);

  const loadSuppliers = async () => {
    setIsLoading(true);
    setError('');
    try {
      const response = await supplierService.getSuppliers();
      setSuppliers(response.data || []);
      setFilteredSuppliers(response.data || []);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al carregar els proveïdors';
      setError(errorMessage);
      console.error('Load suppliers error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSearch = (filters: SearchFilters) => {
    let filtered = [...suppliers];

    if (filters.query) {
      const q = filters.query.toLowerCase();
      filtered = filtered.filter(
        supplier =>
          supplier.name.toLowerCase().includes(q) ||
          supplier.contactName.toLowerCase().includes(q) ||
          supplier.email.toLowerCase().includes(q) ||
          supplier.phone.includes(q)
      );
    }

    if (filters.name) {
      filtered = filtered.filter(supplier =>
        supplier.name.toLowerCase().includes(filters.name.toLowerCase())
      );
    }
    if (filters.contactName) {
      filtered = filtered.filter(supplier =>
        supplier.contactName
          .toLowerCase()
          .includes(filters.contactName.toLowerCase())
      );
    }
    if (filters.email) {
      filtered = filtered.filter(supplier =>
        supplier.email.toLowerCase().includes(filters.email.toLowerCase())
      );
    }
    if (filters.phone) {
      filtered = filtered.filter(supplier =>
        supplier.phone.includes(filters.phone)
      );
    }

    setFilteredSuppliers(filtered);
    setCurrentPage(1);
  };

  const handleClearSearch = () => {
    setFilteredSuppliers(suppliers);
    setCurrentPage(1);
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

      const updatedSuppliers = suppliers.filter(
        s => s.uuid !== supplierToDelete.uuid
      );
      setSuppliers(updatedSuppliers);
      setFilteredSuppliers(
        filteredSuppliers.filter(s => s.uuid !== supplierToDelete.uuid)
      );

      setShowDeleteModal(false);
      setSupplierToDelete(null);

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

  const totalPages = Math.ceil(filteredSuppliers.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentSuppliers = filteredSuppliers.slice(startIndex, endIndex);

  const handlePageChange = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(1);
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
            <SupplierTable
              suppliers={currentSuppliers}
              onDelete={handleDeleteClick}
            />
            <SupplierCard
              suppliers={currentSuppliers}
              onDelete={handleDeleteClick}
            />
          </>
        )}

        {!isLoading && filteredSuppliers.length > 0 && (
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
            itemsPerPage={itemsPerPage}
            totalItems={filteredSuppliers.length}
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
