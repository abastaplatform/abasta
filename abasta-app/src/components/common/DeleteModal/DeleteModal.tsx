import { Modal } from 'react-bootstrap';
import Button from '../Button/Button';
import './DeleteModal.scss';

export type DeleteEntityType = 'proveïdor' | 'producte' | 'comanda';

interface DeleteModalProps {
  show: boolean;
  entityType: DeleteEntityType;
  itemName: string;
  onClose: () => void;
  onConfirm: () => void;
  isDeleting?: boolean;
  customWarning?: string;
}

const DeleteModal: React.FC<DeleteModalProps> = ({
  show,
  entityType,
  itemName,
  onClose,
  onConfirm,
  isDeleting = false,
  customWarning,
}) => {
  const getArticle = (type: DeleteEntityType): string => {
    const masculineTypes: DeleteEntityType[] = ['proveïdor', 'producte'];
    return masculineTypes.includes(type) ? 'el' : 'la';
  };

  const getTitle = (): string => {
    return `Eliminar ${entityType}`;
  };

  const getQuestion = (): string => {
    const article = getArticle(entityType);
    return `Estàs segur que vols eliminar ${article} ${entityType}`;
  };

  const getWarning = (): string => {
    return customWarning || 'Aquesta acció no es pot desfer';
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      className="delete-modal"
      backdrop="static"
    >
      <Modal.Header className="delete-modal-header">
        <Modal.Title className="delete-modal-title">{getTitle()}</Modal.Title>
        <button
          type="button"
          className="btn-close"
          onClick={onClose}
          aria-label="Tancar"
          disabled={isDeleting}
        ></button>
      </Modal.Header>

      <Modal.Body className="delete-modal-body">
        <div className="delete-modal-icon">
          <i className="bi bi-exclamation-triangle"></i>
        </div>

        <p className="delete-modal-question">
          {getQuestion()} <strong>{itemName}</strong>?
        </p>

        <p className="delete-modal-warning">{getWarning()}</p>
      </Modal.Body>

      <Modal.Footer className="delete-modal-footer">
        <Button
          title="Cancel·lar"
          variant="outline"
          onClick={onClose}
          disabled={isDeleting}
        />
        <Button
          title={isDeleting ? 'Eliminant...' : 'Eliminar'}
          onClick={onConfirm}
          isLoading={isDeleting}
          disabled={isDeleting}
          className="btn-danger"
        />
      </Modal.Footer>
    </Modal>
  );
};

export default DeleteModal;
