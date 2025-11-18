import { useState } from 'react';
import { Modal } from 'react-bootstrap';
import Button from '../../common/Button/Button';
import './SendOrderModal.scss';

interface SendOrderModalProps {
  show: boolean;
  providerName: string;
  totalPrice: string;
  itemsCount: number;
  email?: string;
  phone?: string;
  onClose: () => void;
  onSend: (method: 'email' | 'whatsapp') => void;
}

const SendOrderModal: React.FC<SendOrderModalProps> = ({
  show,
  providerName,
  totalPrice,
  itemsCount,
  email = '',
  phone = '',
  onClose,
  onSend,
}) => {
  const [selected, setSelected] = useState<'email' | 'whatsapp'>('email');

  const handleSendClick = () => {
    onSend(selected);
  };

  return (
    <Modal
      show={show}
      onHide={onClose}
      centered
      className="send-order-modal"
      size="lg"
    >
      <Modal.Header className="modal-header-custom">
        <Modal.Title className="modal-title-custom">Enviar comanda</Modal.Title>

        <button type="button" className="btn-close" onClick={onClose}></button>
      </Modal.Header>

      <Modal.Body>
        <div className="send-order-info mb-3">
          <div>
            Proveïdor: <strong>{providerName}</strong>
          </div>
          <div>
            <strong>{totalPrice}</strong> ({itemsCount}{' '}
            {itemsCount === 1 ? 'producte' : 'productes'})
          </div>
        </div>

        <p className="send-order-question">Com vols enviar la comanda?</p>

        <div className="row g-3 mt-2">
          <div className="col-12 col-md-6">
            <div
              className={`send-order-card ${selected === 'email' ? 'selected' : ''}`}
              role="button"
              onClick={() => setSelected('email')}
            >
              <div className="top-row d-flex align-items-center gap-2">
                <i className="bi bi-envelope-fill fs-4 text-primary"></i>
                <span className="fw-bold">Email</span>
              </div>

              <div className="bottom-row">
                <small className="text-muted">{email}</small>
              </div>
            </div>
          </div>

          <div className="col-12 col-md-6">
            <div
              className={`send-order-card ${selected === 'whatsapp' ? 'selected' : ''}`}
              role="button"
              onClick={() => setSelected('whatsapp')}
            >
              <div className="top-row d-flex align-items-center gap-2">
                <i className="bi bi-whatsapp fs-4 text-success"></i>
                <span className="fw-bold">Whatsapp</span>
              </div>

              <div className="bottom-row">
                <small className="text-muted">{phone}</small>
              </div>
            </div>
          </div>
        </div>
      </Modal.Body>

      <Modal.Footer className="send-order-footer w-100">
        <div className="row w-100 g-2 flex-column-reverse flex-md-row">
          <div className="col-12 col-md-6 d-flex">
            <Button
              title="Cancel·lar"
              variant="outline"
              onClick={onClose}
              className="w-100"
            />
          </div>

          <div className="col-12 col-md-6 d-flex">
            <Button
              title="Enviar"
              onClick={handleSendClick}
              className="w-100"
            />
          </div>
        </div>
      </Modal.Footer>
    </Modal>
  );
};

export default SendOrderModal;
