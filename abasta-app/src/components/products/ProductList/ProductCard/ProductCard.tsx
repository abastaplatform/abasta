import type { Product } from '../../../../types/product.types';
import ActionDropdown from '../ActionDropDown/ActionDropDown';
import './ProductCard.scss';

interface ProductCardProps {
  products: Product[];
  onDelete: (productId: string, productName: string) => void;
}

const ProductCard: React.FC<ProductCardProps> = ({ products, onDelete }) => {
  if (products.length === 0) {
    return (
      <div className="product-card-empty d-md-none">
        <i className="bi bi-inbox"></i>
        <p>No s'han trobat productes</p>
      </div>
    );
  }

  return (
    <div className="product-card-container d-md-none">
      {products.map(product => (
        <div key={product.uuid} className="product-card">
          <div className="product-card-header">
            <h3 className="product-card-name">{product.name}</h3>
            <ActionDropdown
              itemUuid={product.uuid}
              onDelete={() => onDelete(product.uuid, product.name)}
            />
          </div>

          <div className="product-card-body">
            <div className="product-card-row">
              <span className="product-card-label">Proveïdor</span>
              <span className="product-card-value">
                {product.supplier?.name || '-'}
              </span>
            </div>

            <div className="product-card-row">
              <span className="product-card-label">Categoria</span>
              <span className="product-card-value">
                {product.category || '-'}
              </span>
            </div>

            <div className="product-card-row">
              <span className="product-card-label">Preu</span>
              <span className="product-card-value">{product.price} €</span>
            </div>

            <div className="product-card-row">
              <span className="product-card-label">Unitat</span>
              <span className="product-card-value">
                {product.volume ? `${product.volume} ` : ''}
                {product.unit || ''}
              </span>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default ProductCard;
