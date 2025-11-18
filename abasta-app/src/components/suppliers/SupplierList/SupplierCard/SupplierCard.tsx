import type { Supplier } from '../../../../types/supplier.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './SupplierCard.scss';

interface SupplierCardProps {
  suppliers: Supplier[];
  onDelete: (productUid: string, supplierName: string) => void;
}

const SupplierCard: React.FC<SupplierCardProps> = ({ suppliers, onDelete }) => {
  if (suppliers.length === 0) {
    return (
      <div className="supplier-card-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat proveïdors</p>
      </div>
    );
  }

  return (
    <div className="supplier-card-container">
      {suppliers.map(supplier => (
        <div key={supplier.uuid} className="supplier-card">
          <div className="supplier-card-header">
            <h3 className="supplier-card-name">{supplier.name}</h3>
            <ActionDropdown
              supplierUuid={supplier.uuid}
              onDelete={() => onDelete(supplier.uuid, supplier.name)}
            />
          </div>

          <div className="supplier-card-body">
            <div className="supplier-card-row">
              <span className="supplier-card-label">Contacte</span>
              <span className="supplier-card-value">
                {supplier.contactName}
              </span>
            </div>

            <div className="supplier-card-row">
              <span className="supplier-card-label">Correu electrònic</span>
              <span className="supplier-card-value supplier-card-email">
                {supplier.email}
              </span>
            </div>

            <div className="supplier-card-row">
              <span className="supplier-card-label">Telèfon</span>
              <span className="supplier-card-value">{supplier.phone}</span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default SupplierCard;
