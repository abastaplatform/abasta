import { useEffect, useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { OrderSearchFilters } from '../../../../types/order.types';
import SupplierAutocomplete from '../../../common/SupplierAutocomplete/SupplierAutocomplete';
import type { CachedSuppliersResult } from '../../../../types/supplier.types';

interface SearchBarProps {
  onSearch: (filters: OrderSearchFilters, isAdvanced: boolean) => void;
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
  placeholder = 'Cercar una comanda',
}: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);

  const [filters, setFilters] = useState<OrderSearchFilters>({
    query: '',
    name: '',
    notes: '',
    status: '',
    supplierUuid: '',
    minAmount: null,
    maxAmount: null,
    deliveryDateFrom: null,
    deliveryDateTo: null,
    createdAtFrom: null,
    createdAtTo: null,
    updatedAtFrom: null,
    updatedAtTo: null,
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
    const emptyFilters: OrderSearchFilters = {
      query: '',
      name: '',
      notes: '',
      status: '',
      supplierUuid: '',
      minAmount: null,
      maxAmount: null,
      deliveryDateFrom: null,
      deliveryDateTo: null,
      createdAtFrom: null,
      createdAtTo: null,
      updatedAtFrom: null,
      updatedAtTo: null,
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
            type="button"
          >
            <i className="bi bi-search me-2"></i>
            <span className="search-btn-text">Cercar</span>
          </button>
        </div>

        <button
          className="btn btn-outline-secondary advanced-btn"
          onClick={() => setShowAdvanced(!showAdvanced)}
          type="button"
        >
          <i className="bi bi-sliders me-2"></i>
          <span className="advanced-btn-text">Cerca avançada</span>
        </button>
      </div>

      <Collapse in={showAdvanced}>
        <div className="search-bar-advanced">
          <Row className="g-3">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Nom de la comanda</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per nom de la comanda"
                  value={filters.name}
                  onChange={e =>
                    setFilters({ ...filters, name: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Notes</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per notes"
                  value={filters.notes}
                  onChange={e =>
                    setFilters({ ...filters, notes: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Estat</Form.Label>
                <Form.Select
                  value={filters.status}
                  onChange={e =>
                    setFilters({ ...filters, status: e.target.value })
                  }
                >
                  <option value="">Tots</option>
                  <option value="PENDING">Pendent</option>
                  <option value="SENT">Enviada</option>
                  <option value="CONFIRMED">Confirmada</option>
                  <option value="REJECTED">Rebutjada</option>
                  <option value="COMPLETED">Completada</option>
                  <option value="CANCELLED">Cancel·lada</option>
                </Form.Select>
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

            <Col md={4}>
              <Form.Group>
                <Form.Label>Import mínim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per import mínim"
                  value={filters.minAmount ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      minAmount: e.target.value
                        ? Number(e.target.value)
                        : null,
                    })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Import màxim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Cercar per import màxim"
                  value={filters.maxAmount ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      maxAmount: e.target.value
                        ? Number(e.target.value)
                        : null,
                    })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Entrega des de</Form.Label>
                <Form.Control
                  type="date"
                  value={filters.deliveryDateFrom ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      deliveryDateFrom: e.target.value || null,
                    })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Entrega fins a</Form.Label>
                <Form.Control
                  type="date"
                  value={filters.deliveryDateTo ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      deliveryDateTo: e.target.value || null,
                    })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Creada des de</Form.Label>
                <Form.Control
                  type="date"
                  value={filters.createdAtFrom ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      createdAtFrom: e.target.value || null,
                    })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Creada fins a</Form.Label>
                <Form.Control
                  type="date"
                  value={filters.createdAtTo ?? ''}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      createdAtTo: e.target.value || null,
                    })
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
