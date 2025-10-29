import { Alert, Form, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { useRecoverPasswordForm } from '../../../hooks/useRecoverPasswordForm';

import Button from '../../common/Button/Button';
import Topbar from '../../common/Topbar/Topbar';

import './RecoverPasswordForm.scss';
import benefits from '../../../assets/images/benefits-1.png';

const RecoverPasswordForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    errors,
    isLoading,
    error,
    setError,
    isSubmitted,
    showSuccess,
    serverMessage,
  } = useRecoverPasswordForm();

  return (
    <div className="recover-container d-flex align-items-start justify-content-center">
      <Topbar />
      <img
        src={benefits}
        alt="Background illustration"
        className="recover-illustration d-none d-md-block"
      />

      <div className="recover-card p-4 mt-5 text-center">
        {isSubmitted ? (
          <>
            {!showSuccess ? (
              <>
                <Spinner
                  animation="border"
                  role="status"
                  className="mb-3 text-secondary"
                />
                <h4 className="text-primary mb-2">Comprovant el correu...</h4>
                <p className="text-muted">
                  Si el correu és correcte, rebràs un enllaç per restablir la
                  teva contrasenya.
                </p>
              </>
            ) : (
              <>
                <h4 className="text-success mb-2">
                  {serverMessage || 'Correu enviat correctament!'}
                </h4>
                <p className="text-muted">
                  Hem enviat un correu electrònic amb un enllaç per restablir la
                  teva contrasenya.
                  <br />
                  Revisa la safata d’entrada o la carpeta de correu brossa.
                </p>
                <Link to="/login" className="link">
                  Torna a iniciar sessió
                </Link>
              </>
            )}
          </>
        ) : (
          <div className="text-start">
            <h2 className="text-primary mb-1">Recuperar contrasenya</h2>
            <p className="recover-subtitle mb-4">
              Introdueix el teu correu electrònic per restablir la teva
              contrasenya.
            </p>

            {error && (
              <Alert variant="danger" dismissible onClose={() => setError('')}>
                <i className="bi bi-exclamation-circle me-2"></i>
                {error} {serverMessage && ` - ${serverMessage}`}
              </Alert>
            )}

            <Form onSubmit={handleSubmit}>
              <Form.Group controlId="formEmail" className="mb-3 text-start">
                <Form.Label>Correu electrònic</Form.Label>
                <Form.Control
                  type="email"
                  placeholder="exemple@mail.com"
                  {...register('email', {
                    required: 'El correu electrònic és obligatori',
                    pattern: {
                      value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                      message: 'Correu electrònic no vàlid',
                    },
                  })}
                  isInvalid={!!errors.email}
                  disabled={isLoading}
                />
                <Form.Control.Feedback type="invalid">
                  {errors.email?.message}
                </Form.Control.Feedback>
              </Form.Group>

              <span className="d-flex justify-content-center">
                <Button
                  title={isLoading ? 'Enviant...' : 'Recuperar contrasenya'}
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

            <div className="text-center mt-3">
              <p className="small mb-0">No tens compte a Abasta? </p>
              <Link to="/register" className="link">
                Registra la teva empresa
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default RecoverPasswordForm;
