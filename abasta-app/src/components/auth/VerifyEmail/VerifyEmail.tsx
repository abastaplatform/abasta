import { useEffect, useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import Topbar from '../../common/Topbar/Topbar';
import { Alert, Spinner } from 'react-bootstrap';
import { useAuth } from '../../../context/useAuth';

const VerifyEmail = () => {
  const [searchParams] = useSearchParams();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading'
  );
  const [errorMessage, setErrorMessage] = useState('');

  const { verifyEmail } = useAuth();

  useEffect(() => {
    const verify = async () => {
      const token = searchParams.get('token');

      if (!token) {
        setErrorMessage("No s'ha trobat el token de verificació.");
        setStatus('error');
        return;
      }

      try {
        await verifyEmail(token);
        setStatus('success');
      } catch (err) {
        const message =
          err instanceof Error
            ? err.message
            : 'Error al verificar el correu electrònic.';
        setErrorMessage(message);
        setStatus('error');
      }
    };

    verify();
  }, [searchParams, verifyEmail]);

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
            <i className="bi bi-check-circle text-success fs-1 mb-3"></i>
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
            <i className="bi bi-x-circle text-danger fs-1 mb-3"></i>
            <h4 className="text-danger mb-3">Error en la verificació</h4>
            <Alert variant="danger" className="mb-3 text-start">
              <i className="bi bi-exclamation-circle me-2"></i>
              {errorMessage || "No s'ha pogut verificar el correu electrònic."}
            </Alert>
            <p className="text-muted mb-4">
              Si creus que és un error o el token ha expirat, pots sol·licitar
              un nou correu de verificació des de la pàgina d'inici de sessió.
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
