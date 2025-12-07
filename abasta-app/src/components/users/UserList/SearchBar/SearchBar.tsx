import { useState } from 'react';
import { Form, Collapse, Row, Col } from 'react-bootstrap';

import './SearchBar.scss';
import Button from '../../../common/Button/Button';
import type { SearchFilters } from '../../../../types/user.types';

interface SearchBarProps {
  onSearch: (filters: SearchFilters, isAdvanced: boolean) => void;
  onClear: () => void;
}

const SearchBar = ({ onSearch, onClear }: SearchBarProps) => {
  const [showAdvanced, setShowAdvanced] = useState(false);
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    role: null,
    isActive: null,
    emailVerified: null,
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
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      role: 'USER',
      isActive: null,
      emailVerified: null,
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
            placeholder="Cercar per nom, cognoms, telèfon..."
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
                <Form.Label>Nom</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per nom"
                  value={filters.firstName}
                  onChange={e =>
                    setFilters({ ...filters, firstName: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Cognoms</Form.Label>
                <Form.Control
                  type="text"
                  placeholder="Cercar per cognoms"
                  value={filters.lastName}
                  onChange={e =>
                    setFilters({ ...filters, lastName: e.target.value })
                  }
                />
              </Form.Group>
            </Col>

            <Col md={4}>
              <Form.Group>
                <Form.Label>Rol</Form.Label>
                <Form.Select
                  value={filters.role === null ? '' : filters.role}
                  onChange={e =>
                    setFilters({
                      ...filters,
                      role: e.target.value as 'ADMIN' | 'USER',
                    })
                  }
                >
                  <option value="">-</option>
                  <option value="ADMIN">Admin</option>
                  <option value="USER">User</option>
                </Form.Select>
              </Form.Group>
            </Col>

            <Col md={3}>
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

            <Col md={3}>
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

            <Col md={3}>
              <Form.Group>
                <Form.Label>Estat</Form.Label>
                <Form.Select
                  value={
                    filters.isActive === null ? '' : filters.isActive.toString()
                  }
                  onChange={e => {
                    const value = e.target.value;
                    setFilters({
                      ...filters,
                      isActive: value === '' ? null : value === 'true',
                    });
                  }}
                >
                  <option value="">-</option>
                  <option value="true">Actiu</option>
                  <option value="false">Inactiu</option>
                </Form.Select>
              </Form.Group>
            </Col>

            <Col md={3}>
              <Form.Group>
                <Form.Label>Email verificat</Form.Label>
                <Form.Select
                  value={
                    filters.emailVerified === null
                      ? ''
                      : filters.emailVerified.toString()
                  }
                  onChange={e => {
                    const value = e.target.value;
                    setFilters({
                      ...filters,
                      emailVerified: value === '' ? null : value === 'true',
                    });
                  }}
                >
                  <option value="">-</option>
                  <option value="true">Sí</option>
                  <option value="false">No</option>
                </Form.Select>
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
