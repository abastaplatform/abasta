import type { OrderResponseData } from '../../../../types/order.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './OrderCard.scss';

interface CardFieldConfig {
  key: keyof OrderResponseData | 'actions';
  label: string;
  render?: (order: OrderResponseData) => React.ReactNode;
  show?: boolean;
}

interface OrderCardProps {
  orders: OrderResponseData[];
  fields?: CardFieldConfig[];
  onDelete?: (orderUuid: string, orderName: string) => void;
  onOrderClick?: (order: OrderResponseData) => void;
  selectable?: boolean;
  showActions?: boolean;
  selectedOrders?: Set<string | undefined>;
  supplierNames?: Record<string, string>; 
}

const defaultFields: CardFieldConfig[] = [
  {
    key: 'name',
    label: 'Nom',
    render: order => order.name,
    show: true,
  },
  {
    key: 'status',
    label: 'Estat',
    render: order => order.status,
    show: true,
  },
  {
    key: 'totalAmount',
    label: 'Total',
    render: order => `${order.totalAmount.toFixed(2)}€`,
    show: true,
  },
  {
    key: 'deliveryDate',
    label: 'Data',
    render: order =>
      order.deliveryDate
        ? new Date(order.deliveryDate).toLocaleDateString('ca-ES')
        : new Date(order.createdAt).toLocaleDateString('ca-ES'),
    show: true,
  },
];

const OrderCard: React.FC<OrderCardProps> = ({
  orders,
  fields = defaultFields,
  onDelete,
  onOrderClick,
  selectable = false,
  showActions = true,
  selectedOrders = new Set(),
  supplierNames = {},
}) => {
  const visibleFields = fields.filter(field => field.show !== false);

  const handleCardClick = (order: OrderResponseData) => {
    if (selectable && onOrderClick) {
      onOrderClick(order);
    }
  };

  if (orders.length === 0) {
    return (
      <div className="order-card-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat comandes</p>
      </div>
    );
  }

  return (
    <div className="order-card-container">
      {orders.map(order => {
        const isSelected = selectedOrders.has(order.uuid);
        const cardClasses = [
          'order-card',
          selectable ? 'selectable-card' : '',
          isSelected ? 'selected' : '',
        ]
          .filter(Boolean)
          .join(' ');

        const supplierName = supplierNames[order.supplierUuid] ?? '-';

        return (
          <div
            key={order.uuid}
            className={cardClasses}
            onClick={() => handleCardClick(order)}
          >
            <div className="order-card-header">
              <h3 className="order-card-name">{supplierName}</h3>
              {showActions && onDelete && (
                <ActionDropdown
                  orderUuid={order.uuid}
                  status={order.status}
                  onDelete={() => onDelete(order.uuid, order.name)}
                />
              )}
            </div>

            <div className="order-card-body">
              {visibleFields.map(field => (
                <div key={field.key as string} className="order-card-row">
                  <span className="order-card-label">{field.label}</span>
                  <span className="order-card-value">
                    {field.render
                      ? field.render(order)
                      : (order[field.key as keyof OrderResponseData] as
                          | string
                          | number
                          | null
                          | undefined)?.toString() || '-'}
                  </span>
                </div>
              ))}
            </div>
          </div>
        );
      })}
    </div>
  );
};

export default OrderCard;
