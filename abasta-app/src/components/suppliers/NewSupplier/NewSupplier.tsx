import { Alert, Form } from 'react-bootstrap';

import { useSupplierForm } from '../../../hooks/useSupplierForm';

import './NewSupplier.scss';
import Button from '../../common/Button/Button';

const NewSupplier = () => {
  const {
    register,
    handleSubmit,
    errors,
    isLoading,
    error,
    successMessage,
    handleCancel,
  } = useSupplierForm();

  return (
    <div className="new-supplier-container">
      <div className="container-fluid py-4">
        <h2 className="mb-4 text-primary">Nou proveïdor</h2>

        {successMessage && (
          <Alert
            variant="success"
            onClose={() => window.location.reload()}
            dismissible
          >
            <i className="bi bi-check-circle-fill me-2"></i>
            {successMessage}
          </Alert>
        )}

        {error && (
          <div className="alert alert-danger" role="alert">
            <i className="bi bi-exclamation-triangle-fill me-2"></i>
            {error}
          </div>
        )}

        <Form onSubmit={handleSubmit}>
          <div className="card shadow-sm mb-4">
            <div className="card-body">
              <h3 className="card-title text-primary">Informació general</h3>
              <p className="text-muted mb-4">Dades principals del proveïdor</p>

              <div className="row g-3">
                <div className="col-12 col-md-6">
                  <Form.Group className="mb-3 text-start" controlId="name">
                    <Form.Label>
                      Nom del proveïdor <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix el nom del proveïdor"
                      {...register('name', {
                        required: 'El nom del proveïdor és obligatori',
                        minLength: {
                          value: 2,
                          message: 'El nom ha de tenir com a mínim 2 caràcters',
                        },
                      })}
                      isInvalid={!!errors.name}
                      disabled={isLoading}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.name?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-12 col-md-6">
                  <Form.Group
                    className="mb-3 text-start"
                    controlId="contactName"
                  >
                    <Form.Label>
                      Nom de contacte <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix el nom de contacte"
                      {...register('contactName', {
                        required: 'El nom de contacte és obligatori',
                        minLength: {
                          value: 2,
                          message:
                            'El nom de contacte ha de tenir com a mínim 2 caràcters',
                        },
                      })}
                      isInvalid={!!errors.contactName}
                      disabled={isLoading}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.contactName?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-12 col-md-6">
                  <Form.Group className="mb-3 text-start" controlId="email">
                    <Form.Label>
                      Correu electrònic <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="email"
                      placeholder="Introdueix el correu electrònic"
                      {...register('email', {
                        required: 'El correu electrònic és obligatori',
                        pattern: {
                          value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                          message: 'El correu electrònic no és vàlid',
                        },
                      })}
                      isInvalid={!!errors.email}
                      disabled={isLoading}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.email?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-12 col-md-6">
                  <Form.Group className="mb-3 text-start" controlId="phone">
                    <Form.Label>
                      Telèfon <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix el telèfon"
                      {...register('phone', {
                        required: 'El telèfon és obligatori',
                        pattern: {
                          value:
                            /^[+]?[(]?[0-9]{3}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{3,6}$/,
                          message: 'El telèfon no és vàlid',
                        },
                      })}
                      isInvalid={!!errors.phone}
                      disabled={isLoading}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.phone?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-12">
                  <Form.Group className="mb-3 text-start" controlId="address">
                    <Form.Label>
                      Adreça <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix l'adreça"
                      {...register('address', {
                        required: "L'adreça és obligatoria",
                        minLength: {
                          value: 5,
                          message: "L'adreça no és vàlida",
                        },
                      })}
                      isInvalid={!!errors.address}
                      disabled={isLoading}
                    />
                    <Form.Control.Feedback type="invalid">
                      {errors.address?.message}
                    </Form.Control.Feedback>
                  </Form.Group>
                </div>

                <div className="col-12 col-md-6">
                  <Form.Group className="mb-3 text-start" controlId="city">
                    <Form.Label>
                      Ciutat <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix la ciutat"
                      disabled={isLoading}
                    />
                  </Form.Group>
                </div>

                <div className="col-12 col-md-6">
                  <Form.Group className="mb-3 text-start" controlId="zipCode">
                    <Form.Label>
                      Codi postal <span className="text-danger">*</span>
                    </Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="Introdueix el codi postal"
                      disabled={isLoading}
                    />
                  </Form.Group>
                </div>

                <div className="col-12">
                  <Form.Group className="mb-3 text-start" controlId="notes">
                    <Form.Label>Notes</Form.Label>
                    <Form.Control
                      as="textarea"
                      rows={4}
                      placeholder="Afegeix notes addicionals (opcional)"
                      {...register('notes')}
                      disabled={isLoading}
                    />
                  </Form.Group>
                </div>
              </div>
            </div>

            <div className="d-flex flex-column flex-md-row gap-3 justify-content-center mb-4">
              <Button
                title="Cancel·lar"
                variant="outline"
                disabled={isLoading}
                onClick={handleCancel}
              />

              <Button
                title={isLoading ? 'Desant...' : 'Desar'}
                disabled={isLoading}
                type="submit"
              />
            </div>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default NewSupplier;
