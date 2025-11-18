import { useEffect, useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { SearchFilters } from '../../../../types/product.types';
import SupplierAutocomplete from '../../../common/SupplierAutocomplete/SupplierAutocomplete';
import type { CachedSuppliersResult } from '../../../../types/supplier.types';

interface SearchBarProps {
  onSearch: (filters: SearchFilters, isAdvanced: boolean) => void;
  onClear: () => void;
  fetchSuppliers: (
    page: number,
    query: string
  ) => Promise<CachedSuppliersResult>;
  supplierUuid?: string | null;
  supplierName?: string | null;
  placeholder?: string;
}

const SearchBar = ({
  onSearch,
  onClear,
  fetchSuppliers,
  supplierUuid,
  supplierName,
  placeholder = 'Cercar un producte',
}: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    name: '',
    category: '',
    minPrice: null,
    maxPrice: null,
    supplierUuid: '',
    volume: null,
    unit: '',
  });

  useEffect(() => {
    if (supplierUuid) {
      setFilters(prev => ({ ...prev, supplierUuid }));
    }
  }, [supplierUuid]);

  const handleBasicSearch = () => {
    onSearch(filters, false);
  };

  const handleAdvancedSearch = () => {
    onSearch(filters, true);
  };

  const handleClear = () => {
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
    setFilters(emptyFilters);
    onClear();
  };

  const handleQueryChange = (value: string) => {
    setFilters({ ...filters, query: value });
  };

  return (
    <div className="search-bar-container">
      <div className="search-bar-basic">
        <div className="search-input-group">
          <Form.Control
            type="text"
            placeholder={placeholder}
            value={filters.query}
            onChange={e => handleQueryChange(e.target.value)}
            onKeyDown={e => {
              if (e.key === 'Enter') {
                handleBasicSearch();
              }
            }}
            className="search-input"
          />
          <button
            className="btn btn-outline-secondary search-btn"
            onClick={handleBasicSearch}
          >
            <i className="bi bi-search me-2"></i>
            Cercar
          </button>
        </div>

        <button
          className="btn btn-outline-secondary advanced-btn"
          onClick={() => setShowAdvanced(!showAdvanced)}
        >
          <i className="bi bi-sliders me-2"></i>
          Cerca avançada
        </button>
      </div>

      <Collapse in={showAdvanced}>
        <div className="search-bar-advanced">
          <Row className="g-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Nom del producte</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per nom del producte"
                  value={filters.name}
                  onChange={e =>
                    setFilters({ ...filters, name: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Categoria</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per cetegoria"
                  value={filters.category}
                  onChange={e =>
                    setFilters({ ...filters, category: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              {supplierUuid ? (
                <Form.Group>
                  <Form.Label>Proveïdor</Form.Label>
                  <Form.Control
                    type="text"
                    value={supplierName || '—'}
                    disabled
                  />
                </Form.Group>
              ) : (
                <SupplierAutocomplete
                  value={filters.supplierUuid}
                  onChange={(uuid: string) =>
                    setFilters(prev => ({ ...prev, supplierUuid: uuid }))
                  }
                  placeholder={
                    supplierUuid
                      ? supplierName || 'Proveïdor seleccionat'
                      : 'Cercar per proveïdor'
                  }
                  label="Proveïdor"
                  fetchSuppliers={fetchSuppliers}
                  disabled={!!supplierUuid}
                  readOnlyValue={supplierName || undefined}
                />
              )}
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Volum</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per volum"
                  value={filters.volume || ''}
                  onChange={e =>
                    setFilters({ ...filters, volume: Number(e.target.value) })
                  }
                />
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Unitat</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per unitat"
                  value={filters.unit}
                  onChange={e =>
                    setFilters({ ...filters, unit: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Preu mínim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per preu mínim"
                  value={filters.minPrice || ''}
                  onChange={e =>
                    setFilters({ ...filters, minPrice: Number(e.target.value) })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Preu màxim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per preu màxim"
                  value={filters.maxPrice || ''}
                  onChange={e =>
                    setFilters({ ...filters, maxPrice: Number(e.target.value) })
                  }
                />
              </Form.Group>
            </Col>
          </Row>

          <div className="advanced-actions">
            <Button
              title="Netejar filtres"
              variant="outline"
              onClick={handleClear}
            />
            <Button title="Cercar" onClick={handleAdvancedSearch} />
          </div>
        </div>
      </Collapse>
    </div>
  );
};

export default SearchBar;
