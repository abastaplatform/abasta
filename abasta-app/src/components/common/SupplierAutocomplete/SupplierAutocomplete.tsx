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
  readOnlyValue?: string;
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
  readOnlyValue,
  fetchSuppliers,
}: SupplierAutocompleteProps) => {
  const [inputValue, setInputValue] = useState('');
  const [query, setQuery] = useState('');

  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [showDropdown, setShowDropdown] = useState(false);

  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [hasLoadedOnce, setHasLoadedOnce] = useState(false);

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
        setHasLoadedOnce(true);
      } catch (error) {
        console.error('Error loading suppliers:', error);
      } finally {
        setLoading(false);
      }
    },
    [fetchSuppliers, loading]
  );

  useEffect(() => {
    if (!hasLoadedOnce) return;

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
  }, [query, hasLoadedOnce, loadSuppliers]);

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
        setInputValue(supplier.name);
      }
    } else if (!value) {
      setInputValue('');
    }
  }, [value, suppliers]);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newQuery = e.target.value;

    setInputValue(newQuery);
    setQuery(newQuery);
    setShowDropdown(true);

    if (!newQuery && value) {
      onChange('', '');
    }
  };

  const handleSelectSupplier = (supplier: Supplier) => {
    onChange(supplier.uuid, supplier.name);

    setInputValue(supplier.name);

    setQuery('');

    setShowDropdown(false);
  };

  const handleInputFocus = () => {
    setShowDropdown(true);

    if (!hasLoadedOnce) {
      loadSuppliers(1, '', false);
    }
  };

  const handleClear = () => {
    setInputValue('');
    setQuery('');
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
          value={inputValue || (disabled && readOnlyValue ? readOnlyValue : '')}
          onChange={handleInputChange}
          onFocus={handleInputFocus}
          disabled={disabled}
          isInvalid={isInvalid}
          autoComplete="off"
        />
        {inputValue && !disabled && (
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
          {suppliers.length === 0 && !loading && hasLoadedOnce && (
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
