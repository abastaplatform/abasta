import React from 'react';
import { Form, Button, Alert, Spinner } from 'react-bootstrap';
import ButtonCustom from '../../common/Button/Button';
import { useCompanyConfigForm } from '../../../hooks/useCompanyConfigForm';
import './CompanyConfigForm.scss';

const CompanyConfig: React.FC = () => {
  const {
    register,
    handleSubmit,
    errors,
    isEditing,
    toggleEdit,
    isLoading,
    isFetching,
    error,
    success,
    reset,
    originalData,
  } = useCompanyConfigForm();

  // Cancel·lar i restaurar valors originals
  const handleCancel = () => {
    if (originalData) reset(originalData);
    toggleEdit();
  };

  if (isFetching) {
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <Spinner animation="border" role="status" />
        <span className="ms-2 text-muted">Carregant dades de l’empresa...</span>
      </div>
    );
  }

  return (
    <div className="company-config container py-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-primary">Configuració d’Empresa</h2>
          <p className="text-muted mb-0">
            Administra la informació de la teva empresa
          </p>
        </div>

        {!isEditing && (
          <Button
            variant="primary"
            onClick={toggleEdit}
            className="d-flex align-items-center"
          >
            <i className="bi bi-pencil me-2"></i> Editar
          </Button>
        )}
      </div>

      {error && (
        <Alert variant="danger" dismissible>
          <i className="bi bi-exclamation-circle me-2"></i>
          {error}
        </Alert>
      )}
      {success && (
        <Alert variant="success">
          <i className="bi bi-check-circle me-2"></i>
          Les dades de l’empresa s’han desat correctament.
        </Alert>
      )}

      <Form onSubmit={handleSubmit}>
        {/* Informació general */}
        <section className="mb-4">
          <h5 className="fw-semibold mb-3 text-primary">Informació general</h5>
          <div className="row">
            <div className="col-md-6 mb-3">
              <Form.Label>Nom de l'empresa</Form.Label>
              <Form.Control
                type="text"
                {...register('name', { required: true })}
                isInvalid={!!errors.name}
                disabled={!isEditing}
              />
            </div>
            <div className="col-md-6 mb-3">
              <Form.Label>CIF/NIF</Form.Label>
              <Form.Control
                type="text"
                {...register('taxId', { required: true })}
                isInvalid={!!errors.taxId}
                disabled={!isEditing}
              />
            </div>
          </div>

          <Form.Group className="mb-3">
            <Form.Label>Adreça</Form.Label>
            <Form.Control
              type="text"
              {...register('address')}
              isInvalid={!!errors.address}
              disabled={!isEditing}
            />
          </Form.Group>

          <div className="row">
            <div className="col-md-6 mb-3">
              <Form.Label>Ciutat</Form.Label>
              <Form.Control
                type="text"
                {...register('city')}
                isInvalid={!!errors.city}
                disabled={!isEditing}
              />
            </div>
            <div className="col-md-6 mb-3">
              <Form.Label>Codi Postal</Form.Label>
              <Form.Control
                type="text"
                {...register('postalCode')}
                isInvalid={!!errors.postalCode}
                disabled={!isEditing}
              />
            </div>
          </div>
        </section>

        {/* Informació de contacte */}
        <section>
          <h5 className="fw-semibold mb-3 text-primary">
            Informació de contacte
          </h5>
          <div className="row">
            <div className="col-md-6 mb-3">
              <Form.Group>
                <Form.Label>Correu electrònic</Form.Label>
                <Form.Control
                  type="email"
                  placeholder="Introdueix el correu de contacte"
                  {...register('email', {
                    required: 'El correu electrònic és obligatori',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Correu electrònic no vàlid',
                    },
                  })}
                  isInvalid={!!errors.email}
                  disabled={!isEditing}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.email?.message}
                </Form.Control.Feedback>
              </Form.Group>
            </div>

            <div className="col-md-6 mb-3">
              <Form.Label>Telèfon</Form.Label>
              <Form.Control
                type="text"
                {...register('phone')}
                isInvalid={!!errors.phone}
                disabled={!isEditing}
              />
            </div>
          </div>
        </section>

        {/* Botons només quan edites */}
        {isEditing && (
          <div className="d-flex justify-content-end mt-4 gap-3 button-group">
            <ButtonCustom
              title="Cancel·lar"
              type="button"
              variant="outline"
              onClick={handleCancel}
              disabled={isLoading}
            />
            <ButtonCustom
              title={isLoading ? 'Desant...' : 'Desar canvis'}
              type="submit"
              variant="solid"
              disabled={isLoading}
              isLoading={isLoading}
            />
          </div>
        )}
      </Form>
    </div>
  );
};

export default CompanyConfig;
