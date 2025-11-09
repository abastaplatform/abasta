import { Form } from 'react-bootstrap';

import { useSupplierForm } from '../../../hooks/useSupplierForm';

import './NewSupplier.scss';
import Button from '../../common/Button/Button';
import PageHeader from '../../common/PageHeader/PageHeader';
import Alert from '../../common/Alert/Alert';
import FormCard from '../../common/FormCard/FormCard';

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

  const breadcrumbItems = [
    { label: 'Proveïdors', path: '/suppliers' },
    { label: 'Nou proveïdor', active: true },
  ];

  return (
    <div className="form-container">
      <div className="container-fluid py-4">
        <PageHeader title="Nou proveïdor" breadcrumbItems={breadcrumbItems} />

        {successMessage && <Alert variant="success" message={successMessage} />}

        {error && <Alert variant="danger" message={error} />}

        <Form onSubmit={handleSubmit}>
          <FormCard
            title="Informació general"
            subtitle="Dades principals del proveïdor"
          >
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
                <Form.Group className="mb-3 text-start" controlId="contactName">
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
                    {...register('city', {
                      required: 'La ciutat és obligatòria',
                      minLength: {
                        value: 2,
                        message: 'La ciutat ha de tenir almenys 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.city}
                    disabled={isLoading}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.city?.message}
                  </Form.Control.Feedback>
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
                    {...register('zipCode', {
                      required: 'El codi postal és obligatori',
                      pattern: {
                        value: /^\d{5}$/,
                        message: 'El codi postal ha de tenir 5 dígits',
                      },
                    })}
                    isInvalid={!!errors.zipCode}
                    disabled={isLoading}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.zipCode?.message}
                  </Form.Control.Feedback>
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
          </FormCard>
        </Form>
      </div>
    </div>
  );
};

export default NewSupplier;
