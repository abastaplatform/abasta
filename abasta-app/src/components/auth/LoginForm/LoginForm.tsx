import { Alert, Form } from 'react-bootstrap';
import { Link } from 'react-router-dom';

import { useLoginForm } from '../../../hooks/useLoginForm';

import './LoginForm.scss';
import benefits from '../../../assets/images/benefits-1.png';

import Button from '../../common/Button/Button';
import Topbar from '../../common/Topbar/Topbar';

const LoginForm: React.FC = () => {
  const {
    register,
    handleSubmit,
    errors,
    showPassword,
    setShowPassword,
    isLoading,
    error,
    setError,
  } = useLoginForm();

  return (
    <div className="login-container d-flex align-items-start justify-content-center">
      <Topbar />
      <img
        src={benefits}
        alt="Background illustration"
        className="login-illustration d-none d-md-block"
      />
      <div className="login-card p-4 mt-5">
        <h2 className="text-primary mb-1">Iniciar sessió</h2>
        <p className="login-subtitle mb-4">Accedeix al teu compte</p>

        {error && (
          <Alert variant="danger" dismissible onClose={() => setError('')}>
            <i className="bi bi-exclamation-circle me-2"></i>
            {error}
          </Alert>
        )}

        <Form onSubmit={handleSubmit}>
          {/* Email */}
          <Form.Group controlId="formEmail" className="mb-3 text-start">
            <Form.Label>Correu electrònic</Form.Label>
            <Form.Control
              type="email"
              placeholder="mattsmith@mail.com"
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

          {/* Password */}
          <Form.Group
            controlId="formPassword"
            className="mb-4 text-start position-relative"
          >
            <Form.Label>Contrasenya</Form.Label>
            <div className="password-wrapper">
              <Form.Control
                type={showPassword ? 'text' : 'password'}
                placeholder="······"
                {...register('password', {
                  required: 'La contrasenya és obligatòria',
                  minLength: {
                    value: 8,
                    message: 'La contrasenya és massa curta',
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
                  showPassword ? 'Ocultar contrasenya' : 'Mostrar contrasenya'
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

          <span className="d-flex justify-content-center">
            <Button
              title={isLoading ? 'Iniciant sessió...' : 'Iniciar sessió'}
              type="submit"
              disabled={isLoading}
              isLoading={isLoading}
            />
          </span>
        </Form>

        <div className="text-center mt-3">
          <p className="small mb-0">Has oblidat la teva contrasenya? </p>
          <Link to="/recover" className="link">
            Recupera-la
          </Link>
        </div>

        <div className="text-center mt-3">
          <p className="small mb-0">No tens compte a Abasta? </p>
          <Link to="/register" className="link">
            Registra la teva empresa
          </Link>
        </div>
      </div>
    </div>
  );
};

export default LoginForm;
