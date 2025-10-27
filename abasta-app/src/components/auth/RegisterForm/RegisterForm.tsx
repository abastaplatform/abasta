import { Alert, Form, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useRegisterForm } from '../../../hooks/useRegisterForm';

import Button from '../../common/Button/Button';
import Topbar from '../../common/Topbar/Topbar';

import './RegisterForm.scss';

const RegisterForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    errors,
    showPassword,
    setShowPassword,
    isLoading,
    error,
    setError,
    isSubmitted,
    showSuccess,
  } = useRegisterForm();

  return (
    <div className="register-container d-flex align-items-start justify-content-center">
      <Topbar />
      <img
        src="/images/benefits-1.png"
        alt="Background illustration"
        className="register-illustration d-none d-md-block"
      />

      {isSubmitted ? (
        <div className="register-success text-center py-5">
          {!showSuccess ? (
            <>
              <Spinner
                animation="border"
                role="status"
                className="mb-3 text-secondary"
              />
              <h4 className="text-primary mb-2">Comprovant el registre...</h4>
              <p className="text-muted">
                Hem enviat un correu electrònic per verificar el teu compte.{' '}
                <br />
                Si no el trobes, revisa la carpeta de correu brossa.
              </p>
            </>
          ) : (
            <>
              <i className="bi bi-check-circle text-success fs-1 mb-3"></i>
              <h4 className="text-success mb-2">Compte creat correctament!</h4>
              <p className="text-muted">
                Revisa el teu correu electrònic per verificar el teu compte
                abans d'iniciar sessió.
              </p>
            </>
          )}
        </div>
      ) : (
        <div className="register-card p-4 mt-5">
          <h2 className="text-primary mb-1">Registre</h2>
          <p className="register-subtitle mb-4">Crea el teu compte d'empresa</p>

          {error && (
            <Alert variant="danger" dismissible onClose={() => setError('')}>
              <i className="bi bi-exclamation-circle me-2"></i>
              {error}
            </Alert>
          )}

          <Form onSubmit={handleSubmit}>
            {/* Company name */}
            <Form.Group controlId="formCompanyName" className="mb-3 text-start">
              <Form.Label>Nom de l'empresa</Form.Label>
              <Form.Control
                type="text"
                placeholder="Introdueix el nom de l'empresa"
                {...register('companyName', {
                  required: "El nom de l'empresa és obligatori",
                })}
                isInvalid={!!errors.companyName}
                disabled={isLoading}
              />
              <Form.Control.Feedback type="invalid">
                {errors.companyName?.message}
              </Form.Control.Feedback>
            </Form.Group>

            {/* Tax ID */}
            <Form.Group controlId="formTaxId" className="mb-3 text-start">
              <Form.Label>CIF / NIF de l'empresa</Form.Label>
              <Form.Control
                type="text"
                placeholder="Introdueix el CIF/NIF de l'empresa"
                {...register('taxId', {
                  required: 'El CIF/NIF és obligatori',
                })}
                isInvalid={!!errors.taxId}
                disabled={isLoading}
              />
              <Form.Control.Feedback type="invalid">
                {errors.taxId?.message}
              </Form.Control.Feedback>
            </Form.Group>

            {/* Admin first name */}
            <Form.Group
              controlId="formAdminFirstName"
              className="mb-3 text-start"
            >
              <Form.Label>Nom (usuari administrador)</Form.Label>
              <Form.Control
                type="text"
                placeholder="Introdueix el teu nom"
                {...register('adminFirstName', {
                  required: 'El nom és obligatori',
                })}
                isInvalid={!!errors.adminFirstName}
                disabled={isLoading}
              />
              <Form.Control.Feedback type="invalid">
                {errors.adminFirstName?.message}
              </Form.Control.Feedback>
            </Form.Group>

            {/* Admin last name */}
            <Form.Group
              controlId="formAdminLastName"
              className="mb-3 text-start"
            >
              <Form.Label>Cognoms</Form.Label>
              <Form.Control
                type="text"
                placeholder="Introdueix els teus cognoms"
                {...register('adminLastName', {
                  required: 'Els cognoms són obligatoris',
                })}
                isInvalid={!!errors.adminLastName}
                disabled={isLoading}
              />
              <Form.Control.Feedback type="invalid">
                {errors.adminLastName?.message}
              </Form.Control.Feedback>
            </Form.Group>

            {/* Email */}
            <Form.Group controlId="formAdminEmail" className="mb-3 text-start">
              <Form.Label>Correu electrònic</Form.Label>
              <Form.Control
                type="email"
                placeholder="Introdueix el teu correu electrònic"
                {...register('adminEmail', {
                  required: 'El correu electrònic és obligatori',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Correu electrònic no vàlid',
                  },
                })}
                isInvalid={!!errors.adminEmail}
                disabled={isLoading}
              />
              <Form.Control.Feedback type="invalid">
                {errors.adminEmail?.message}
              </Form.Control.Feedback>
            </Form.Group>

            {/* Password */}
            <Form.Group
              controlId="formAdminPassword"
              className="mb-4 text-start position-relative"
            >
              <Form.Label>Contrasenya</Form.Label>
              <div className="password-wrapper">
                <Form.Control
                  type={showPassword ? 'text' : 'password'}
                  placeholder="Introdueix la teva contrasenya"
                  {...register('adminPassword', {
                    required: 'La contrasenya és obligatòria',
                    minLength: {
                      value: 8,
                      message: 'Ha de tenir almenys 8 caràcters',
                    },
                  })}
                  isInvalid={!!errors.adminPassword}
                  disabled={isLoading}
                />
                <button
                  type="button"
                  className="btn-eye"
                  onClick={() => setShowPassword(!showPassword)}
                  aria-label={
                    showPassword ? 'Ocultar contrasenya' : 'Mostrar contrasenya'
                  }
                  disabled={isLoading}
                >
                  <i
                    className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`}
                  />
                </button>
                <Form.Control.Feedback type="invalid">
                  {errors.adminPassword?.message}
                </Form.Control.Feedback>
              </div>
            </Form.Group>

            <span className="d-flex justify-content-center">
              <Button
                title={isLoading ? 'Registrant...' : 'Registrar empresa'}
                type="submit"
                disabled={isLoading}
                isLoading={isLoading}
              />
            </span>
          </Form>

          <div className="text-center mt-3">
            <p className="small mb-0">Ja tens un compte a Abasta?</p>
            <Link to="/login" className="link">
              Inicia la teva sessió
            </Link>
          </div>
        </div>
      )}
    </div>
  );
};

export default RegisterForm;
