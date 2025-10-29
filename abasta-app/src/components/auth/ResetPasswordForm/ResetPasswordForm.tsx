import { Alert, Form, Spinner } from 'react-bootstrap';
import { Link, Navigate } from 'react-router-dom';
import { useResetPasswordForm } from '../../../hooks/useResetPasswordForm';

import Button from '../../common/Button/Button';
import Topbar from '../../common/Topbar/Topbar';

import './ResetPasswordForm.scss';
import benefits from '../../../assets/images/benefits-1.png';
import { useEffect } from 'react';

const ResetPasswordForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    errors,
    showPassword,
    setShowPassword,
    showRepeatPassword,
    setShowRepeatPassword,
    isLoading,
    error,
    setError,
    isSubmitted,
    showSuccess,
    token,
    password,
  } = useResetPasswordForm();

  useEffect(() => {
    if (isSubmitted || error) {
      window.scrollTo(0, 0);
    }
  }, [isSubmitted, error]);

  if (!token) {
    return <Navigate to="/recover" replace />;
  }

  return (
    <div className="reset-container d-flex align-items-start justify-content-center">
      <Topbar />
      <img
        src={benefits}
        alt="Background illustration"
        className="reset-illustration d-none d-md-block"
      />

      <div className="reset-card p-4 mt-5 text-center">
        {isSubmitted ? (
          <>
            {!showSuccess ? (
              <>
                <Spinner
                  animation="border"
                  role="status"
                  className="mb-3 text-secondary"
                />
                <h4 className="text-primary mb-2">
                  Restablint la contrasenya...
                </h4>
                <p className="text-muted">
                  Si el token és correcte, la teva contrasenya s’actualitzarà.
                </p>
              </>
            ) : (
              <>
                <h4 className="text-success mb-2">
                  Contrasenya restablerta correctament!
                </h4>
                <p className="text-muted">
                  Ja pots iniciar sessió amb la nova contrasenya.
                </p>
                <Link to="/login" className="link">
                  Torna a iniciar sessió
                </Link>
              </>
            )}
          </>
        ) : (
          <div className="text-start">
            <h2 className="text-primary mb-1">Nova contrasenya</h2>
            <p className="reset-subtitle mb-4">
              Introdueix la nova contrasenya i repeteix-la per restablir-la.
            </p>

            {error && (
              <Alert variant="danger" dismissible onClose={() => setError('')}>
                {error}
              </Alert>
            )}

            <Form onSubmit={handleSubmit}>
              {/* Password */}
              <Form.Group
                controlId="formPassword"
                className="mb-3 text-start position-relative"
              >
                <Form.Label>Nova contrasenya</Form.Label>
                <div className="password-wrapper">
                  <Form.Control
                    type={showPassword ? 'text' : 'password'}
                    placeholder="········"
                    {...register('password', {
                      required: 'La contrasenya és obligatòria',
                      pattern: {
                        value:
                          /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9\s]).{8,}$/,
                        message:
                          'Ha de tenir almenys 8 caràcters, una majúscula, una minúscula, un número i un caràcter especial',
                      },
                    })}
                    isInvalid={!!errors.password}
                    disabled={isLoading}
                  />

                  <button
                    type="button"
                    className="btn-eye"
                    onClick={() => setShowPassword(!showPassword)}
                    aria-label={
                      showPassword
                        ? 'Ocultar contrasenya'
                        : 'Mostrar contrasenya'
                    }
                    disabled={isLoading}
                  >
                    <i
                      className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`}
                    />
                  </button>
                </div>
                <Form.Control.Feedback type="invalid">
                  {errors.password?.message}
                </Form.Control.Feedback>
              </Form.Group>

              {/* Repeat Password */}
              <Form.Group
                controlId="formRepeatPassword"
                className="mb-4 text-start position-relative"
              >
                <Form.Label>Repetir contrasenya</Form.Label>
                <div className="password-wrapper">
                  <Form.Control
                    type={showRepeatPassword ? 'text' : 'password'}
                    placeholder="········"
                    {...register('repeatPassword', {
                      required: 'Cal repetir la contrasenya',
                      validate: value =>
                        value === password ||
                        'Les contrasenyes no coincideixen',
                    })}
                    isInvalid={!!errors.repeatPassword}
                    disabled={isLoading}
                  />
                  <button
                    type="button"
                    className="btn-eye"
                    onClick={() => setShowRepeatPassword(!showRepeatPassword)}
                    aria-label={
                      showRepeatPassword
                        ? 'Ocultar contrasenya'
                        : 'Mostrar contrasenya'
                    }
                    disabled={isLoading}
                  >
                    <i
                      className={`bi ${showRepeatPassword ? 'bi-eye-slash' : 'bi-eye'}`}
                    />
                  </button>
                </div>
                <Form.Control.Feedback type="invalid">
                  {errors.repeatPassword?.message}
                </Form.Control.Feedback>
              </Form.Group>

              <span className="d-flex justify-content-center">
                <Button
                  title={isLoading ? 'Restablint...' : 'Restablir contrasenya'}
                  type="submit"
                  disabled={isLoading}
                  isLoading={isLoading}
                />
              </span>
            </Form>
          </div>
        )}
      </div>
    </div>
  );
};

export default ResetPasswordForm;
