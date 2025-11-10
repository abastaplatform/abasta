import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Row, Col, Badge } from 'react-bootstrap';
import { supplierService } from '../../services/supplierService';
import PageHeader from '../../components/common/PageHeader/PageHeader';
import Button from '../../components/common/Button/Button';
import Alert from '../../components/common/Alert/Alert';
import type { Supplier } from '../../types/supplier.types';

const SupplierDetailPage = () => {
  const { uuid } = useParams<{ uuid: string }>();
  const navigate = useNavigate();

  const [supplier, setSupplier] = useState<Supplier | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (uuid) {
      loadSupplier(uuid);
    }
  }, [uuid]);

  const loadSupplier = async (supplierUuid: string) => {
    setIsLoading(true);
    setError('');
    try {
      const response = await supplierService.getSupplierByUuid(supplierUuid);
      if (response.success && response.data) {
        setSupplier(response.data);
      }
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al carregar el proveïdor';
      setError(errorMessage);
      console.error('Load supplier error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = () => {
    navigate(`/suppliers/edit/${uuid}`);
  };

  const handleBack = () => {
    navigate('/suppliers');
  };

  const breadcrumbItems = [
    { label: 'Proveïdors', path: '/suppliers' },
    { label: supplier?.name || 'Detall', active: true },
  ];

  if (isLoading) {
    return (
      <div className="container-fluid py-4">
        <div className="text-center py-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Carregant...</span>
          </div>
        </div>
      </div>
    );
  }

  if (error || !supplier) {
    return (
      <div className="container-fluid py-4">
        <PageHeader
          title="Detall del Proveïdor"
          breadcrumbItems={breadcrumbItems}
        />
        <Alert variant="danger" message={error || 'Proveïdor no trobat'} />
        <Button title="Tornar" variant="outline" onClick={handleBack} />
      </div>
    );
  }

  return (
    <div className="container-fluid py-4">
      <PageHeader
        title="Detall del Proveïdor"
        breadcrumbItems={breadcrumbItems}
        actions={
          <div className="d-flex gap-2">
            <Button title="Editar" onClick={handleEdit} />
          </div>
        }
      />

      <Card className="shadow-sm">
        <Card.Body>
          <Row className="mb-4">
            <Col>
              <h3 className="mb-3">
                {supplier.name}
                {supplier.isActive ? (
                  <Badge bg="success" className="ms-2">
                    Actiu
                  </Badge>
                ) : (
                  <Badge bg="secondary" className="ms-2">
                    Inactiu
                  </Badge>
                )}
              </h3>
            </Col>
          </Row>

          <Row>
            <Col md={6} className="mb-4">
              <h5 className="text-muted mb-3">Informació de Contacte</h5>
              <div className="mb-3">
                <strong>Persona de contacte:</strong>
                <p className="mb-1">{supplier.contactName}</p>
              </div>
              <div className="mb-3">
                <strong>Email:</strong>
                <p className="mb-1">
                  <a href={`mailto:${supplier.email}`}>{supplier.email}</a>
                </p>
              </div>
              <div className="mb-3">
                <strong>Telèfon:</strong>
                <p className="mb-1">
                  <a href={`tel:${supplier.phone}`}>{supplier.phone}</a>
                </p>
              </div>
            </Col>

            <Col md={6} className="mb-4">
              <h5 className="text-muted mb-3">Adreça</h5>
              <div className="mb-3">
                <p className="mb-1">{supplier.address || 'No especificada'}</p>
              </div>
            </Col>
          </Row>

          {supplier.notes && (
            <Row>
              <Col md={12} className="mb-4">
                <h5 className="text-muted mb-3">Notes</h5>
                <div className="bg-light p-3 rounded">
                  <p className="mb-0">{supplier.notes}</p>
                </div>
              </Col>
            </Row>
          )}

          <Row>
            <Col md={6} className="mb-3">
              <small className="text-muted">
                <strong>Creat:</strong>{' '}
                {new Date(supplier.createdAt).toLocaleString('ca-ES')}
              </small>
            </Col>
            <Col md={6} className="mb-3">
              <small className="text-muted">
                <strong>Última actualització:</strong>{' '}
                {new Date(supplier.updatedAt).toLocaleString('ca-ES')}
              </small>
            </Col>
          </Row>
        </Card.Body>
      </Card>
    </div>
  );
};

export default SupplierDetailPage;
