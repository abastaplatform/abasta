import { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import type { OrderStatus } from '../../../../types/order.types';
import './ActionDropdown.scss';

interface ActionDropdownProps {
  orderUuid: string;
  status: OrderStatus;
  onDelete: () => void;
}

const ActionDropdown: React.FC<ActionDropdownProps> = ({
  orderUuid,
  status,
  onDelete,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState<{ top: number; left: number } | null>(
    null
  );

  const wrapperRef = useRef<HTMLDivElement>(null);
  const buttonRef = useRef<HTMLButtonElement>(null);
  const navigate = useNavigate();

  const canEdit = status === 'PENDING';

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        wrapperRef.current &&
        !wrapperRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [isOpen]);

  const toggleMenu = () => {
    if (!isOpen) {
      if (buttonRef.current) {
        const rect = buttonRef.current.getBoundingClientRect();
        const MENU_WIDTH = 200; 

        const top = rect.bottom + 4;
        const left = rect.right - MENU_WIDTH;

        setMenuPosition({
          top,
          left: Math.max(left, 8),
        });
      }
      setIsOpen(true);
    } else {
      setIsOpen(false);
    }
  };

  const handleViewDetail = () => {
    navigate(`/orders/${orderUuid}`);
    setIsOpen(false);
  };

  const handleEdit = () => {
    if (!canEdit) return;
    navigate(`/orders/edit/${orderUuid}`);
    setIsOpen(false);
  };

  const handleDuplicate = () => {
    navigate(`/orders/new?duplicate=${orderUuid}`);
    setIsOpen(false);
  };

  const handleDeleteClick = () => {
    setIsOpen(false);
    onDelete();
  };

  return (
    <div className="action-dropdown" ref={wrapperRef}>
      <button
        ref={buttonRef}
        className="action-dropdown-toggle"
        onClick={toggleMenu}
        aria-label="Menú d'accions"
        type="button"
      >
        <i className="bi bi-three-dots-vertical"></i>
      </button>

      {isOpen && menuPosition && (
        <div
          className="action-dropdown-menu"
          style={{
            position: 'fixed',
            top: menuPosition.top,
            left: menuPosition.left,
          }}
        >
          <button
            className="action-dropdown-item"
            type="button"
            onClick={handleViewDetail}
          >
            <i className="bi bi-eye"></i>
            Veure detall
          </button>

          {canEdit && (
            <button
              className="action-dropdown-item"
              type="button"
              onClick={handleEdit}
            >
              <i className="bi bi-pencil"></i>
              Editar
            </button>
          )}

          <button
            className="action-dropdown-item"
            type="button"
            onClick={handleDuplicate}
          >
            <i className="bi bi-files"></i>
            Duplicar
          </button>

          <div className="action-dropdown-divider"></div>

          <button
            className="action-dropdown-item action-dropdown-item-danger"
            type="button"
            onClick={handleDeleteClick}
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
