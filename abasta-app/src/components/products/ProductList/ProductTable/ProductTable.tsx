import type { Product } from '../../../../types/product.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';

import './ProductTable.scss';

interface ColumnConfig {
  key: keyof Product | 'supplier' | 'actions';
  label: string;
  render?: (product: Product) => React.ReactNode;
  className?: string;
  show?: boolean;
}

interface ProductTableProps {
  products: Product[];
  columns?: ColumnConfig[];
  onDelete?: (productId: string, productName: string) => void;
  onProductClick?: (productId: string) => void;
  selectable?: boolean;
  showActions?: boolean;
  selectedProducts?: Set<string>;
}

const defaultColumns: ColumnConfig[] = [
  { key: 'name', label: 'Nom', show: true },
  { key: 'category', label: 'Category', show: true },
  {
    key: 'volume',
    label: 'Volum',
    render: product => `${product.volume}${product.unit}`,
    show: true,
  },
  {
    key: 'price',
    label: 'Preu',
    render: product => `${product.price}€`,
    show: true,
  },
  {
    key: 'supplier',
    label: 'Proveïdor',
    render: product => product.supplier.name,
    show: true,
  },
  {
    key: 'actions',
    label: 'Accions',
    className: 'text-end',
    show: true,
  },
];

const ProductTable: React.FC<ProductTableProps> = ({
  products,
  columns = defaultColumns,
  onDelete,
  onProductClick,
  selectable = false,
  showActions = true,
  selectedProducts = new Set(),
}) => {
  const visibleColumns = columns.filter(col => col.show !== false);

  const handleRowClick = (product: Product) => {
    if (selectable && onProductClick) {
      onProductClick(product.uuid);
    }
  };

  if (products.length === 0) {
    return (
      <div className="product-table-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat productes</p>
      </div>
    );
  }

  return (
    <div className="product-table-container">
      <table className="product-table">
        <thead>
          <tr>
            {visibleColumns.map(col => (
              <th key={col.key} className={col.className}>
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {products.map(product => {
            const isSelected = selectedProducts.has(product.uuid);
            const rowClasses = [
              selectable ? 'selectable-row' : '',
              isSelected ? 'selected' : '',
            ]
              .filter(Boolean)
              .join(' ');

            return (
              <tr
                key={product.uuid}
                className={rowClasses}
                onClick={() => handleRowClick(product)}
              >
                {visibleColumns.map(col => {
                  if (col.key === 'actions' && showActions) {
                    return (
                      <td key={col.key} className={col.className}>
                        {onDelete && (
                          <ActionDropdown
                            productUuid={product.uuid}
                            onDelete={() =>
                              onDelete(product.uuid, product.name)
                            }
                          />
                        )}
                      </td>
                    );
                  }

                  if (col.key === 'actions' && !showActions) {
                    return null;
                  }

                  return (
                    <td key={col.key} className={col.className}>
                      {col.render
                        ? col.render(product)
                        : product[col.key as keyof Product]?.toString() || '-'}
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

export default ProductTable;
