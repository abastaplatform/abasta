import { Alert as BootstrapAlert } from 'react-bootstrap';
import './Alert.scss';

export type AlertVariant = 'success' | 'danger' | 'warning' | 'info';

interface AlertProps {
  variant: AlertVariant;
  message: string;
  dismissible?: boolean;
  onClose?: () => void;
  icon?: boolean;
}

const Alert = ({
  variant,
  message,
  dismissible = true,
  onClose,
  icon = true,
}: AlertProps) => {
  const iconMap: Record<AlertVariant, string> = {
    success: 'bi-check-circle',
    danger: 'bi-exclamation-triangle',
    warning: 'bi-exclamation-circle',
    info: 'bi-info-circle',
  };

  return (
    <BootstrapAlert
      variant={variant}
      dismissible={dismissible}
      onClose={onClose}
      className="custom-alert"
    >
      {icon && <i className={`bi ${iconMap[variant]} me-2`}></i>}
      {message}
    </BootstrapAlert>
  );
};

export default Alert;
