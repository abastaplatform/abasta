import { useEffect, useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { SearchFilters } from '../../../../types/product.types';
import { supplierService } from '../../../../services/supplierService';
import SupplierAutocomplete from '../../../common/SupplierAutocomplete/SupplierAutocomplete';

interface SearchBarProps {
  onSearch: (filters: SearchFilters, isAdvanced: boolean) => void;
  onClear: () => void;
  supplierUuid?: string | null;
  supplierName?: string | null;
}

const SearchBar = ({
  onSearch,
  onClear,
  supplierUuid,
  supplierName,
}: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    name: '',
    category: '',
    minPrice: 0,
    maxPrice: 0,
    supplierUuid: '',
    volume: 0,
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
      minPrice: 0,
      maxPrice: 0,
      supplierUuid: '',
      volume: 0,
      unit: '',
    };
    setFilters(emptyFilters);
    onClear();
  };

  const handleQueryChange = (value: string) => {
    setFilters({ ...filters, query: value });
  };

  const handleSupplierChange = (uuid: string) => {
    setFilters({ ...filters, supplierUuid: uuid });
  };

  const fetchSuppliers = async (page: number, query: string) => {
    return await supplierService.getSuppliersForAutocomplete(page, query);
  };

  return (
    <div className="search-bar-container">
      <div className="search-bar-basic">
        <div className="search-input-group">
          <Form.Control
            type="text"
            placeholder="Cercar per nom, categoria o proveïdor..."
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
                  onChange={handleSupplierChange}
                  placeholder="Cercar per proveïdor"
                  label="Proveïdor"
                  fetchSuppliers={fetchSuppliers}
                />
              )}
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Volum</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per volum"
                  value={filters.volume || 0}
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
                  type="number"
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
                  value={filters.minPrice || 0}
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
                  placeholder="Cercar per telèfon"
                  value={filters.maxPrice || 0}
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
