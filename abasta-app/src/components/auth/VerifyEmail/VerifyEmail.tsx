import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import authService from '../../../services/authService';
import Topbar from '../../common/Topbar/Topbar';
import { Alert, Spinner } from 'react-bootstrap';

const VerifyEmail = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading'
  );

  useEffect(() => {
    const verifyEmail = async () => {
      const token = searchParams.get('token');
      if (!token) {
        setStatus('error');
        return;
      }

      try {
        await authService.verifyEmail(token);
        setStatus('success');
      } catch {
        setStatus('error');
      }
    };

    verifyEmail();
  }, [searchParams]);

  return (
    <div className="verify-container d-flex align-items-start justify-content-center">
      <Topbar />

      <div className="verify-card p-4 mt-5 text-center">
        {status === 'loading' && (
          <>
            <Spinner
              animation="border"
              role="status"
              className="mb-3 text-secondary"
            />
            <h4 className="text-primary mb-2">Verificant el teu correu...</h4>
            <p className="text-muted">
              Estem comprovant la teva verificació. Si us plau, espera uns
              segons.
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <h4 className="text-success mb-3">Correu verificat correctament</h4>
            <p className="text-muted mb-4">
              Ja pots iniciar sessió amb el teu compte.
            </p>
            <Link to="/login" className="link">
              Inicia sessió
            </Link>
          </>
        )}

        {status === 'error' && (
          <>
            <Alert variant="danger" className="mb-3">
              <strong>Error en la verificació</strong>
            </Alert>
            <p className="text-muted mb-4">
              Si creus que és un error, torna a sol·licitar la verificació.
            </p>
            <Link to="/login" className="link">
              Torna a l’inici de sessió
            </Link>
          </>
        )}
      </div>
    </div>
  );
};

export default VerifyEmail;
