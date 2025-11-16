import { useState, useEffect, useRef, useCallback } from 'react';
import { Form, Dropdown } from 'react-bootstrap';
import './SupplierAutocomplete.scss';

interface Supplier {
  uuid: string;
  name: string;
}

interface SupplierAutocompleteProps {
  value: string;
  onChange: (uuid: string, name: string) => void;
  placeholder?: string;
  label?: string;
  disabled?: boolean;
  isInvalid?: boolean;
  errorMessage?: string;
  fetchSuppliers: (
    page: number,
    query: string
  ) => Promise<{
    suppliers: Supplier[];
    hasMore: boolean;
  }>;
}

const SupplierAutocomplete = ({
  value,
  onChange,
  placeholder = 'Cercar proveïdor...',
  label = 'Proveïdor',
  disabled = false,
  isInvalid = false,
  errorMessage = '',
  fetchSuppliers,
}: SupplierAutocompleteProps) => {
  const [query, setQuery] = useState('');
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [selectedName, setSelectedName] = useState('');
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const observerTarget = useRef<HTMLDivElement>(null);
  const debounceTimer = useRef<number | null>(null);

  const loadSuppliers = useCallback(
    async (pageNum: number, searchQuery: string, append: boolean = false) => {
      if (loading) return;

      setLoading(true);
      try {
        const result = await fetchSuppliers(pageNum, searchQuery);

        setSuppliers(prev =>
          append ? [...prev, ...result.suppliers] : result.suppliers
        );
        setHasMore(result.hasMore);
      } catch (error) {
        console.error('Error loading suppliers:', error);
      } finally {
        setLoading(false);
      }
    },
    [fetchSuppliers, loading]
  );

  useEffect(() => {
    if (debounceTimer.current) {
      clearTimeout(debounceTimer.current);
    }

    debounceTimer.current = window.setTimeout(() => {
      setPage(1);
      loadSuppliers(1, query, false);
    }, 300);

    return () => {
      if (debounceTimer.current) {
        clearTimeout(debounceTimer.current);
      }
    };
  }, [query]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting && hasMore && !loading && showDropdown) {
          const nextPage = page + 1;
          setPage(nextPage);
          loadSuppliers(nextPage, query, true);
        }
      },
      { threshold: 1.0 }
    );

    const currentTarget = observerTarget.current;
    if (currentTarget) {
      observer.observe(currentTarget);
    }

    return () => {
      if (currentTarget) {
        observer.unobserve(currentTarget);
      }
    };
  }, [hasMore, loading, page, query, showDropdown, loadSuppliers]);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (value && suppliers.length > 0) {
      const supplier = suppliers.find(s => s.uuid === value);
      if (supplier) {
        setSelectedName(supplier.name);
      }
    } else if (!value) {
      setSelectedName('');
    }
  }, [value, suppliers]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newQuery = e.target.value;
    setQuery(newQuery);
    setShowDropdown(true);

    if (!newQuery && value) {
      onChange('', '');
      setSelectedName('');
    }
  };

  const handleSelectSupplier = (supplier: Supplier) => {
    onChange(supplier.uuid, supplier.name);
    setSelectedName(supplier.name);
    setQuery(supplier.name);
    setShowDropdown(false);
  };

  const handleInputFocus = () => {
    setShowDropdown(true);
    if (suppliers.length === 0) {
      loadSuppliers(1, query, false);
    }
  };

  const handleClear = () => {
    setQuery('');
    setSelectedName('');
    onChange('', '');
    setShowDropdown(false);
  };

  return (
    <Form.Group className="supplier-autocomplete" ref={dropdownRef}>
      {label && <Form.Label>{label}</Form.Label>}
      <div className="autocomplete-input-wrapper">
        <Form.Control
          type="text"
          placeholder={placeholder}
          value={query || selectedName}
          onChange={handleInputChange}
          onFocus={handleInputFocus}
          disabled={disabled}
          isInvalid={isInvalid}
          autoComplete="off"
        />
        {(query || selectedName) && !disabled && (
          <button
            type="button"
            className="clear-btn"
            onClick={handleClear}
            aria-label="Netejar"
          >
            <i className="bi bi-x-circle"></i>
          </button>
        )}
      </div>

      {isInvalid && errorMessage && (
        <Form.Control.Feedback type="invalid" className="d-block">
          {errorMessage}
        </Form.Control.Feedback>
      )}

      {showDropdown && !disabled && (
        <Dropdown.Menu show className="autocomplete-dropdown">
          {suppliers.length === 0 && !loading && (
            <div className="dropdown-item text-muted">
              No s'han trobat proveïdors
            </div>
          )}

          {suppliers.map(supplier => (
            <Dropdown.Item
              key={supplier.uuid}
              onClick={() => handleSelectSupplier(supplier)}
              active={supplier.uuid === value}
            >
              {supplier.name}
            </Dropdown.Item>
          ))}

          {loading && (
            <div className="dropdown-item text-center">
              <div className="spinner-border spinner-border-sm" role="status">
                <span className="visually-hidden">Carregant...</span>
              </div>
            </div>
          )}

          <div ref={observerTarget} style={{ height: '1px' }} />
        </Dropdown.Menu>
      )}
    </Form.Group>
  );
};

export default SupplierAutocomplete;
