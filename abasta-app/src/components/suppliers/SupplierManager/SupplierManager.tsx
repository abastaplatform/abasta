import { useState, useEffect, useMemo } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Form } from 'react-bootstrap';

import { useSupplierForm } from '../../../hooks/useSupplierForm';
import { supplierService } from '../../../services/supplierService';
import type { Supplier, SupplierFormData } from '../../../types/supplier.types';
import { supplierToFormData } from '../../../utils/supplier.utils';

import Button from '../../common/Button/Button';
import PageHeader from '../../common/PageHeader/PageHeader';
import Alert from '../../common/Alert/Alert';
import FormCard from '../../common/FormCard/FormCard';
import DeleteModal from '../../common/DeleteModal/DeleteModal';

type FormMode = 'create' | 'edit' | 'detail';

interface SupplierManagerProps {
  mode: FormMode;
}

const SupplierManager = ({ mode }: SupplierManagerProps) => {
  const { uuid } = useParams<{ uuid: string }>();
  const navigate = useNavigate();

  const [initialData, setInitialData] = useState<SupplierFormData | undefined>(
    undefined
  );
  const [supplierDetail, setSupplierDetail] = useState<Supplier | null>(null);
  const [isLoadingData, setIsLoadingData] = useState(mode !== 'create');
  const [loadError, setLoadError] = useState('');

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const isReadMode = mode === 'detail';
  const isEditMode = mode === 'edit';
  const isCreateMode = mode === 'create';

  useEffect(() => {
    if (isCreateMode || !uuid) return;

    const fetchSupplier = async (supplierUuid: string) => {
      setIsLoadingData(true);
      setLoadError('');
      try {
        const response = await supplierService.getSupplierByUuid(supplierUuid);
        if (response.success && response.data) {
          const supplier = response.data;
          setSupplierDetail(supplier);
          setInitialData(supplierToFormData(supplier));
        } else {
          setLoadError('No s’ha pogut carregar el proveïdor.');
        }
      } catch (err) {
        setLoadError(
          err instanceof Error ? err.message : 'Error al carregar el proveïdor.'
        );
      } finally {
        setIsLoadingData(false);
      }
    };

    fetchSupplier(uuid);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mode, uuid]);

  const {
    register,
    handleSubmit,
    errors,
    isLoading: isSubmitting,
    error: submitError,
    successMessage,
    handleCancel,
  } = useSupplierForm({
    mode: isEditMode ? 'edit' : 'create',
    initialData: initialData,
  });

  const isLoading = isLoadingData || isSubmitting;

  const isFormDisabled = isReadMode || isSubmitting;

  const breadcrumbItem = useMemo(() => {
    switch (mode) {
      case 'create':
        return 'Nou proveïdor';
      case 'edit':
        return `Editar proveïdor`;
      case 'detail':
        return `Detall del proveïdor`;
      default:
        return 'Proveïdor';
    }
  }, [mode]);

  const title = useMemo(() => {
    switch (mode) {
      case 'create':
        return 'Nou proveïdor';
      case 'edit':
        return `${supplierDetail?.name || 'Editar proveïdor'}`;
      case 'detail':
        return `${supplierDetail?.name || 'Detall del proveïdor'}`;
      default:
        return 'Proveïdor';
    }
  }, [mode, supplierDetail]);

  const breadcrumbItems = [
    { label: 'Proveïdors', path: '/suppliers' },
    { label: breadcrumbItem, active: true },
  ];

  const handleEdit = () => {
    if (uuid) navigate(`/suppliers/edit/${uuid}`);
  };

  const handleDeleteClick = () => {
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!supplierDetail) return;

    setIsDeleting(true);
    setLoadError('');
    try {
      await supplierService.deleteSupplier(supplierDetail.uuid);

      setShowDeleteModal(false);
      const successMsg = `Proveïdor "${supplierDetail.name}" eliminat correctament`;

      navigate('/suppliers', { state: { successMessage: successMsg } });
    } catch (err) {
      const errorMessage =
        err instanceof Error ? err.message : 'Error al eliminar el proveïdor';
      setLoadError(errorMessage);
      console.error('Delete supplier error:', err);
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setShowDeleteModal(false);
  };

  if (isLoadingData && !isCreateMode) {
    return (
      <div className="container-fluid py-4 text-center">
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Carregant...</span>
        </div>
      </div>
    );
  }

  if (loadError) {
    return (
      <div className="container-fluid py-4">
        <PageHeader title={title} breadcrumbItems={breadcrumbItems} />
        <Alert variant="danger" message={loadError} />
        <Button
          title="Tornar"
          variant="outline"
          onClick={() => navigate('/suppliers')}
        />
      </div>
    );
  }

  return (
    <div className="form-container">
      <div className="container-fluid py-4">
        <PageHeader
          title={title}
          breadcrumbItems={breadcrumbItems}
          actions={
            isReadMode && (
              <div className="d-flex gap-2">
                <Button title="Editar" onClick={handleEdit} />
              </div>
            )
          }
        />

        {successMessage && <Alert variant="success" message={successMessage} />}
        {submitError && <Alert variant="danger" message={submitError} />}

        <Form onSubmit={isReadMode ? e => e.preventDefault() : handleSubmit}>
          <FormCard
            title={'Informació general'}
            subtitle={'Dades principals del proveïdor'}
          >
            <div className="row g-3">
              {/* Name Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="name">
                  <Form.Label>
                    Nom del proveïdor{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix el nom del proveïdor"
                    {...register('name', {
                      required: isCreateMode
                        ? 'El nom del proveïdor és obligatori'
                        : undefined,
                      minLength: {
                        value: 2,
                        message: 'El nom ha de tenir com a mínim 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.name}
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.name?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Contact Name Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="contactName">
                  <Form.Label>
                    Nom de contacte{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix el nom de contacte"
                    {...register('contactName', {
                      required: isCreateMode
                        ? 'El nom de contacte és obligatori'
                        : undefined,
                      minLength: {
                        value: 2,
                        message:
                          'El nom de contacte ha de tenir com a mínim 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.contactName}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.contactName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Email Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="email">
                  <Form.Label>
                    Correu electrònic{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="email"
                    placeholder="Introdueix el correu electrònic"
                    {...register('email', {
                      required: isCreateMode
                        ? 'El correu electrònic és obligatori'
                        : undefined,
                      pattern: {
                        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                        message: 'El correu electrònic no és vàlid',
                      },
                    })}
                    isInvalid={!!errors.email}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.email?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Phone Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="phone">
                  <Form.Label>
                    Telèfon{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix el telèfon"
                    {...register('phone', {
                      required: isCreateMode
                        ? 'El telèfon és obligatori'
                        : undefined,
                      pattern: {
                        value:
                          /^[+]?[(]?[0-9]{3}[)]?[-\s.]?[0-9]{3}[-\s.]?[0-9]{3,6}$/,
                        message: 'El telèfon no és vàlid',
                      },
                    })}
                    isInvalid={!!errors.phone}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.phone?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Address Field */}
              <div className="col-12">
                <Form.Group className="mb-3 text-start" controlId="address">
                  <Form.Label>
                    Adreça{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix l'adreça"
                    {...register('address', {
                      required: isCreateMode
                        ? "L'adreça és obligatoria"
                        : undefined,
                      minLength: { value: 5, message: "L'adreça no és vàlida" },
                    })}
                    isInvalid={!!errors.address}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.address?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* City Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="city">
                  <Form.Label>
                    Ciutat{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix la ciutat"
                    {...register('city', {
                      required: isCreateMode
                        ? 'La ciutat és obligatòria'
                        : undefined,
                      minLength: {
                        value: 2,
                        message: 'La ciutat ha de tenir almenys 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.city}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.city?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Zip Code Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="zipCode">
                  <Form.Label>
                    Codi postal{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix el codi postal"
                    {...register('zipCode', {
                      required: isCreateMode
                        ? 'El codi postal és obligatori'
                        : undefined,
                      pattern: {
                        value: /^\d{5}$/,
                        message: 'El codi postal ha de tenir 5 dígits',
                      },
                    })}
                    isInvalid={!!errors.zipCode}
                    disabled={isFormDisabled} // CLAVE
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.zipCode?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Notes Field */}
              <div className="col-12">
                <Form.Group className="mb-3 text-start" controlId="notes">
                  <Form.Label>Notes</Form.Label>
                  <Form.Control
                    as="textarea"
                    rows={4}
                    placeholder="Afegeix notes addicionals (opcional)"
                    {...register('notes')}
                    disabled={isFormDisabled} // CLAVE
                  />
                </Form.Group>
              </div>

              {/* Action Buttons (Solo visibles si NO estamos en modo detalle) */}
              {!isReadMode && (
                <div className="d-flex flex-column flex-md-row gap-3 justify-content-center mb-4">
                  <Button
                    title="Cancel·lar"
                    variant="outline"
                    disabled={isLoading}
                    onClick={handleCancel}
                  />

                  <Button
                    title={
                      isSubmitting
                        ? isCreateMode
                          ? 'Desant...'
                          : 'Actualitzant...'
                        : isCreateMode
                          ? 'Desar'
                          : 'Actualitzar'
                    }
                    disabled={isLoading}
                    type="submit"
                  />
                </div>
              )}
            </div>
          </FormCard>
        </Form>

        {isReadMode && (
          <div className="d-flex justify-content-center mt-4">
            <Button
              title="Tornar a la llista"
              variant="outline"
              onClick={() => navigate('/suppliers')}
            />
          </div>
        )}
        {(isEditMode || isReadMode) && (
          <div className="mt-4 text-center">
            <button
              type="button"
              className="btn btn-link text-danger text-decoration-underline"
              onClick={handleDeleteClick}
              disabled={isSubmitting}
              style={{ padding: 0, border: 'none', background: 'none' }}
            >
              Eliminar proveïdor
            </button>
          </div>
        )}

        <DeleteModal
          show={showDeleteModal}
          entityType="proveïdor"
          itemName={supplierDetail?.name || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default SupplierManager;
