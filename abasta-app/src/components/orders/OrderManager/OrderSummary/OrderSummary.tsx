import type { OrderItem } from '../../../../types/order.types';
import Button from '../../../common/Button/Button';

import './OrderSummary.scss';

interface OrderSummaryProps {
  supplierName: string;
  items: OrderItem[];
  notes: string;
  onNotesChange: (notes: string) => void;
  onUpdateItem: (productUuid: string, updates: Partial<OrderItem>) => void;
  onRemoveItem: (productUuid: string) => void;
  onSubmit: () => void;
  onCancel: () => void;
  isSubmitting?: boolean;
  canSend?: boolean;
  onSave?: () => void;
}

const OrderSummary: React.FC<OrderSummaryProps> = ({
  supplierName,
  items,
  notes,
  onNotesChange,
  onUpdateItem,
  onRemoveItem,
  onSubmit,
  onCancel,
  isSubmitting = false,
  canSend = true,
  onSave,
}) => {
  const total = items.reduce((sum, item) => sum + item.total, 0);

  const handleIncrement = (productUuid: string, currentQuantity: number) => {
    onUpdateItem(productUuid, { quantity: currentQuantity + 1 });
  };

  const handleDecrement = (productUuid: string, currentQuantity: number) => {
    if (currentQuantity > 1) {
      onUpdateItem(productUuid, { quantity: currentQuantity - 1 });
    }
  };

  const sendButtonTitle = !canSend
    ? 'Enviar'
    : isSubmitting
    ? 'Enviant...'
    : 'Enviar';

  return (
    <div className="order-summary card">
      <div className="card-body">
        <h5 className="card-title mb-3">Resum de comanda</h5>

        <div className="supplier-info mb-3">
          <strong>{supplierName}</strong>
        </div>

        <div className="order-items mb-3">
          {items.length === 0 ? (
            <p className="text-muted text-center py-3">
              No hi ha productes seleccionats
            </p>
          ) : (
            items.map(item => (
              <div key={item.productUuid} className="order-item mb-3">
                <div className="d-flex justify-content-between align-items-start mb-2">
                  <strong className="item-name">{item.productName}</strong>
                  <button
                    type="button"
                    className="btn btn-sm btn-link"
                    onClick={() => onRemoveItem(item.productUuid!)}
                    aria-label="Eliminar producte"
                    disabled={isSubmitting}
                  >
                    <i className="bi bi-trash fs-5"></i>
                  </button>
                </div>

                <div className="d-flex align-items-center justify-content-between mb-2">
                  <div className="quantity-controls">
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() =>
                        handleDecrement(item.productUuid!, item.quantity)
                      }
                      disabled={item.quantity <= 1 || isSubmitting}
                    >
                      -
                    </button>
                    <span className="quantity">{item.quantity}</span>
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() =>
                        handleIncrement(item.productUuid!, item.quantity)
                      }
                      disabled={isSubmitting}
                    >
                      +
                    </button>
                  </div>
                  <span className="fw-bold">{item.total.toFixed(2)}€</span>
                </div>

                <input
                  type="text"
                  className="form-control form-control-sm"
                  placeholder="Nota del producte"
                  value={item.notes || ''}
                  onChange={e =>
                    onUpdateItem(item.productUuid!, {
                      notes: e.target.value,
                    })
                  }
                  disabled={isSubmitting}
                />
              </div>
            ))
          )}
        </div>

        <hr />

        <div className="total-section mb-3">
          <div className="d-flex justify-content-between align-items-center">
            <strong>TOTAL</strong>
            <strong className="fs-4">{total.toFixed(2)}€</strong>
          </div>
        </div>

        <div className="mb-3">
          <label className="form-label">Notes</label>
          <textarea
            className="form-control"
            rows={3}
            value={notes}
            onChange={e => onNotesChange(e.target.value)}
            placeholder="Notes addicionals per a la comanda..."
            disabled={isSubmitting}
          />
        </div>

        <div className="d-grid gap-2">
          <Button
            title={sendButtonTitle}
            onClick={onSubmit}
            disabled={items.length === 0 || isSubmitting || !canSend}
          />
          {onSave && (
            <Button
              title="Guardar"
              onClick={onSave}
              variant="outline"
              disabled={items.length === 0 || isSubmitting || !canSend}
            />
          )}
          <Button
            title="Cancel·lar"
            onClick={onCancel}
            variant="outline"
            disabled={isSubmitting}
          />
        </div>
      </div>
    </div>
  );
};

export default OrderSummary;
