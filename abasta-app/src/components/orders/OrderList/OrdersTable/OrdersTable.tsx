// src/components/orders/OrderList/OrdersTable/OrdersTable.tsx
import type { OrderResponseData } from '../../../../types/order.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';

import './OrdersTable.scss';

interface ColumnConfig {
  key: keyof OrderResponseData | 'supplier' | 'actions';
  label: string;
  render?: (order: OrderResponseData) => React.ReactNode;
  className?: string;
  show?: boolean;
  sortKey?: string;
}

interface OrdersTableProps {
  orders: OrderResponseData[];
  columns?: ColumnConfig[];
  onDelete?: (orderUuid: string, orderName: string) => void;
  onOrderClick?: (order: OrderResponseData) => void;
  selectable?: boolean;
  showActions?: boolean;
  selectedOrders?: Set<string | undefined>;
  supplierNames?: Record<string, string>;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  onSortChange?: (sortBy: string) => void;
}

const defaultColumns: ColumnConfig[] = [
  {
    key: 'supplier',
    label: 'Proveïdor',
    render: () => '-',
    show: true,
    sortKey: 'supplierName',
  },
  { key: 'name', label: 'Nom', show: true, sortKey: 'name' },
  {
    key: 'status',
    label: 'Estat',
    render: order => order.status,
    show: true,
    sortKey: 'status',
  },
  {
    key: 'totalAmount',
    label: 'Total',
    render: order => `${order.totalAmount.toFixed(2)}€`,
    show: true,
    sortKey: 'totalAmount',
  },
  {
    key: 'deliveryDate',
    label: 'Data',
    render: order =>
      order.deliveryDate
        ? new Date(order.deliveryDate).toLocaleDateString('ca-ES')
        : new Date(order.createdAt).toLocaleDateString('ca-ES'),
    show: true,
    sortKey: 'deliveryDate',
  },
  {
    key: 'actions',
    label: 'Accions',
    className: 'text-end',
    show: true,
  },
];

const OrdersTable: React.FC<OrdersTableProps> = ({
  orders,
  columns = defaultColumns,
  onDelete,
  onOrderClick,
  selectable = false,
  showActions = true,
  selectedOrders = new Set(),
  supplierNames = {},
  sortBy,
  sortDir = 'asc',
  onSortChange,
}) => {
  const visibleColumns = columns.filter(col => col.show !== false);

  const handleRowClick = (order: OrderResponseData) => {
    if (selectable && onOrderClick) {
      onOrderClick(order);
    }
  };

  const handleHeaderClick = (col: ColumnConfig) => {
    if (!col.sortKey || !onSortChange) return;
    onSortChange(col.sortKey);
  };

  if (orders.length === 0) {
    return (
      <div className="product-table-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat comandes</p>
      </div>
    );
  }

  return (
    <div className="product-table-container">
      <table className="product-table">
        <thead>
          <tr>
            {visibleColumns.map(col => (
              <th
                key={col.key}
                className={col.className}
                onClick={() => handleHeaderClick(col)}
                style={col.sortKey ? { cursor: 'pointer' } : undefined}
              >
                {col.label}
                {col.sortKey && sortBy === col.sortKey && (
                  <span style={{ marginLeft: 4 }}>
                    {sortDir === 'asc' ? '▲' : '▼'}
                  </span>
                )}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {orders.map(order => {
            const isSelected = selectedOrders.has(order.uuid);
            const rowClasses = [
              selectable ? 'selectable-row' : '',
              isSelected ? 'selected' : '',
            ]
              .filter(Boolean)
              .join(' ');

            return (
              <tr
                key={order.uuid}
                className={rowClasses}
                onClick={() => handleRowClick(order)}
              >
                {visibleColumns.map(col => {
                  if (col.key === 'actions' && showActions) {
                    return (
                      <td key={col.key} className={col.className}>
                        {onDelete && (
                          <ActionDropdown
                            orderUuid={order.uuid}
                            status={order.status}
                            onDelete={() => onDelete(order.uuid, order.name)}
                          />
                        )}
                      </td>
                    );
                  }

                  if (col.key === 'actions' && !showActions) {
                    return null;
                  }

                  if (col.key === 'supplier') {
                    const name = supplierNames[order.supplierUuid] ?? '-';
                    return (
                      <td key={col.key} className={col.className}>
                        {name}
                      </td>
                    );
                  }

                  return (
                    <td key={col.key} className={col.className}>
                      {col.render
                        ? col.render(order)
                        : (order[col.key as keyof OrderResponseData] as
                            | string
                            | number
                            | null
                            | undefined)?.toString() || '-'}
                    </td>
                  );
                })}
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default OrdersTable;
