import { Modal } from 'react-bootstrap';
import Button from '../Button/Button';
import './ConfirmModal.scss';

interface ConfirmModalProps {
  show: boolean;
  title: string;
  message: string;
  warning?: string;
  confirmText?: string;
  cancelText?: string;
  onClose: () => void;
  onConfirm: () => void;
  isLoading?: boolean;
  variant?: 'danger' | 'warning' | 'primary';
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({
  show,
  title,
  message,
  warning,
  confirmText = 'Confirmar',
  cancelText = 'Cancel·lar',
  onClose,
  onConfirm,
  isLoading = false,
  variant = 'warning',
}) => {
  const getIconClass = () => {
    switch (variant) {
      case 'danger':
        return 'bi bi-exclamation-triangle text-danger';
      case 'warning':
        return 'bi bi-exclamation-circle text-warning';
      case 'primary':
        return 'bi bi-question-circle text-primary';
      default:
        return 'bi bi-exclamation-circle text-warning';
    }
  };

  const getButtonClass = () => {
    switch (variant) {
      case 'danger':
        return 'btn-danger';
      case 'warning':
        return 'btn-warning';
      case 'primary':
        return 'btn-primary';
      default:
        return 'btn-warning';
    }
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      className="confirm-modal"
      backdrop="static"
    >
      <Modal.Header className="confirm-modal-header">
        <Modal.Title className="confirm-modal-title">{title}</Modal.Title>
        <button
          type="button"
          className="btn-close"
          onClick={onClose}
          aria-label="Tancar"
          disabled={isLoading}
        ></button>
      </Modal.Header>

      <Modal.Body className="confirm-modal-body">
        <div className="confirm-modal-icon">
          <i className={getIconClass()}></i>
        </div>

        <p className="confirm-modal-message">{message}</p>

        {warning && <p className="confirm-modal-warning">{warning}</p>}
      </Modal.Body>

      <Modal.Footer className="confirm-modal-footer">
        <Button
          title={cancelText}
          variant="outline"
          onClick={onClose}
          disabled={isLoading}
        />
        <Button
          title={isLoading ? 'Processant...' : confirmText}
          onClick={onConfirm}
          isLoading={isLoading}
          disabled={isLoading}
          className={getButtonClass()}
        />
      </Modal.Footer>
    </Modal>
  );
};

export default ConfirmModal;
