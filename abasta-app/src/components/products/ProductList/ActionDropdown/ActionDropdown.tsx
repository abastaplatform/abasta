import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { useNavigate } from 'react-router-dom';
import './ActionDropdown.scss';

interface ActionDropdownProps {
  productUuid: string;
  onDelete: () => void;
}

const ActionDropdown: React.FC<ActionDropdownProps> = ({
  productUuid,
  onDelete,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState<{
    top: number;
    left: number;
  } | null>(null);

  const triggerRef = useRef<HTMLDivElement | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);
  const navigate = useNavigate();

  // Cerrar al hacer clic fuera (botón + menú)
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      const target = event.target as Node;

      if (
        triggerRef.current?.contains(target) ||
        menuRef.current?.contains(target)
      ) {
        return;
      }

      setIsOpen(false);
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const openMenuWithPosition = () => {
    if (!triggerRef.current || typeof window === 'undefined') return;

    const rect = triggerRef.current.getBoundingClientRect();

    setMenuPosition({
      top: rect.bottom + window.scrollY,
      left: rect.right + window.scrollX - 180, // 180 = ancho aprox. del menú
    });
    setIsOpen(true);
  };

  const handleToggle = () => {
    setIsOpen(prev => {
      const next = !prev;
      if (next) {
        openMenuWithPosition();
      }
      return next;
    });
  };

  const handleViewDetail = () => {
    navigate(`/products/${productUuid}`);
    setIsOpen(false);
  };

  const handleEdit = () => {
    navigate(`/products/edit/${productUuid}`);
    setIsOpen(false);
  };

  const handleDelete = () => {
    setIsOpen(false);
    onDelete();
  };

  const renderMenu = () => {
    if (!isOpen || !menuPosition || typeof document === 'undefined') return null;

    return createPortal(
      <div
        ref={menuRef}
        className="action-dropdown-menu"
        style={{
          position: 'absolute',
          top: menuPosition.top,
          left: menuPosition.left,
        }}
      >
        <button className="action-dropdown-item" onClick={handleViewDetail}>
          <i className="bi bi-eye"></i>
          Veure detall
        </button>

        <button className="action-dropdown-item" onClick={handleEdit}>
          <i className="bi bi-pencil"></i>
          Editar
        </button>

        <div className="action-dropdown-divider"></div>

        <button
          className="action-dropdown-item action-dropdown-item-danger"
          onClick={handleDelete}
        >
          <i className="bi bi-trash"></i>
          Eliminar
        </button>
      </div>,
      document.body
    );
  };

  return (
    <>
      <div className="action-dropdown" ref={triggerRef}>
        <button
          className="action-dropdown-toggle"
          onClick={handleToggle}
          aria-label="Menú d'accions"
          type="button"
        >
          <i className="bi bi-three-dots-vertical"></i>
        </button>
      </div>

      {renderMenu()}
    </>
  );
};

export default ActionDropdown;
