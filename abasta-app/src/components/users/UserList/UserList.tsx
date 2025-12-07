import { useLocation, useNavigate } from 'react-router-dom';
import Button from '../../common/Button/Button';
import PageHeader from '../../common/PageHeader/PageHeader';
import Alert from '../../common/Alert/Alert';
import { useEffect, useState } from 'react';
import type {
  AdvancedSearchParams,
  BasicSearchParams,
  PaginatedResponse,
  PaginationParams,
  SearchFilters,
  User,
} from '../../../types/user.types';
import { userService } from '../../../services/userService';
import SearchBar from './SearchBar/SearchBar';
import UserTable from './UserTable/UserTable';
import UserCard from './UserCard/UserCard';
import Pagination from '../../common/Pagination/Pagination';
import DeleteModal from '../../common/DeleteModal/DeleteModal';

const UserList = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const [currentPage, setCurrentPage] = useState(0);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  const [totalElements, setTotalElements] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [currentFilters, setCurrentFilters] = useState<SearchFilters>({
    query: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: null,
    isActive: null,
    emailVerified: null,
  });
  const [isAdvancedSearch, setIsAdvancedSearch] = useState(false);

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [userToDelete, setUserToDelete] = useState<{
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
    loadUsers();
  }, [currentPage, itemsPerPage]);

  const loadUsers = async () => {
    setIsLoading(true);
    setError('');
    try {
      if (hasActiveFilters()) {
        await performSearch(currentFilters, isAdvancedSearch);
      } else {
        const paginationParams: PaginationParams = {
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'firstName',
          sortDir: 'asc',
        };

        const response = await userService.getUsers(paginationParams);

        setUsers(response.data?.content || []);
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
      currentFilters.firstName ||
      currentFilters.lastName ||
      currentFilters.email ||
      currentFilters.phone ||
      currentFilters.isActive ||
      currentFilters.role ||
      currentFilters.emailVerified
    );
  };

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const performSearch = async (filters: SearchFilters, isAdvanced: boolean) => {
    try {
      let response;

      if (isAdvanced) {
        const filterParams: AdvancedSearchParams = {
          firstName: filters.firstName || undefined,
          lastName: filters.lastName || undefined,
          email: filters.email || undefined,
          phone: filters.phone || undefined,
          isActive: filters.isActive !== null ? filters.isActive : undefined,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'firstName',
          sortDir: 'asc',
        };
        response = await userService.filterUsers(filterParams);
        setIsAdvancedSearch(true);
      } else {
        const searchParams: BasicSearchParams = {
          searchText: filters.query,
          page: currentPage,
          size: itemsPerPage,
          sortBy: 'firstName',
          sortDir: 'asc',
        };
        response = await userService.searchUsers(searchParams);
        setIsAdvancedSearch(false);
      }

      if (response.success && response.data) {
        const paginatedData = response.data as PaginatedResponse<User>;
        setUsers(paginatedData.content);
        setTotalElements(paginatedData.pageable.totalElements);
        setTotalPages(paginatedData.pageable.totalPages);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al cercar usuaris';
      setError(errorMessage);
      console.error('Search users error:', err);
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
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      role: null,
      isActive: null,
      emailVerified: null,
    };
    setCurrentFilters(emptyFilters);
    setCurrentPage(0);
    setIsAdvancedSearch(false);
    loadUsers();
  };

  const handleDeleteClick = (userUuid: string, userName: string) => {
    setUserToDelete({ uuid: userUuid, name: userName });
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!userToDelete) return;

    setIsDeleting(true);
    setError('');
    try {
      await userService.deleteUser(userToDelete.uuid);
      setSuccessMessage(`Usuari "${userToDelete.name}" eliminat correctament`);

      setShowDeleteModal(false);
      setUserToDelete(null);

      await loadUsers();

      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : "Error al eliminar l'usuari";
      setError(errorMessage);
      console.error('Delete user error:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
    setUserToDelete(null);
  };

  const handlePageChange = (page: number) => {
    setCurrentPage(page - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const handleItemsPerPageChange = (items: number) => {
    setItemsPerPage(items);
    setCurrentPage(0);
  };

  const breadcrumbItems = [{ label: 'Usuaris', active: true }];

  return (
    <div className="user-list-container form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title="Usuaris"
          breadcrumbItems={breadcrumbItems}
          actions={
            <Button title="Nou usuari" onClick={() => navigate('/users/new')} />
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
            <UserTable users={users} onDelete={handleDeleteClick} />
            <UserCard users={users} onDelete={handleDeleteClick} />
          </>
        )}

        {!isLoading && users.length > 0 && (
          <Pagination
            type="usuari"
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
          entityType="usuari"
          itemName={userToDelete?.name || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default UserList;
