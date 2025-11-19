// src/components/suppliers/SupplierList/ActionDropdown.tsx

import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './ActionDropdown.scss';

interface ActionDropdownProps {
  supplierUuid: string;
  onDelete: () => void;
}

const ActionDropdown: React.FC<ActionDropdownProps> = ({
  supplierUuid,
  onDelete,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handleViewDetail = () => {
    navigate(`/suppliers/${supplierUuid}`);
    setIsOpen(false);
  };

  const handleEdit = () => {
    navigate(`/suppliers/edit/${supplierUuid}`);
    setIsOpen(false);
  };

  const handleViewProducts = () => {
    navigate(`/products?supplier=${supplierUuid}`);
    setIsOpen(false);
  };

  /* const handleAddProduct = () => {
    navigate(`/products/new`);
    setIsOpen(false);
  };
 */
  const handleNewOrder = () => {
    navigate(`/orders/new?supplier=${supplierUuid}`);
    setIsOpen(false);
  };

  const handleDelete = () => {
    setIsOpen(false);
    onDelete();
  };

  return (
    <div className="action-dropdown" ref={dropdownRef}>
      <button
        className="action-dropdown-toggle"
        onClick={() => setIsOpen(!isOpen)}
        aria-label="Menú d'accions"
      >
        <i className="bi bi-three-dots-vertical"></i>
      </button>

      {isOpen && (
        <div className="action-dropdown-menu">
          <button className="action-dropdown-item" onClick={handleViewDetail}>
            <i className="bi bi-eye"></i>
            Veure detall
          </button>

          <button className="action-dropdown-item" onClick={handleEdit}>
            <i className="bi bi-pencil"></i>
            Editar
          </button>

          <button className="action-dropdown-item" onClick={handleViewProducts}>
            <i className="bi bi-box-seam"></i>
            Veure productes
          </button>

          {/* <button className="action-dropdown-item" onClick={handleAddProduct}>
            <i className="bi bi-plus-circle"></i>
            Afegir producte
          </button> */}

          <button className="action-dropdown-item" onClick={handleNewOrder}>
            <i className="bi bi-cart"></i>
            Crear comanda
          </button>

          <div className="action-dropdown-divider"></div>

          <button
            className="action-dropdown-item action-dropdown-item-danger"
            onClick={handleDelete}
          >
            <i className="bi bi-trash"></i>
            Eliminar
          </button>
        </div>
      )}
    </div>
  );
};

export default ActionDropdown;
