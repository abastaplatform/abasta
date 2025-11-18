import { useState, useEffect } from 'react';
import './OrderCreateScrollButton.scss';

interface OrderCreateScrollButtonProps {
  itemsCount: number;
  totalAmount: number;
  targetId: string;
}

const OrderCreateScrollButton: React.FC<OrderCreateScrollButtonProps> = ({
  itemsCount,
  totalAmount,
  targetId,
}) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const checkScroll = () => {
      const summaryElement = document.getElementById(targetId);
      if (!summaryElement) return;

      const rect = summaryElement.getBoundingClientRect();
      const isOutOfView = rect.top > window.innerHeight || rect.bottom < 0;
      setIsVisible(isOutOfView && itemsCount > 0);
    };

    checkScroll();
    window.addEventListener('scroll', checkScroll);
    window.addEventListener('resize', checkScroll);

    return () => {
      window.removeEventListener('scroll', checkScroll);
      window.removeEventListener('resize', checkScroll);
    };
  }, [targetId, itemsCount]);

  const handleClick = () => {
    const summaryElement = document.getElementById(targetId);
    if (summaryElement) {
      summaryElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  };

  if (!isVisible) return null;

  return (
    <button
      type="button"
      className="order-scroll-button"
      onClick={handleClick}
      aria-label="Veure resum de comanda"
    >
      <div className="order-scroll-content">
        <div className="order-scroll-info">
          <span className="order-scroll-items">
            {itemsCount} {itemsCount === 1 ? 'producte' : 'productes'}
          </span>
          <span className="order-scroll-total">{totalAmount.toFixed(2)}€</span>
        </div>
        <i className="bi bi-arrow-down-circle"></i>
      </div>
    </button>
  );
};

export default OrderCreateScrollButton;
