import { useState } from 'react';
import { Form } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import './LoginForm.scss';

import Button from '../../common/Button/Button';
import Topbar from '../../common/Topbar/Topbar';

const LoginForm: React.FC = () => {
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Enviar login');
  };

  return (
    <div className="login-container d-flex align-items-start justify-content-center">
      <Topbar />

      <div className="login-card p-4 mt-5">
        <h2 className="text-primary mb-1">Iniciar sessió</h2>
        <p className="login-subtitle mb-4">Accedeix al teu compte</p>

        <Form onSubmit={handleSubmit}>
          {/* Email */}
          <Form.Group controlId="formEmail" className="mb-3 text-start">
            <Form.Label>Correu electrònic</Form.Label>
            <Form.Control
              type="email"
              placeholder="mattsmith@mail.com"
              required
            />
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
                required
              />
              <button
                type="button"
                className="btn-eye"
                onClick={() => setShowPassword(!showPassword)}
                aria-label={
                  showPassword ? 'Ocultar contrasenya' : 'Mostrar contrasenya'
                }
              >
                <i
                  className={`bi ${showPassword ? 'bi-eye-slash' : 'bi-eye'}`}
                />
              </button>
            </div>
          </Form.Group>

          <span className="d-flex justify-content-center">
            <Button title="Iniciar sessió" type="submit" />
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
