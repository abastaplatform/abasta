import type { Supplier } from '../../../../types/supplier.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './SupplierTable.scss';

interface SupplierTableProps {
  suppliers: Supplier[];
  onDelete: (supplierId: string, supplierName: string) => void;
}

const SupplierTable: React.FC<SupplierTableProps> = ({
  suppliers,
  onDelete,
}) => {
  if (suppliers.length === 0) {
    return (
      <div className="supplier-table-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat proveïdors</p>
      </div>
    );
  }

  return (
    <div className="supplier-table-container">
      <table className="supplier-table">
        <thead>
          <tr>
            <th>Nom</th>
            <th>Contacte</th>
            <th>Correu electrònic</th>
            <th>Telèfon</th>
            <th className="text-end">Accions</th>
          </tr>
        </thead>
        <tbody>
          {suppliers.map(supplier => (
            <tr key={supplier.uuid}>
              <td className="supplier-name">{supplier.name}</td>
              <td>{supplier.contactName}</td>
              <td>{supplier.email}</td>
              <td>{supplier.phone}</td>
              <td className="text-end">
                <ActionDropdown
                  supplierUuid={supplier.uuid}
                  onDelete={() => onDelete(supplier.uuid, supplier.name)}
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default SupplierTable;
