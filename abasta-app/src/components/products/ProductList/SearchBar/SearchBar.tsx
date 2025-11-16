import { useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { ProductSearchFilters } from '../../../../types/product.types';

interface SearchBarProps {
  onSearch: (filters: ProductSearchFilters, isAdvanced: boolean) => void;
  onClear: () => void;
}

const SearchBar = ({ onSearch, onClear }: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState<ProductSearchFilters>({
    query: '',
    name: '',
    category: '',
    minPrice: '',
    maxPrice: '',
    unit: '',
  });

  const handleBasicSearch = () => {
    onSearch(filters, false);
  };

  const handleAdvancedSearch = () => {
    onSearch(filters, true);
  };

  const handleClear = () => {
    const emptyFilters: ProductSearchFilters = {
      query: '',
      name: '',
      category: '',
      minPrice: '',
      maxPrice: '',
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
            placeholder="Cercar per nom, categoria..."
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
                  placeholder="Cercar per categoria"
                  value={filters.category}
                  onChange={e =>
                    setFilters({ ...filters, category: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={2}>
              <Form.Group>
                <Form.Label>Preu mínim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Mínim"
                  value={filters.minPrice}
                  onChange={e =>
                    setFilters({ ...filters, minPrice: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={2}>
              <Form.Group>
                <Form.Label>Preu màxim</Form.Label>
                <Form.Control
                  type="number"
                  placeholder="Màxim"
                  value={filters.maxPrice}
                  onChange={e =>
                    setFilters({ ...filters, maxPrice: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Unitat</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Ex: kg, l, unitats..."
                  value={filters.unit}
                  onChange={e =>
                    setFilters({ ...filters, unit: e.target.value })
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
