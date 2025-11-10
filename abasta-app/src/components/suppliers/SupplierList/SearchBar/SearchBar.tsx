import { useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { SearchFilters } from '../../../../types/supplier.types';

interface SearchBarProps {
  onSearch: (filters: SearchFilters, isAdvanced: boolean) => void;
  onClear: () => void;
}

const SearchBar = ({ onSearch, onClear }: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    name: '',
    contactName: '',
    email: '',
    phone: '',
  });

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
      contactName: '',
      email: '',
      phone: '',
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
            placeholder="Cercar per nom, contacte, email o telèfon..."
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
            <Col md={6}>
              <Form.Group>
                <Form.Label>Nom del proveïdor</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per nom del proveïdor"
                  value={filters.name}
                  onChange={e =>
                    setFilters({ ...filters, name: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Nom de contacte</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per nom de contacte"
                  value={filters.contactName}
                  onChange={e =>
                    setFilters({ ...filters, contactName: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Correu electrònic</Form.Label>
                <Form.Control
                  type="email"
                  placeholder="Cercar per email"
                  value={filters.email}
                  onChange={e =>
                    setFilters({ ...filters, email: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={6}>
              <Form.Group>
                <Form.Label>Telèfon</Form.Label>
                <Form.Control
                  type="tel"
                  placeholder="Cercar per telèfon"
                  value={filters.phone}
                  onChange={e =>
                    setFilters({ ...filters, phone: e.target.value })
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
