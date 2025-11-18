import type { Product } from '../../../../types/product.types';
import ActionDropdown from '../ActionDropdown/ActionDropdown';
import './ProductCard.scss';

interface CardFieldConfig {
  key: keyof Product | 'supplier' | 'actions';
  label: string;
  render?: (product: Product) => React.ReactNode;
  show?: boolean;
}

interface ProductCardProps {
  products: Product[];
  fields?: CardFieldConfig[];
  onDelete?: (productUid: string, productName: string) => void;
  onProductClick?: (product: Product) => void;
  selectable?: boolean;
  showActions?: boolean;
  selectedProducts?: Set<string | undefined>;
}

const defaultFields: CardFieldConfig[] = [
  {
    key: 'category',
    label: 'Categoria',
    show: true,
  },
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
];

const ProductCard: React.FC<ProductCardProps> = ({
  products,
  fields = defaultFields,
  onDelete,
  onProductClick,
  selectable = false,
  showActions = true,
  selectedProducts = new Set(),
}) => {
  const visibleFields = fields.filter(field => field.show !== false);

  const handleCardClick = (product: Product) => {
    if (selectable && onProductClick) {
      onProductClick(product);
    }
  };

  if (products.length === 0) {
    return (
      <div className="product-card-empty">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat productes</p>
      </div>
    );
  }

  return (
    <div className="product-card-container">
      {products.map(product => {
        const isSelected = selectedProducts.has(product.uuid);
        const cardClasses = [
          'product-card',
          selectable ? 'selectable-card' : '',
          isSelected ? 'selected' : '',
        ]
          .filter(Boolean)
          .join(' ');
        return (
          <div
            key={product.uuid}
            className={cardClasses}
            onClick={() => handleCardClick(product)}
          >
            <div className="product-card-header">
              <h3 className="product-card-name">{product.name}</h3>
              {showActions && onDelete && (
                <ActionDropdown
                  productUuid={product.uuid}
                  onDelete={() => onDelete(product.uuid, product.name)}
                />
              )}
            </div>

            <div className="product-card-body">
              {visibleFields.map(field => (
                <div key={field.key} className="product-card-row">
                  <span className="product-card-label">{field.label}</span>
                  <span className="product-card-value">
                    {field.render
                      ? field.render(product)
                      : product[field.key as keyof Product]?.toString() || '-'}
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

export default ProductCard;
