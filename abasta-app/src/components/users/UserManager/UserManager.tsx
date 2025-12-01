import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { User, UserFormData } from '../../../types/user.types';
import { userService } from '../../../services/userService';
import { useUserForm } from '../../../hooks/useUserForm';
import PageHeader from '../../common/PageHeader/PageHeader';
import Button from '../../common/Button/Button';
import Alert from '../../common/Alert/Alert';
import { Form } from 'react-bootstrap';
import FormCard from '../../common/FormCard/FormCard';
import DeleteModal from '../../common/DeleteModal/DeleteModal';

type FormMode = 'create' | 'edit' | 'detail';

interface UserManagerProps {
  mode: FormMode;
}

const UserManager = ({ mode }: UserManagerProps) => {
  const { uuid } = useParams<{ uuid: string }>();
  const navigate = useNavigate();

  const [initialData, setInitialData] = useState<Partial<User> | undefined>(
    undefined
  );
  const [userDetails, setUserDetails] = useState<User | null>(null);
  const [isLoadingData, setIsLoadingData] = useState(mode !== 'create');
  const [loadError, setLoadError] = useState<string>('');

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const isReadMode = mode === 'detail';
  const isEditMode = mode === 'edit';
  const isCreateMode = mode === 'create';

  useEffect(() => {
    if (isCreateMode || !uuid) return;

    const fetchUser = async (userUuid: string) => {
      setIsLoadingData(true);
      setLoadError('');
      try {
        const response = await userService.getUserByUuid(userUuid);
        if (response.success && response.data) {
          const user = response.data;
          setUserDetails(user);
          setInitialData(response.data);
        } else {
          setLoadError(response.message || "Error al carregar l'usuari.");
        }
      } catch (err) {
        setLoadError(
          err instanceof Error ? err.message : "Error al carregar l'usuari."
        );
      } finally {
        setIsLoadingData(false);
      }
    };

    fetchUser(uuid);
  }, [mode, uuid]);

  const formInitialData = useMemo(() => {
    if (!initialData) return undefined;
    return {
      firstName: initialData.firstName || '',
      lastName: initialData.lastName || '',
      email: initialData.email || '',
      phone: initialData.phone || '',
      password: initialData.password || '',
      role: initialData.role || '',
      isActive: initialData.isActive ?? false,
    } as UserFormData;
  }, [initialData]);

  const {
    register,
    handleSubmit,
    errors,
    isLoading: isSubmitting,
    error: submitError,
    successMessage,
    handleCancel,
  } = useUserForm({
    mode: isEditMode ? 'edit' : 'create',
    initialData: formInitialData,
  });

  const isLoading = isLoadingData || isSubmitting;
  const isFormDisabled = isReadMode || isLoading;

  const breadcrumbItem = useMemo(() => {
    switch (mode) {
      case 'create':
        return 'Crear Usuari';
      case 'edit':
        return 'Editar Usuari';
      case 'detail':
        return "Detalls de l'Usuari";
      default:
        return 'Usuari';
    }
  }, [mode]);

  const title = useMemo(() => {
    switch (mode) {
      case 'create':
        return 'Nou Usuari';
      case 'edit':
        return `${userDetails?.firstName || 'Editar usuari'}`;
      case 'detail':
        return `${userDetails?.firstName || "Detalls de l'usuari"}`;
      default:
        return 'Usuari';
    }
  }, [mode, userDetails]);

  const breadcrumbItems = [
    { label: 'Usuaris', path: '/users' },
    { label: breadcrumbItem, active: true },
  ];

  const handleEdit = () => {
    if (uuid) {
      navigate(`/users/edit/${uuid}`);
    }
  };

  const handleDeleteClick = () => {
    setShowDeleteModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!userDetails) return;
    setIsDeleting(true);
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
          onClick={() => navigate('/users')}
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
            title={"Configuració de l'usuari"}
            subtitle={"Rol i estat de l'usuari al sistema"}
          >
            <div className="row g-3">
              {/* Role Field - SELECT */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="role">
                  <Form.Label>
                    Rol {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Select
                    {...register('role', {
                      required: isCreateMode
                        ? 'El rol és obligatori'
                        : undefined,
                    })}
                    isInvalid={!!errors.role}
                    disabled={isFormDisabled}
                  >
                    <option value="">Selecciona un rol</option>
                    <option value="ADMIN">Administrador</option>
                    <option value="USER">Usuari</option>
                  </Form.Select>
                  <Form.Control.Feedback type="invalid">
                    {errors.role?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* isActive Field - SWITCH */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="isActive">
                  <Form.Label>Estat</Form.Label>
                  <div>
                    <Form.Check
                      type="switch"
                      id="isActive"
                      label="Usuari actiu"
                      {...register('isActive')}
                      disabled={isFormDisabled}
                    />
                  </div>
                </Form.Group>
              </div>
            </div>

            <div>
              <h2 className="form-card-title card-title mt-6 mb-3 text-primary fw-bold">
                Informació general
              </h2>
              <p className="form-card-subtitle text-muted mb-4">
                Dades principals de l'usuari
              </p>
            </div>
            <div className="row g-3">
              {/* Name Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="firstName">
                  <Form.Label>
                    Nom {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix el nom de l'usuari"
                    {...register('firstName', {
                      required: isCreateMode
                        ? "El nom de l'usuari és obligatori"
                        : undefined,
                      minLength: {
                        value: 2,
                        message: 'El nom ha de tenir com a mínim 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.firstName}
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.firstName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Last name Field */}
              <div className="col-12 col-md-6">
                <Form.Group className="mb-3 text-start" controlId="lastName">
                  <Form.Label>
                    Cognoms{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="text"
                    placeholder="Introdueix els cognoms de l'usuari"
                    {...register('lastName', {
                      required: isCreateMode
                        ? "Els cognoms de l'usuari són obligatoris"
                        : undefined,
                      minLength: {
                        value: 2,
                        message:
                          'Els cognoms han de tenir com a mínim 2 caràcters',
                      },
                    })}
                    isInvalid={!!errors.lastName}
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.lastName?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Phone Field */}
              <div className="col-12 col-md-4">
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
                      validate: value => {
                        if (!value) return true;
                        const cleaned = value.replace(/\s/g, '');
                        const isValid = /^[+]?[0-9]{9,13}$/.test(cleaned);
                        return (
                          isValid || 'El telèfon no és vàlid (9-13 dígits)'
                        );
                      },
                    })}
                    isInvalid={!!errors.phone}
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.phone?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Email Field */}
              <div className="col-12 col-md-4">
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
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.email?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Password Field */}
              <div className="col-12 col-md-4">
                <Form.Group className="mb-3 text-start" controlId="password">
                  <Form.Label>
                    Contrasenya{' '}
                    {isCreateMode && <span className="text-danger">*</span>}
                  </Form.Label>
                  <Form.Control
                    type="password"
                    placeholder="Introdueix una contrasenya"
                    {...register('password', {
                      required: isCreateMode
                        ? 'La contrasenya és obligatoria'
                        : undefined,
                      pattern: {
                        value:
                          /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\s]).{8,}$/,
                        message:
                          'Ha de tenir almenys 8 caràcters, una majúscula, una minúscula, un número i un caràcter especial',
                      },
                    })}
                    isInvalid={!!errors.password}
                    disabled={isFormDisabled}
                  />
                  <Form.Control.Feedback type="invalid">
                    {errors.password?.message}
                  </Form.Control.Feedback>
                </Form.Group>
              </div>

              {/* Action Buttons */}
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
              onClick={() => navigate('/users')}
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
              Eliminar usuari
            </button>
          </div>
        )}

        <DeleteModal
          show={showDeleteModal}
          entityType="usuari"
          itemName={userDetails?.firstName || ''}
          onClose={handleDeleteCancel}
          onConfirm={handleDeleteConfirm}
          isDeleting={isDeleting}
        />
      </div>
    </div>
  );
};

export default UserManager;
